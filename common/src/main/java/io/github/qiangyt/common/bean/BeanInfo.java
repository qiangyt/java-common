/*
 * io.github.qiangyt:qiangyt-common - Common library by Yiting Qiang
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.qiangyt.common.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.LockCloser;
import jakarta.annotation.Nonnull;
import lombok.Getter;

public class BeanInfo<T> {

    @Getter
    @Nonnull
    final T instance;

    @Getter
    @Nonnull
    final String primaryName;

    @Nonnull
    final Set<String> names = new HashSet<>();

    @Getter
    volatile boolean inited;

    @Nonnull
    final Logger log;

    @Nonnull
    LinkedHashMap<String, BeanInfo<?>> dependsOn = new LinkedHashMap<>();

    @Nonnull
    LinkedHashMap<String, BeanInfo<?>> dependedBy = new LinkedHashMap<>();

    @Getter
    WrapperBean<T> wrapper;

    @Getter
    @Nonnull
    final Container container;

    @Nonnull
    final ReentrantReadWriteLock lock;

    @SuppressWarnings("unchecked")
    public BeanInfo(@Nonnull Container container, @Nonnull T instanceOrWrapper, @Nonnull String... names) {
        if (names.length == 0) {
            throw new BadStateException("names cannot be empty");
        }

        boolean isWrapper = (instanceOrWrapper instanceof WrapperBean);
        if (isWrapper) {
            this.wrapper = (WrapperBean<T>) instanceOrWrapper;
            this.instance = this.wrapper.getInstance();
        } else {
            this.wrapper = null;
            this.instance = instanceOrWrapper;
        }

        this.primaryName = names[0];
        this.container = container;
        this.names.addAll(Arrays.asList(names));
        this.inited = false;

        if (container.isThreadSafe()) {
            this.lock = new ReentrantReadWriteLock();
        } else {
            this.lock = null;
        }

        this.log = LoggerFactory.getLogger(primaryName);
    }

    @Nonnull
    public Logger log() {
        return this.log;
    }

    public boolean isThreadSafe() {
        return this.lock != null;
    }

    public boolean notThreadSafe() {
        return this.lock == null;
    }

    LockCloser lock4Read() {
        return LockCloser.read(this.lock);
    }

    LockCloser lock4Write() {
        return LockCloser.write(this.lock);
    }

    @Override
    public int hashCode() {
        return getPrimaryName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        try {
            var that = (BeanInfo<?>) obj;
            return getInstance() == that.getInstance();
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean hasWrapper() {
        return this.wrapper != null;
    }

    Set<String> getNames() {
        return this.names;
    }

    @Override
    public String toString() {
        var inst = getInstance();
        return String.format("names=%s, class=%s, hasWrapper=%s, instance=%s", getNames(), inst.getClass(),
                hasWrapper(), inst);
    }

    public boolean doesDependsOn(@Nonnull String name) {
        if (notThreadSafe()) {
            return this.dependsOn.containsKey(name);
        }

        try (var rl = lock4Read()) {
            return this.dependsOn.containsKey(name);
        }
    }

    public boolean isDependedBy(@Nonnull String name) {
        if (notThreadSafe()) {
            return this.dependedBy.containsKey(name);
        }

        try (var rl = lock4Read()) {
            return this.dependedBy.containsKey(name);
        }
    }

    public void ensureNotInited() {
        if (isInited()) {
            throw new BadStateException("bean %s - already inited", getPrimaryName());
        }
    }

    void addDependedBy(@Nonnull String name, @Nonnull BeanInfo<?> bi) {
        if (notThreadSafe()) {
            this.dependedBy.put(name, bi);
        }

        try (var wl = lock4Write()) {
            this.dependedBy.put(name, bi);
        }
    }

    public <T2> Collection<T2> dependsOn(@Nonnull Class<T2> interfase) {
        var beanInfos = getContainer().listBeanInfosByInterface(interfase);
        dependsOn(beanInfos);

        var r = new ArrayList<T2>(beanInfos.size());
        for (var bi : beanInfos) {
            r.add(bi.getInstance());
        }
        return r;
    }

    public void dependsOn(@Nonnull Object... depends) {
        List<BeanInfo<?>> dependsBeanInfos = new ArrayList<>(depends.length);

        for (var depBean : depends) {
            BeanInfo<?> depInfo;
            if (depBean instanceof BeanInfo) {
                depInfo = (BeanInfo<?>) depBean;
            } else {
                depInfo = container.loadBeanInfoByInstance(depBean);
            }

            dependsBeanInfos.add(depInfo);
        }

        dependsOn(depends);
    }

    public void dependsOn(@Nonnull Collection<BeanInfo<?>> dependsBeanInfo) {
        if (notThreadSafe()) {
            doDependsOn(dependsBeanInfo);
        }

        try (var wl = lock4Write()) {
            doDependsOn(dependsBeanInfo);
        }
    }

    void doDependsOn(@Nonnull Collection<BeanInfo<?>> dependsBeanInfos) {
        ensureNotInited();

        var _dependsOn = new LinkedHashMap<String, BeanInfo<?>>(this.dependsOn);
        var myName = getPrimaryName();

        for (var depBeanInfo : dependsBeanInfos) {
            var depName = depBeanInfo.getPrimaryName();

            if (_dependsOn.containsKey(depName)) {
                // depends already
                continue;
            }

            if (depBeanInfo.doesDependsOn(myName)) {
                throw new BadStateException("bean %s - found cyclic depending bean: %s", myName, depName);
            }
            synchronized (depBeanInfo) {
                depBeanInfo.addDependedBy(myName, this);
            }

            _dependsOn.put(depName, depBeanInfo);
        }

        this.dependsOn = _dependsOn;
    }

    public void init() {
        if (notThreadSafe()) {
            doInit();
            return;
        }

        try (var wl = lock4Write()) {
            doInit();
        }
    }

    void doInit() {
        if (isInited()) {
            return;
        }

        this.dependsOn.values().forEach(BeanInfo::init);

        var inst = getInstance();
        if (inst instanceof Bean) {
            try {
                ((Bean) inst).init();
            } catch (Exception e) {
                throw new BadStateException(e, "bean %s - failed to init", getPrimaryName());
            }
        }

        this.inited = true;
    }

    public void destroy() {
        if (notThreadSafe()) {
            doDestroy();
            return;
        }

        try (var wl = lock4Write()) {
            doDestroy();
        }
    }

    boolean doDestroy() {
        if (isInited() == false) {
            return true;
        }

        this.dependedBy.values().forEach(BeanInfo::destroy);

        var inst = getInstance();
        if (inst instanceof Bean) {
            try {
                ((Bean) inst).destroy();
                return true;
            } catch (Exception e) {
                log().error("bean {} - failed to destroy", getPrimaryName(), e);
                return false;
            } finally {
                this.inited = false;
            }
        }

        return true;
    }

    public void addAliases(@Nonnull String... aliases) {
        if (notThreadSafe()) {
            doAddAliases(aliases);
            return;
        }

        try (var wl = lock4Write()) {
            doAddAliases(aliases);
        }
    }

    void doAddAliases(@Nonnull String... aliases) {
        getContainer().addAliases(this, aliases);

        for (var alias : aliases) {
            this.names.add(alias);
        }
    }

}

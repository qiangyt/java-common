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
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.LockCloser;
import jakarta.annotation.Nonnull;
import lombok.Getter;

public class BeanMetadata<T> {

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
    LinkedHashMap<String, BeanMetadata<?>> dependsOn = new LinkedHashMap<>();

    @Nonnull
    LinkedHashMap<String, BeanMetadata<?>> dependedBy = new LinkedHashMap<>();

    @Getter
    WrapperBean<T> wrapper;

    @Getter
    @Nonnull
    final BeanContainer container;

    @Nonnull
    final ReentrantReadWriteLock lock;

    @Getter
    boolean logLifecycle = true;

    @SuppressWarnings("unchecked")
    public BeanMetadata(@Nonnull BeanContainer container, @Nonnull T instanceOrWrapper, @Nonnull String... names) {
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

    public Bean getBeanInstance() {
        var w = getWrapper();
        if (w != null) {
            return w;
        }

        var i = getInstance();
        if (i instanceof Bean) {
            return (Bean) i;
        }

        return null;
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
            var that = (BeanMetadata<?>) obj;
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
            return doDoesDependsOn(name);
        }

        try (var rl = lock4Read()) {
            return doDoesDependsOn(name);
        }
    }

    boolean doDoesDependsOn(@Nonnull String name) {
        return this.dependsOn.containsKey(name);
    }

    public boolean isDependedBy(@Nonnull String name) {
        if (notThreadSafe()) {
            return doDependedBy(name);
        }

        try (var rl = lock4Read()) {
            return doDependedBy(name);
        }
    }

    boolean doDependedBy(@Nonnull String name) {
        return this.dependedBy.containsKey(name);
    }

    public void ensureNotInited() {
        if (isInited()) {
            throw new BadStateException("bean %s - already inited", getPrimaryName());
        }
    }

    void addDependedBy(@Nonnull String name, @Nonnull BeanMetadata<?> bi) {
        if (notThreadSafe()) {
            doAddDependedBy(name, bi);
            return;
        }

        try (var wl = lock4Write()) {
            doAddDependedBy(name, bi);
        }
    }

    void doAddDependedBy(@Nonnull String name, @Nonnull BeanMetadata<?> bi) {
        this.dependedBy.put(name, bi);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T2> Collection<T2> dependsOn(@Nonnull Class<T2> interfase) {
        var metadatas = getContainer().listMetadatasByInterface(interfase);
        dependsOn((Collection) metadatas);

        var r = new ArrayList<T2>(metadatas.size());
        for (var bi : metadatas) {
            r.add(bi.getInstance());
        }
        return r;
    }

    void dependsOn(@Nonnull Collection<BeanMetadata<?>> dependsMetadata) {
        if (notThreadSafe()) {
            doDependsOn(dependsMetadata);
            return;
        }

        try (var wl = lock4Write()) {
            doDependsOn(dependsMetadata);
        }
    }

    void doDependsOn(@Nonnull Collection<BeanMetadata<?>> dependsMetadatas) {
        ensureNotInited();

        var _dependsOn = new LinkedHashMap<String, BeanMetadata<?>>(this.dependsOn);
        var myName = getPrimaryName();

        for (var depMetadata : dependsMetadatas) {
            var depName = depMetadata.getPrimaryName();

            if (_dependsOn.containsKey(depName)) {
                // depends already
                continue;
            }

            if (depMetadata.doesDependsOn(myName)) {
                throw new BadStateException("bean %s - found cyclic depending bean: %s", myName, depName);
            }
            depMetadata.doAddDependedBy(myName, this);

            _dependsOn.put(depName, depMetadata);
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

        this.dependsOn.values().forEach(BeanMetadata::init);

        var logHere = isLogLifecycle();
        var lg = log();

        var b = getBeanInstance();
        if (b != null) {
            if (logHere) {
                lg.info("init - begin");
            } else {
                lg.debug("init - begin");
            }

            try {
                ((Bean) b).doInit();
            } catch (Exception e) {
                throw new BadStateException(e, "bean %s - failed to init", getPrimaryName());
            }

            if (logHere) {
                lg.info("init - done");
            } else {
                lg.debug("init - done");
            }
        } else {
            if (logHere) {
                lg.info("init() is skipped as this is not implemented");
            } else {
                lg.debug("init() is skipped as this is not implemented");
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

        this.dependedBy.values().forEach(BeanMetadata::destroy);

        var logHere = isLogLifecycle();
        var lg = log();

        var b = getBeanInstance();
        if (b != null) {
            if (logHere) {
                lg.info("destroy - begin");
            } else {
                lg.debug("destroy - begin");
            }

            try {
                ((Bean) b).doDestroy();
            } catch (Exception e) {
                lg.error("bean {} - failed to destroy", getPrimaryName(), e);
                return false;
            } finally {
                this.inited = false;
            }

            if (logHere) {
                lg.info("destroy - done");
            } else {
                lg.debug("destroy - done");
            }
        } else {
            if (logHere) {
                lg.info("destroy() is skipped as this is not implemented");
            } else {
                lg.debug("destroy() is skipped as this is not implemented");
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

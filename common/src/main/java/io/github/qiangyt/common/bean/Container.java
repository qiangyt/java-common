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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.ClassHelper;
import io.github.qiangyt.common.misc.LockCloser;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class Container {

    private static final ThreadLocal<Container> CURRENT = new ThreadLocal<>();

    @Getter(AccessLevel.NONE)
    final Logger log;

    @Nonnull
    final String name;

    @Getter(AccessLevel.NONE)
    final Map<Class<?>, BeanInfo<?>> beansByClass = new HashMap<>();

    @Getter(AccessLevel.NONE)
    final Multimap<Class<?>, BeanInfo<?>> beansByInterfaces = MultimapBuilder.hashKeys().arrayListValues().build();

    @Getter(AccessLevel.NONE)
    final LinkedHashMap<String, BeanInfo<?>> beansByName = new LinkedHashMap<>();

    @Getter(AccessLevel.NONE)
    final Map<Object, BeanInfo<?>> beansByInstance = new IdentityHashMap<>();

    final ReentrantReadWriteLock lock;

    public Container(@Nonnull String name, boolean threadSafe) {
        this.name = name;
        this.log = LoggerFactory.getLogger(name + "@" + ClassHelper.parseTitle(getClass()));

        if (threadSafe) {
            this.lock = new ReentrantReadWriteLock();
        } else {
            this.lock = null;
        }
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

    @Nonnull
    public Logger log() {
        return this.log;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Container> T getCurrent() {
        return (T) CURRENT.get();
    }

    @Nonnull
    public static <T extends Container> T loadCurrent() {
        T r = getCurrent();
        if (r == null) {
            throw new BadStateException("no active container");
        }
        return r;
    }

    public <T> BeanInfo<T> registerBean(@Nonnull T instance, @Nonnull String beanName) {
        if (notThreadSafe()) {
            return doRegisterBean(instance, beanName);
        }

        try (var lc = lock4Write()) {
            return doRegisterBean(instance, beanName);
        }
    }

    public void ensureNameNotConflicts(@Nonnull String... beanNames) {
        if (notThreadSafe()) {
            doEnsureNameNotConflicts(beanNames);
            return;
        }

        try (var rl = lock4Read()) {
            doEnsureNameNotConflicts(beanNames);
        }
    }

    void doEnsureNameNotConflicts(@Nonnull String... beanNames) {
        for (var beanName : beanNames) {
            var bi = this.beansByName.get(beanName);
            if (bi != null) {
                throw new BadStateException("%s - bean already registered: %s", getName(), bi);
            }
        }
    }

    public void ensureNameExists(@Nonnull String... beanNames) {
        if (notThreadSafe()) {
            doEnsureNameExists(beanNames);
            return;
        }

        try (var rl = lock4Read()) {
            doEnsureNameExists(beanNames);
        }
    }

    void doEnsureNameExists(@Nonnull String... beanNames) {
        for (var beanName : beanNames) {
            loadBean(beanName);
        }
    }

    @SuppressWarnings("unchecked")
    <T> BeanInfo<T> doRegisterBean(@Nonnull T instanceOrWrapper, @Nonnull String... beanNames) {
        doEnsureNameNotConflicts(beanNames);

        Object instance;
        if (instanceOrWrapper instanceof WrapperBean) {
            instance = ((WrapperBean<T>) instanceOrWrapper).getInstance();
        } else {
            instance = instanceOrWrapper;
        }

        var biByInstance = this.beansByInstance.get(instance);
        if (biByInstance != null) {
            throw new BadStateException("%s - bean already registered: %s", getName(), biByInstance);
        }

        Class<?> clazz = instance.getClass();

        var biByClazz = (Set<BeanInfo<?>>) this.beansByClass.get(clazz);
        if (biByClazz != null) {
            throw new BadStateException("%s - bean already registered: %s", biByClazz);
        }

        var r = new BeanInfo<T>(this, instanceOrWrapper, beanNames);

        for (var beanName : beanNames) {
            this.beansByName.put(beanName, r);
        }

        this.beansByInstance.put(instance, r);
        this.beansByClass.put(clazz, r);

        for (var interfase : clazz.getInterfaces()) {
            this.beansByInterfaces.put(interfase, r);
        }

        return r;
    }

    public void build(@Nonnull BeansBuilder builder) {
        if (notThreadSafe()) {
            doBuild(builder);
            return;
        }

        try (var lc = lock4Write()) {
            if (CURRENT.get() != null) {
                throw new BadStateException("%s - wrong container build status", getName());
            }

            try {
                CURRENT.set(this);
                builder.build(this);
            } catch (Exception ex) {
                throw new BadStateException(ex, "%s - failed to build container", getName());
            } finally {
                CURRENT.remove();
            }
        }
    }

    void doBuild(@Nonnull BeansBuilder builder) {
        if (CURRENT.get() != null) {
            throw new BadStateException("%s - wrong container build status", getName());
        }

        try {
            CURRENT.set(this);
            builder.build(this);
        } catch (Exception ex) {
            throw new BadStateException(ex, "%s - failed to build container", getName());
        } finally {
            CURRENT.remove();
        }
    }

    public void refresh() {
        if (notThreadSafe()) {
            doRefresh();
            return;
        }

        try (var lc = LockCloser.write(this.lock);) {
            doRefresh();
        }
    }

    void doRefresh() {
        this.beansByName.values().forEach(BeanInfo::init);
    }

    public void destroy() {
        if (notThreadSafe()) {
            doDestroy();
            return;
        }

        try (var lc = lock4Write()) {
            doDestroy();
        }
    }

    void doDestroy() {
        this.beansByName.values().forEach(BeanInfo::destroy);
    }

    public void addAliases(@Nonnull BeanInfo<?> beanInfo, @Nonnull String... aliases) {
        if (notThreadSafe()) {
            doAddAliases(beanInfo, aliases);
            return;
        }

        try (var lc = lock4Write()) {
            doAddAliases(beanInfo, aliases);
        }
    }

    void doAddAliases(@Nonnull BeanInfo<?> beanInfo, @Nonnull String... aliases) {
        loadBeanInfo(beanInfo.getPrimaryName());

        for (var alias : aliases) {
            if (this.beansByName.containsKey(alias)) {
                throw new BadStateException("%s - alias already registered: %s", getName(), alias);
            }
        }

        for (var alias : aliases) {
            this.beansByName.put(alias, beanInfo);
        }
    }

    public <T> BeanInfo<T> getBeanInfo(@Nonnull Class<T> clazz) {
        if (notThreadSafe()) {
            return doGetBeanInfo(clazz);
        }

        try (var lc = lock4Read()) {
            return doGetBeanInfo(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    <T> BeanInfo<T> doGetBeanInfo(@Nonnull Class<T> clazz) {
        var r = (BeanInfo<T>) this.beansByClass.get(clazz);
        if (r == null) {
            return null;
        }

        var inst = r.getInstance();
        if (inst.getClass() != clazz) {
            throw new BadStateException("%s - bean class mismatch: expected=%s, actual=%s", getName(), clazz,
                    inst.getClass());
        }
        return r;
    }

    @Nonnull
    public <T> BeanInfo<T> loadBeanInfo(@Nonnull Class<T> clazz) {
        BeanInfo<T> r = getBeanInfo(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    public <T> T getBean(@Nonnull Class<T> clazz) {
        BeanInfo<T> bi = getBeanInfo(clazz);
        return (bi == null) ? null : bi.getInstance();
    }

    @Nonnull
    public <T> T loadBean(@Nonnull Class<T> clazz) {
        return loadBeanInfo(clazz).getInstance();
    }

    @SuppressWarnings("unchecked")
    public <T> BeanInfo<T> getBeanInfo(@Nonnull String beanName) {
        if (notThreadSafe()) {
            return (BeanInfo<T>) this.beansByName.get(beanName);
        }

        try (var lc = lock4Read()) {
            return (BeanInfo<T>) this.beansByName.get(beanName);
        }
    }

    @Nonnull
    public <T> BeanInfo<T> loadBeanInfo(@Nonnull String beanName) {
        BeanInfo<T> r = getBeanInfo(beanName);
        if (r == null) {
            throw new BadStateException("%s - bean not found: name=%s", getName(), beanName);
        }
        return r;
    }

    public <T> T getBean(@Nonnull String beanName) {
        BeanInfo<T> bi = getBeanInfo(beanName);
        return (bi == null) ? null : bi.getInstance();
    }

    @Nonnull
    public <T> T loadBean(@Nonnull String beanName) {
        BeanInfo<T> bi = loadBeanInfo(beanName);
        return bi.getInstance();
    }

    @Nonnull
    public <T> BeanInfo<T> loadBeanInfoByInstance(T instance) {
        BeanInfo<T> r = getBeanInfoByInstance(instance);
        if (r == null) {
            throw new BadStateException("%s - bean not found: instance=%s", getName(), instance);
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public <T> BeanInfo<T> getBeanInfoByInstance(T instance) {
        if (notThreadSafe()) {
            return (BeanInfo<T>) this.beansByInstance.get(instance);
        }

        try (var rl = lock4Read()) {
            return (BeanInfo<T>) this.beansByInstance.get(instance);
        }
    }

    public <T> T getBeanByInstance(T instance) {
        BeanInfo<T> bi = getBeanInfoByInstance(instance);
        return (bi == null) ? null : bi.getInstance();
    }

    @Nonnull
    public <T> T loadBeanByInstance(T instance) {
        BeanInfo<T> bi = loadBeanInfoByInstance(instance);
        return bi.getInstance();
    }

    @Nonnull
    public <T> Collection<BeanInfo<T>> listBeanInfosByInterface(Class<T> interfase) {
        if (notThreadSafe()) {
            return doListBeanInfosByInterface(interfase);
        }

        try (var rl = lock4Read()) {
            return doListBeanInfosByInterface(interfase);
        }
    }

    @Nonnull
    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T> Collection<BeanInfo<T>> doListBeanInfosByInterface(Class<T> interfase) {
        Collection r = (Collection) this.beansByInterfaces.get(interfase);
        return (r == null) ? Collections.emptyList() : (Collection<BeanInfo<T>>) r;
    }

    @Nonnull
    public <T> Collection<?> listBeanByInterface(Class<T> interfase) {
        if (notThreadSafe()) {
            return doListBeanByInterface(interfase);
        }

        try (var rl = lock4Read()) {
            return doListBeanByInterface(interfase);
        }
    }

    @Nonnull
    <T> Collection<T> doListBeanByInterface(Class<T> interfase) {
        var biList = listBeanInfosByInterface(interfase);
        if (biList.isEmpty()) {
            return Collections.emptyList();
        }

        var r = new ArrayList<T>(biList.size());
        for (var bi : biList) {
            r.add(bi.getInstance());
        }

        return r;
    }

}

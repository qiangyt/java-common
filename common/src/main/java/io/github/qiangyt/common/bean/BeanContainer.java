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

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.LockCloser;
import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public class BeanContainer extends InternalContainer {

    private static final ThreadLocal<BeanContainer> CURRENT = new ThreadLocal<>();

    final ReentrantReadWriteLock lock;

    public BeanContainer(@Nonnull String name, boolean threadSafe) {
        super(name, threadSafe);

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

    @SuppressWarnings("unchecked")
    public static <T extends BeanContainer> T getCurrent() {
        return (T) CURRENT.get();
    }

    @Nonnull
    public static <T extends BeanContainer> T loadCurrent() {
        T r = getCurrent();
        if (r == null) {
            throw new BadStateException("no active bean container");
        }
        return r;
    }

    public <T> BeanMetadata<T> registerBean(@Nonnull T instance, @Nonnull String beanName) {
        if (notThreadSafe()) {
            return doRegisterBean(instance, beanName);
        }

        try (var lc = lock4Write()) {
            return doRegisterBean(instance, beanName);
        }
    }

    @SuppressWarnings("unchecked")
    <T> BeanMetadata<T> doRegisterBean(@Nonnull T instanceOrWrapper, @Nonnull String... beanNames) {
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

        var biByClazz = (Set<BeanMetadata<?>>) this.beansByClass.get(clazz);
        if (biByClazz != null) {
            throw new BadStateException("%s - bean already registered: %s", biByClazz);
        }

        var r = new BeanMetadata<T>(this, instanceOrWrapper, beanNames);

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

    public void ensureNameNotConflicts(@Nonnull String... beanNames) {
        if (notThreadSafe()) {
            doEnsureNameNotConflicts(beanNames);
            return;
        }

        try (var rl = lock4Read()) {
            doEnsureNameNotConflicts(beanNames);
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

    /*
     * public void build(@Nonnull BeansBuilder builder) { if (notThreadSafe()) { doBuild(builder); return; }
     *
     * try (var lc = lock4Write()) { doBuild(builder); } }
     */

    /*
     * void doBuild(@Nonnull BeansBuilder builder) { if (CURRENT.get() != null) { throw new
     * BadStateException("%s - wrong container build status", getName()); }
     *
     * try { CURRENT.set(this); builder.build(this); } catch (Exception ex) { throw new BadStateException(ex,
     * "%s - failed to build container", getName()); } finally { CURRENT.remove(); } }
     */

    public void refresh() {
        if (notThreadSafe()) {
            doRefresh();
            return;
        }

        try (var lc = LockCloser.write(this.lock);) {
            doRefresh();
        }
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

    public void addAliases(@Nonnull BeanMetadata<?> metadata, @Nonnull String... aliases) {
        if (notThreadSafe()) {
            doAddAliases(metadata, aliases);
            return;
        }

        try (var lc = lock4Write()) {
            doAddAliases(metadata, aliases);
        }
    }

    public <T> BeanMetadata<T> getMetadata(@Nonnull Class<T> clazz) {
        if (notThreadSafe()) {
            return doGetMetadata(clazz);
        }

        try (var lc = lock4Read()) {
            return doGetMetadata(clazz);
        }
    }

    @Nonnull
    public <T> BeanMetadata<T> loadMetadata(@Nonnull Class<T> clazz) {
        if (notThreadSafe()) {
            return doLoadMetadata(clazz);
        }

        try (var lc = lock4Read()) {
            return doLoadMetadata(clazz);
        }
    }

    public <T> T getBean(@Nonnull Class<T> clazz) {
        if (notThreadSafe()) {
            return doGetBean(clazz);
        }

        try (var lc = lock4Read()) {
            return doGetBean(clazz);
        }
    }

    @Nonnull
    public <T> T loadBean(@Nonnull Class<T> clazz) {
        if (notThreadSafe()) {
            return doLoadBean(clazz);
        }

        try (var lc = lock4Read()) {
            return doLoadBean(clazz);
        }
    }

    public <T> BeanMetadata<T> getMetadata(@Nonnull String beanName) {
        if (notThreadSafe()) {
            return doGetMetadata(beanName);
        }

        try (var lc = lock4Read()) {
            return doGetMetadata(beanName);
        }
    }

    @Nonnull
    public <T> BeanMetadata<T> loadMetadata(@Nonnull String beanName) {
        if (notThreadSafe()) {
            return doLoadMetadata(beanName);
        }

        try (var lc = lock4Read()) {
            return doLoadMetadata(beanName);
        }
    }

    public <T> T getBean(@Nonnull String beanName) {
        if (notThreadSafe()) {
            return doGetBean(beanName);
        }

        try (var lc = lock4Read()) {
            return doGetBean(beanName);
        }
    }

    @Nonnull
    public <T> T loadBean(@Nonnull String beanName) {
        if (notThreadSafe()) {
            return doLoadBean(beanName);
        }

        try (var lc = lock4Read()) {
            return doLoadBean(beanName);
        }
    }

    @Nonnull
    public <T> BeanMetadata<T> loadMetadataByInstance(T instance) {
        if (notThreadSafe()) {
            return doLoadMetadataByInstance(instance);
        }

        try (var lc = lock4Read()) {
            return doLoadMetadataByInstance(instance);
        }
    }

    public <T> BeanMetadata<T> getMetadataByInstance(T instance) {
        if (notThreadSafe()) {
            return doGetMetadataByInstance(instance);
        }

        try (var lc = lock4Read()) {
            return doGetMetadataByInstance(instance);
        }
    }

    @Nonnull
    public <T> Collection<BeanMetadata<T>> listMetadatasByInterface(Class<T> interfase) {
        if (notThreadSafe()) {
            return doListMetadatasByInterface(interfase);
        }

        try (var rl = lock4Read()) {
            return doListMetadatasByInterface(interfase);
        }
    }

    @Nonnull
    public <T> Collection<T> listBeanByInterface(Class<T> interfase) {
        if (notThreadSafe()) {
            return doListBeanByInterface(interfase);
        }

        try (var rl = lock4Read()) {
            return doListBeanByInterface(interfase);
        }
    }

    public Collection<BeanMetadata<?>> normalizeMetadatas(@Nonnull Iterable<?> beans) {
        if (notThreadSafe()) {
            return doNormalizeMetadatas(beans);
        }

        try (var rl = lock4Read()) {
            return doNormalizeMetadatas(beans);
        }
    }

}
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.ClassHelper;

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
    final LinkedHashMap<String, BeanInfo<?>> beansByName = new LinkedHashMap<>();

    volatile boolean locked;

    public Container(@Nonnull String name) {
        this.log = LoggerFactory.getLogger(name + "@" + ClassHelper.parseTitle(getClass()));
        this.locked = false;
        this.name = name;
    }

    @Nonnull
    public Logger log() {
        return this.log;
    }

    public synchronized void ensureNotLocked() {
        if (isLocked()) {
            throw new BadStateException("%s - container is locked", getName());
        }
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

    public synchronized <T extends Bean> BeanInfo<T> tryToRegisterBean(@Nonnull T instance, @Nonnull String beanName) {
        BeanInfo<T> r = getBeanInfo(beanName);
        if (r != null) {
            if (r.getInstance() != instance) {
                throw new BadStateException("%s - bean already registered: name=%s, instance=%s", getName(), beanName,
                        instance);
            }
            return r;
        }

        return doRegisterBean(instance, beanName);
    }

    public synchronized <T extends Bean> BeanInfo<T> registerBean(@Nonnull T instance, @Nonnull String beanName,
            @Nonnull Object... dependsOn) {
        if (this.beansByName.containsKey(beanName)) {
            throw new BadStateException("%s - bean already registered: name=%s", getName(), beanName);
        }

        Class<?> clazz = instance.getClass();
        if (this.beansByClass.containsKey(clazz)) {
            throw new BadStateException("%s - bean already registered: class=%s", getName(), clazz);
        }

        return doRegisterBean(instance, beanName, dependsOn);
    }

    synchronized <T> BeanInfo<T> doRegisterBean(@Nonnull T instance, @Nonnull String beanName,
            @Nonnull Object... dependsOn) {
        ensureNotLocked();

        var r = new BeanInfo<T>(instance, beanName, dependsOn);

        this.beansByName.put(beanName, r);
        this.beansByClass.put(instance.getClass(), r);

        r.dependsOn(this, dependsOn);

        return r;
    }

    @SuppressWarnings("unchecked")
    public <T extends Bean> BeanInfo<T> getBeanInfo(@Nonnull Class<T> clazz) {
        return (BeanInfo<T>) this.beansByClass.get(clazz);
    }

    public <T extends Bean> BeanInfo<T> loadBeanInfo(@Nonnull Class<T> clazz) {
        BeanInfo<T> r = getBeanInfo(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    public <T extends Bean> T getBean(@Nonnull Class<T> clazz) {
        var bi = getBeanInfo(clazz);
        if (bi == null) {
            return null;
        }

        var r = bi.getInstance();
        if (r.getClass() != clazz) {
            throw new BadStateException("%s - bean class mismatch: expected=%s, actual=%s", getName(), clazz,
                    r.getClass());
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public <T extends Bean> BeanInfo<T> getBeanInfo(@Nonnull String beanName) {
        return (BeanInfo<T>) this.beansByName.get(beanName);
    }

    public <T extends Bean> BeanInfo<T> loadBeanInfo(@Nonnull String beanName) {
        BeanInfo<T> r = getBeanInfo(beanName);
        if (r == null) {
            throw new BadStateException("%s - bean not found: name=%s", getName(), beanName);
        }
        return r;
    }

    public <T extends Bean> T getBean(@Nonnull String beanName) {
        BeanInfo<T> bi = getBeanInfo(beanName);
        if (bi == null) {
            return null;
        }
        return bi.getInstance();
    }

    @Nonnull
    public <T extends Bean> T loadBean(@Nonnull Class<T> clazz) {
        T r = getBean(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    @Nonnull
    public <T extends Bean> T loadBean(@Nonnull String beanName) {
        T r = getBean(beanName);
        if (r == null) {
            throw new BadStateException("%s - bean not found: name=%s", getName(), beanName);
        }
        return r;
    }

    public synchronized void build(@Nonnull BeansBuilder builder) {
        ensureNotLocked();

        if (CURRENT.get() != null) {
            throw new BadStateException("%s - wrong container build status", getName());
        }

        try {
            CURRENT.set(this);
            builder.build();
        } catch (Exception ex) {
            throw new BadStateException(ex, "%s - failed to build container", getName());
        } finally {
            CURRENT.remove();
        }
    }

    public synchronized void refresh() {
        ensureNotLocked();
        this.beansByName.values().forEach(BeanInfo::init);
        this.locked = true;
    }

    public synchronized void destroy() {
        ensureNotLocked();
        this.beansByName.values().forEach(BeanInfo::destroy);
        this.locked = true;
    }

}

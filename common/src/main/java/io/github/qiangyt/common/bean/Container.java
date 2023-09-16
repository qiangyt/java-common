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

import java.util.IdentityHashMap;
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

    @Getter(AccessLevel.NONE)
    final Map<Object, BeanInfo<?>> beansByInstance = new IdentityHashMap<>();

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

    @SuppressWarnings("unchecked")
    public synchronized <T> BeanInfo<T> registerBean(@Nonnull T instance, @Nonnull String beanName,
            @Nonnull Object... dependsOn) {
        var bi = getBeanInfo(beanName);
        if (bi != null) {
            // name already registered
            var inst = bi.getInstance();
            if (inst != instance) {
                // but instance is different
                throw new BadStateException("%s - bean already registered: %s", getName(), bi);
            }
        } else {
            // name not registered
        }

        Class<?> clazz = instance.getClass();
        bi = (BeanInfo<Object>) getBeanInfo(clazz);
        if (bi != null) {
            // class already registered
            var inst = bi.getInstance();
            if (inst != instance) {
                // but instance is different
                throw new BadStateException("%s - bean already registered: %s", bi);
            }
        } else {
            // class not registered
        }

        bi = (BeanInfo<Object>) this.beansByInstance.get(instance);
        if (bi != null) {
            // instance already registered
            bi.dependsOn(this, dependsOn);
            return (BeanInfo<T>) bi;
        }

        return doRegisterBean(instance, beanName, dependsOn);
    }

    /*
     * @SuppressWarnings("unchecked") public synchronized <T> BeanInfo<T> registerBean(@Nonnull T instance, @Nonnull
     * String beanName,
     *
     * @Nonnull Object... dependsOn) { var bi = getBeanInfo(beanName); if (bi != null) { var inst = bi.getInstance();
     * throw new BadStateException("%s - bean already registered: name=%s, class=%s, instance=%s", getName(),
     * bi.getName(), inst.getClass(), inst); }
     *
     * Class<?> clazz = instance.getClass(); bi = (BeanInfo<Object>) getBeanInfo(clazz); if (bi != null) { var inst =
     * bi.getInstance(); throw new BadStateException("%s - bean already registered: name=%s, class=%s, instance=%s",
     * getName(), bi.getName(), inst.getClass(), inst); }
     *
     * bi = (BeanInfo<Object>) this.beansByInstance.get(instance); if (bi != null) { var inst = bi.getInstance(); throw
     * new BadStateException("%s - bean already registered: name=%s, class=%s, instance=%s", getName(), bi.getName(),
     * inst.getClass(), inst); }
     *
     * return doRegisterBean(instance, beanName, dependsOn); }
     */

    synchronized <T> BeanInfo<T> doRegisterBean(@Nonnull T instance, @Nonnull String beanName,
            @Nonnull Object... dependsOn) {
        ensureNotLocked();

        var r = new BeanInfo<T>(instance, beanName, dependsOn);

        this.beansByName.put(beanName, r);
        this.beansByClass.put(instance.getClass(), r);
        this.beansByInstance.put(instance, r);

        r.dependsOn(this, dependsOn);

        return r;
    }

    @SuppressWarnings("unchecked")
    public <T> BeanInfo<T> getBeanInfo(@Nonnull Class<T> clazz) {
        return (BeanInfo<T>) this.beansByClass.get(clazz);
    }

    public <T> BeanInfo<T> loadBeanInfo(@Nonnull Class<T> clazz) {
        BeanInfo<T> r = getBeanInfo(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    public <T> T getBean(@Nonnull Class<T> clazz) {
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
    public <T> BeanInfo<T> getBeanInfo(@Nonnull String beanName) {
        return (BeanInfo<T>) this.beansByName.get(beanName);
    }

    public <T> BeanInfo<T> loadBeanInfo(@Nonnull String beanName) {
        BeanInfo<T> r = getBeanInfo(beanName);
        if (r == null) {
            throw new BadStateException("%s - bean not found: name=%s", getName(), beanName);
        }
        return r;
    }

    public <T> T getBean(@Nonnull String beanName) {
        BeanInfo<T> bi = getBeanInfo(beanName);
        if (bi == null) {
            return null;
        }
        return bi.getInstance();
    }

    @Nonnull
    public <T> T loadBean(@Nonnull Class<T> clazz) {
        T r = getBean(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    @Nonnull
    public <T> T loadBean(@Nonnull String beanName) {
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
            builder.build(this);
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

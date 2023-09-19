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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.ClassHelper;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
class InternalContainer {

    @Getter(AccessLevel.NONE)
    final Logger log;

    @Nonnull
    final String name;

    @Getter(AccessLevel.NONE)
    final Map<Class<?>, BeanMetadata<?>> beansByClass = new HashMap<>();

    @Getter(AccessLevel.NONE)
    final Multimap<Class<?>, BeanMetadata<?>> beansByInterfaces = MultimapBuilder.hashKeys().arrayListValues().build();

    @Getter(AccessLevel.NONE)
    final LinkedHashMap<String, BeanMetadata<?>> beansByName = new LinkedHashMap<>();

    @Getter(AccessLevel.NONE)
    final Map<Object, BeanMetadata<?>> beansByInstance = new IdentityHashMap<>();

    InternalContainer(@Nonnull String name, boolean threadSafe) {
        if (name == null) {
            name = ClassHelper.parseTitle(getClass());
        }

        this.name = name;

        this.log = LoggerFactory.getLogger(name);
        this.log.info("created");
    }

    @Nonnull
    public Logger log() {
        return this.log;
    }

    void doEnsureNameNotConflicts(@Nonnull String... beanNames) {
        for (var beanName : beanNames) {
            var bi = this.beansByName.get(beanName);
            if (bi != null) {
                throw new BadStateException("%s - bean already registered: %s", getName(), bi);
            }
        }
    }

    void doEnsureNameExists(@Nonnull String... beanNames) {
        for (var beanName : beanNames) {
            doLoadMetadata(beanName);
        }
    }

    void doRefresh() {
        this.log.info("refresh - begin");
        this.beansByName.values().forEach(BeanMetadata::init);
        this.log.info("refresh - done");
    }

    void doDestroy() {
        this.log.info("destroy - begin");
        this.beansByName.values().forEach(BeanMetadata::destroy);
        this.log.info("destroy - end");
    }

    void doAddAliases(@Nonnull BeanMetadata<?> metadata, @Nonnull String... aliases) {
        doGetMetadata(metadata.getPrimaryName());

        for (var alias : aliases) {
            if (this.beansByName.containsKey(alias)) {
                throw new BadStateException("%s - alias already registered: %s", getName(), alias);
            }
        }

        for (var alias : aliases) {
            this.beansByName.put(alias, metadata);
        }
    }

    @SuppressWarnings("unchecked")
    <T> BeanMetadata<T> doGetMetadata(@Nonnull String beanName) {
        return (BeanMetadata<T>) this.beansByName.get(beanName);
    }

    <T> BeanMetadata<T> doLoadMetadata(@Nonnull String beanName) {
        BeanMetadata<T> r = doGetMetadata(beanName);
        if (r == null) {
            throw new BadStateException("%s - bean not found: name=%s", getName(), beanName);
        }
        return r;
    }

    <T> T doGetBean(@Nonnull String beanName) {
        BeanMetadata<T> bi = doGetMetadata(beanName);
        if (bi == null) {
            return null;
        }
        return bi.getInstance();
    }

    <T> T doLoadBean(@Nonnull String beanName) {
        T r = doGetBean(beanName);
        if (r == null) {
            throw new BadStateException("%s - bean not found: name=%s", getName(), beanName);
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    <T> BeanMetadata<T> doGetMetadata(@Nonnull Class<T> clazz) {
        var bi = (BeanMetadata<T>) this.beansByClass.get(clazz);
        if (bi == null) {
            return null;
        }

        var r = bi.getInstance();
        if (r.getClass() != clazz) {
            throw new BadStateException("%s - bean class mismatch: expected=%s, actual=%s", getName(), clazz,
                    r.getClass());
        }
        return bi;
    }

    <T> BeanMetadata<T> doLoadMetadata(@Nonnull Class<T> clazz) {
        BeanMetadata<T> r = doGetMetadata(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    <T> T doGetBean(@Nonnull Class<T> clazz) {
        var bi = (BeanMetadata<T>) doGetMetadata(clazz);
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

    <T> T doLoadBean(@Nonnull Class<T> clazz) {
        var r = doGetBean(clazz);
        if (r == null) {
            throw new BadStateException("%s - bean not found: class=%s", getName(), clazz);
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    <T> BeanMetadata<T> doGetMetadataByInstance(T instance) {
        return (BeanMetadata<T>) this.beansByInstance.get(instance);
    }

    <T> BeanMetadata<T> doLoadMetadataByInstance(T instance) {
        BeanMetadata<T> r = doGetMetadataByInstance(instance);
        if (r == null) {
            throw new BadStateException("%s - bean not found: instance=%s", getName(), instance);
        }
        return r;
    }

    @Nonnull
    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T> Collection<BeanMetadata<T>> doListMetadatasByInterface(Class<T> interfase) {
        Collection r = (Collection) this.beansByInterfaces.get(interfase);
        return (r == null) ? Collections.emptyList() : (Collection<BeanMetadata<T>>) r;
    }

    @Nonnull
    <T> Collection<T> doListBeanByInterface(Class<T> interfase) {
        var biList = doListMetadatasByInterface(interfase);
        if (biList.isEmpty()) {
            return Collections.emptyList();
        }

        var r = new ArrayList<T>(biList.size());
        for (var bi : biList) {
            r.add(bi.getInstance());
        }

        return r;
    }

    Collection<BeanMetadata<?>> doNormalizeMetadatas(@Nonnull Object... beans) {
        List<BeanMetadata<?>> r = new ArrayList<>(beans.length);

        for (var depBean : beans) {
            BeanMetadata<?> depInfo;
            if (depBean instanceof BeanMetadata) {
                depInfo = (BeanMetadata<?>) depBean;
            } else {
                depInfo = doLoadMetadataByInstance(depBean);
            }

            r.add(depInfo);
        }

        return r;
    }

    Collection<BeanMetadata<?>> doNormalizeMetadatas(@Nonnull Iterable<?> beans) {
        List<BeanMetadata<?>> r = new ArrayList<>();

        for (var depBean : beans) {
            BeanMetadata<?> depInfo;
            if (depBean instanceof BeanMetadata) {
                depInfo = (BeanMetadata<?>) depBean;
            } else {
                depInfo = doLoadMetadataByInstance(depBean);
            }

            r.add(depInfo);
        }

        return r;
    }

}

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

import jakarta.annotation.Nonnull;

import io.github.qiangyt.common.err.BadStateException;

public class SimpleContainer {

    final Map<Class<?>, SimpleBean> beansByClass = new HashMap<>();

    final LinkedHashMap<String, SimpleBean> beansByName = new LinkedHashMap<>();

    public synchronized void registerBean(@Nonnull SimpleBean bean) {
        String name = bean.getBeanInfo().getName();
        if (this.beansByName.containsKey(name)) {
            throw new BadStateException("bean already registered: name=%s", name);
        }

        Class<?> clazz = bean.getClass();
        if (this.beansByClass.containsKey(clazz)) {
            throw new BadStateException("bean already registered: class=%s", clazz);
        }

        this.beansByName.put(name, bean);
        this.beansByClass.put(clazz, bean);
    }

    @SuppressWarnings("unchecked")

    public <T extends SimpleBean> T getBean(@Nonnull Class<T> clazz) {
        T r = (T) this.beansByClass.get(clazz);
        if (r == null) {
            return null;
        }
        if (r.getClass() != clazz) {
            throw new BadStateException("bean class mismatch: expected=%s, actual=%s", clazz, r.getClass());
        }
        return r;
    }

    @SuppressWarnings("unchecked")

    public <T extends SimpleBean> T getBean(@Nonnull String name) {
        return (T) this.beansByName.get(name);
    }

    @Nonnull
    public <T extends SimpleBean> T loadBean(@Nonnull Class<T> clazz) {
        T r = getBean(clazz);
        if (r == null) {
            throw new BadStateException("bean not found: class=%s", clazz);
        }
        return r;
    }

    @Nonnull
    public <T extends SimpleBean> T loadBean(@Nonnull String name) {
        T r = getBean(name);
        if (r == null) {
            throw new BadStateException("bean not found: name=%s", name);
        }
        return r;
    }

    public void refresh() {
        for (var bean : this.beansByName.values()) {
            var beanInfo = bean.getBeanInfo();
            if (beanInfo.isInited()) {
                continue;
            }

            var deps = beanInfo.getDependencies();
            if (deps.isEmpty() == false) {
                for (var dep : deps.values()) {
                    if (dep.isInited() == false) {
                        dep.init();
                    }
                }
            }

            beanInfo.init();
        }
    }

}

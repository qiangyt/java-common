/*
 * io.github.qiangyt:qiangyt-common-core - Common library by Yiting Qiang
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

import java.util.LinkedHashMap;

import io.github.qiangyt.common.err.BadStateException;

public class SimpleContainer {

    final LinkedHashMap<String, SimpleBean> beans = new LinkedHashMap<>();

    public synchronized void registerBean(SimpleBean bean) {
        String name = bean.getBeanInfo().getName();
        if (beans.containsKey(name)) {
            throw new BadStateException("bean already registered: %s", name);
        }
        beans.put(name, bean);
    }

    public <T extends SimpleBean> BeanInfo<T> getBeanInfo(String name) {
        return null;
    }

    public <T extends SimpleBean> BeanInfo<T> loadBeanInfo(String name) {
        BeanInfo<T> r = getBeanInfo(name);
        if (r == null) {
            throw new BadStateException("bean not found: %s", name);
        }
        return r;
    }

    public void refresh() {
        for (var bean : beans.values()) {
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

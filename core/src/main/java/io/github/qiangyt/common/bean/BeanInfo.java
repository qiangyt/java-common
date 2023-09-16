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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.qiangyt.common.err.BadStateException;
import javax.annotation.Nonnull;
import lombok.Getter;
import static java.util.Objects.requireNonNull;

@Getter
public class BeanInfo<T extends SimpleBean> {

    @Nonnull
    final T instance;

    @Nonnull
    final String name;

    boolean inited;

    @Nonnull
    final Logger log;

    @Nonnull
    LinkedHashMap<String, BeanInfo<?>> dependencies = new LinkedHashMap<>();

    @SuppressWarnings("null")
    public BeanInfo(@Nonnull T instance, @Nonnull String name, @Nonnull SimpleBean... dependentBeans) {
        this.instance = requireNonNull(instance);
        this.name = requireNonNull(name);
        this.inited = false;
        this.log = LoggerFactory.getLogger(name);

        addDependencies(dependentBeans);
    }

    public synchronized void addDependencies(@Nonnull SimpleBean... dependentBeans) {
        ensureNotInited();

        var deps = new LinkedHashMap<String, BeanInfo<?>>(this.dependencies);

        for (var depBean : dependentBeans) {
            requireNonNull(depBean);

            var depInfo = depBean.getBeanInfo();
            var depName = depInfo.getName();

            if (deps.containsKey(depName)) {
                throw new BadStateException("bean %s - found duplicated dependent bean: %s", getName(), depName);
            }
            deps.put(depName, depInfo);
        }

        this.dependencies = deps;
    }

    public void ensureAlreadyInited() {
        if (!isInited()) {
            throw new BadStateException("bean %s - not yet inited", getName());
        }
    }

    public void ensureNotInited() {
        if (isInited()) {
            throw new BadStateException("bean %s - already inited", getName());
        }
    }

    public void ensureDependenciesAlreadyInited() {
        this.dependencies.values().forEach(BeanInfo::ensureAlreadyInited);
    }

    public void init() {
        ensureNotInited();

        ensureDependenciesAlreadyInited();

        try {
            getInstance().init();
        } catch (Exception e) {
            throw new BadStateException(e, "bean %s - failed to init", getName());
        }

        inited = true;
    }

    public void destroy() {
        if (isInited() == false) {
            return;
        }

        try {
            getInstance().destroy();
        } catch (Exception e) {
            throw new BadStateException(e, "bean %s - failed to destroy", getName());
        }
        inited = false;
    }

}

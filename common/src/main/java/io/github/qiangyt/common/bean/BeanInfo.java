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

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.qiangyt.common.err.BadStateException;
import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
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
    @Getter(AccessLevel.NONE)
    LinkedHashMap<String, BeanInfo<?>> dependsOn = new LinkedHashMap<>();

    @Nonnull
    @Getter(AccessLevel.NONE)
    LinkedHashMap<String, BeanInfo<?>> dependedBy = new LinkedHashMap<>();

    public BeanInfo(@Nonnull T instance, @Nonnull String name, @Nonnull SimpleBean... dependsOn) {
        this.instance = requireNonNull(instance);
        this.name = requireNonNull(name);
        this.inited = false;
        this.log = LoggerFactory.getLogger(name);
    }

    public synchronized boolean doesDependsOn(@Nonnull String name) {
        return this.dependsOn.containsKey(name);
    }

    public synchronized boolean isDependedBy(@Nonnull String name) {
        return this.dependedBy.containsKey(name);
    }

    public synchronized void ensureNotInited() {
        if (isInited()) {
            throw new BadStateException("bean %s - already inited", getName());
        }
    }

    synchronized void dependsOn(@Nonnull SimpleContainer container, @Nonnull SimpleBean... depends) {
        ensureNotInited();

        var _dependsOn = new LinkedHashMap<String, BeanInfo<?>>(this.dependsOn);
        var myName = getName();

        for (var depBean : depends) {
            requireNonNull(depBean);

            var depName = depBean.getName();
            var depInfo = container.loadBeanInfo(depName);

            if (_dependsOn.containsKey(depName)) {
                throw new BadStateException("bean %s - found duplicated depending bean: %s", myName, depName);
            }
            if (depInfo.doesDependsOn(myName)) {
                throw new BadStateException("bean %s - found cyclic depending bean: %s", myName, depName);
            }

            synchronized (depInfo) {
                depInfo.dependedBy.put(myName, this);
            }

            _dependsOn.put(depName, depInfo);
        }

        this.dependsOn = _dependsOn;
    }

    synchronized void init() {
        if (isInited()) {
            return;
        }

        this.dependsOn.values().forEach(BeanInfo::init);

        try {
            getInstance().init();
        } catch (Exception e) {
            throw new BadStateException(e, "bean %s - failed to init", getName());
        }

        this.inited = true;
    }

    synchronized boolean destroy(@Nonnull Logger log) {
        if (isInited() == false) {
            return true;
        }

        this.dependedBy.values().forEach(bi -> bi.destroy(log));

        try {
            getInstance().destroy();
            return true;
        } catch (Exception e) {
            log.error("bean {} - failed to destroy", getName(), e);
            return false;
        } finally {
            this.inited = false;
        }

    }

}

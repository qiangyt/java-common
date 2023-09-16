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
public class BeanInfo<T> {

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

    public BeanInfo(@Nonnull T instance, @Nonnull String name, @Nonnull Object... dependsOn) {
        this.instance = requireNonNull(instance);
        this.name = requireNonNull(name);
        this.inited = false;
        this.log = LoggerFactory.getLogger(name);
    }

    @Nonnull
    public Logger log() {
        return this.log;
    }

    @Override
    public String toString() {
        var inst = getInstance();
        return String.format("name=%s, class=%s, instance=%s", getName(), inst.getClass(), inst);
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

    public void dependsOn(@Nonnull Object... depends) {
        var container = Container.loadCurrent();
        dependsOn(container, depends);
    }

    synchronized void dependsOn(@Nonnull Container container, @Nonnull Object... depends) {
        ensureNotInited();

        var _dependsOn = new LinkedHashMap<String, BeanInfo<?>>(this.dependsOn);
        var myName = getName();

        for (var depBean : depends) {
            requireNonNull(depBean);

            var depName = Bean.parseBeanName(depBean);
            var depInfo = container.loadBeanInfo(depName);

            if (_dependsOn.containsKey(depName)) {
                // depends already
                continue;
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

        var inst = getInstance();
        if (inst instanceof Bean) {
            try {
                ((Bean) inst).init();
            } catch (Exception e) {
                throw new BadStateException(e, "bean %s - failed to init", getName());
            }
        }

        this.inited = true;
    }

    synchronized boolean destroy() {
        if (isInited() == false) {
            return true;
        }

        this.dependedBy.values().forEach(BeanInfo::destroy);

        var inst = getInstance();
        if (inst instanceof Bean) {
            try {
                ((Bean) inst).destroy();
                return true;
            } catch (Exception e) {
                log().error("bean {} - failed to destroy", getName(), e);
                return false;
            } finally {
                this.inited = false;
            }
        }

        return true;

    }

}

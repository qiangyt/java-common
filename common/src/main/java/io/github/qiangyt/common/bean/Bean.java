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

import org.slf4j.Logger;

import jakarta.annotation.Nonnull;

public interface Bean {

    @Nonnull
    <T extends Bean> BeanInfo<T> getBeanInfo();

    @Nonnull
    default String getName() {
        return getBeanInfo().getName();
    }

    @Nonnull
    default Logger log() {
        return getBeanInfo().log();
    }

    default boolean isInited() {
        return getBeanInfo().isInited();
    }

    default void init() throws Exception {
    }

    default void destroy() throws Exception {
    }

    @Nonnull
    static <T> BeanWrapper<T> wrap(@Nonnull T instance, @Nonnull String name) {
        return new BeanWrapper<T>(instance, name);
    }

    @Nonnull
    static <T> BeanWrapper<T> wrap(@Nonnull T instance) {
        return new BeanWrapper<T>(instance);
    }

}

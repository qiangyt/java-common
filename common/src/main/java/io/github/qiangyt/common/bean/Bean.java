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

import io.github.qiangyt.common.misc.ClassHelper;
import jakarta.annotation.Nonnull;

public interface Bean {

    @Nonnull
    <T extends Bean> BeanInfo<T> getBeanInfo();

    @Nonnull
    default String getPrimaryName() {
        return getBeanInfo().getPrimaryName();
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
    static String parseBeanName(@Nonnull Class<?> clazz) {
        var r = ClassHelper.parseTitle(clazz);
        return r.substring(0, 1).toLowerCase() + r.substring(1);
    }

    @Nonnull
    static String parseBeanName(@Nonnull Object object) {
        if (object instanceof Bean) {
            return ((Bean) object).getPrimaryName();
        }
        return parseBeanName(object.getClass());
    }

}

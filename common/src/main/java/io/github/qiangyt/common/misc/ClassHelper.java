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
package io.github.qiangyt.common.misc;

import io.github.qiangyt.common.bean.Bean;
import jakarta.annotation.Nonnull;

/**
 *
 */
public class ClassHelper {

    @Nonnull
    public static String parseTitle(@Nonnull Class<?> clazz) {
        var n = clazz.getSimpleName();
        int pos = n.lastIndexOf('.');
        if (pos < 0) {
            return n;
        }
        return n.substring(pos + 1);
    }

    @Nonnull
    public static String parseBeanName(@Nonnull Class<?> clazz) {
        var r = parseTitle(clazz);
        return r.substring(0, 1).toLowerCase() + r.substring(1);
    }

    @Nonnull
    public static String parseBeanName(@Nonnull Object object) {
        if (object instanceof Bean) {
            return ((Bean) object).getName();
        }
        return parseBeanName(object.getClass());
    }

}

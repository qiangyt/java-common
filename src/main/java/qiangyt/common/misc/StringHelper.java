/*
 * qiangyt-common 1.0.0 - Common library by Yiting Qiang
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package qiangyt.common.misc;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Objects;

import jakarta.annotation.Nullable;

/**
 * 字符串操作的工具类
 */
public class StringHelper {

    @Nullable
    public static String capitalize(@Nullable String t) {
        if (t == null || t.isBlank()) {
            return t;
        }
        return t.substring(0, 1).toUpperCase() + t.substring(1);
    }

    /** 数组的toString */
    @Nullable
    public static <T> String toString(@Nullable T[] array) {
        if (array == null) {
            return null;
        }
        return Lists.newArrayList(array).toString();
    }

    /** boolean数组的toString */
    @Nullable
    public static String toString(@Nullable boolean[] array) {
        if (array == null) {
            return null;
        }
        return Lists.newArrayList(array).toString();
    }

    /**
     * 把多个字符串用指定分隔符连接
     *
     * @param separator
     *            分隔
     * @param texts
     *            待连接的多个字符串
     */
    public static <T> String join(String separator, Collection<T> texts) {
        return join(separator, texts.toArray(new String[texts.size()]));
    }

    /**
     * 数组的toString，用指定分隔符连接
     *
     * @param separator
     *            分隔符
     * @param array
     *            数组
     */
    public static <T> String join(String separator, T[] array) {
        var r = new StringBuilder(array.length * 64);
        var isFirst = true;
        for (var obj : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                r.append(separator);
            }
            r.append(Objects.toString(obj));
        }
        return r.toString();
    }

    /**
     * 是否是null或全是空白字符串
     */
    public static boolean isBlank(@Nullable String str) {
        return (str == null || str.length() == 0 || str.trim().length() == 0);
    }

    /**
     * 是否不会null而且不全是空白字符串
     */
    public static boolean notBlank(@Nullable String str) {
        return !isBlank(str);
    }

}

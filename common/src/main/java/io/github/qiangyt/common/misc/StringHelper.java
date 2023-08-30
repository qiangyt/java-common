/*
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.qiangyt.common.misc;

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

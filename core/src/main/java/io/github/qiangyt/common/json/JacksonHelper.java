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
package io.github.qiangyt.common.json;

import com.fasterxml.jackson.core.type.TypeReference;

import io.github.qiangyt.common.bean.Dumpable;

import javax.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

public class JacksonHelper {

    public static final Jackson JACKSON = Jackson.DEFAULT;

    public static String pretty(Object object) {
        return JACKSON.pretty(object);
    }

    public static String pretty(Dumpable dumpable) {
        return JACKSON.pretty(dumpable);
    }

    public static String to(Object object) {
        return JACKSON.toString(object, false);
    }

    public static <T> T from(String json, @Nonnull Class<T> clazz) {
        requireNonNull(clazz);
        return JACKSON.from(json, clazz);
    }

    public static <T> T from(String json, @Nonnull TypeReference<T> typeReference) {
        requireNonNull(typeReference);
        return JACKSON.from(json, typeReference);
    }

}

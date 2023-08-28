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
package qiangyt.common.json;

import com.fasterxml.jackson.core.type.TypeReference;

import qiangyt.common.bean.Dumpable;

import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

public class JacksonHelper {

    public static final Jackson JACKSON = Jackson.DEFAULT;

    @Nullable
    public static String pretty(@Nullable Object object) {
        return JACKSON.pretty(object);
    }

    @Nullable
    public static String pretty(@Nullable Dumpable dumpable) {
        return JACKSON.pretty(dumpable);
    }

    @Nullable
    public static String to(@Nullable Object object) {
        return JACKSON.toString(object, false);
    }

    @Nullable
    public static <T> T from(@Nullable String json, @Nonnull Class<T> clazz) {
        requireNonNull(clazz);
        return JACKSON.from(json, clazz);
    }

    @Nullable
    public static <T> T from(@Nullable String json, @Nonnull TypeReference<T> typeReference) {
        requireNonNull(typeReference);
        return JACKSON.from(json, typeReference);
    }

}

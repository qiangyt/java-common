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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.qiangyt.common.err.BadValueException;
import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

public class ClassCache {

    @Nonnull
    public static final ClassCache DEFAULT = new ClassCache();

    private final Map<String, Class<?>> cache = new ConcurrentHashMap<>();

    public int size() {
        return this.cache.size();
    }

    @Nonnull
    @SuppressWarnings("null")
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Nonnull
    @SuppressWarnings("null")
    public Class<?> resolve(@Nonnull String className) {
        requireNonNull(className);

        return this.cache.computeIfAbsent(className, key -> {
            try {
                return getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new BadValueException(e);
            }
        });
    }

}

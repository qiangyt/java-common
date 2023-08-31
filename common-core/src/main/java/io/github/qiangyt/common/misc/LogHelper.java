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
package io.github.qiangyt.common.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.logstash.logback.argument.StructuredArgument;
import net.logstash.logback.argument.StructuredArguments;
import io.github.qiangyt.common.bean.Dumpable;

import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

public class LogHelper {

    @SuppressWarnings("null")
    @Nonnull
    public static Logger getLogger(@Nonnull Class<?> klass, @Nonnull String name) {
        requireNonNull(klass);
        requireNonNull(name);

        return LoggerFactory.getLogger(name + "@" + klass.getCanonicalName());
    }

    @SuppressWarnings("null")
    @Nonnull
    public static StructuredArgument entries(@Nonnull Dumpable dumpable) {
        requireNonNull(dumpable);

        var map = dumpable.toMap(null);
        return StructuredArguments.entries(map);
    }

    @SuppressWarnings("null")
    @Nonnull
    public static StructuredArgument kv(@Nonnull String key, @Nullable Dumpable dumpable) {
        requireNonNull(key);

        var map = (dumpable == null) ? null : dumpable.toMap(null);
        return StructuredArguments.kv(key, map);
    }

}

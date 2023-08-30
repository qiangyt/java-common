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

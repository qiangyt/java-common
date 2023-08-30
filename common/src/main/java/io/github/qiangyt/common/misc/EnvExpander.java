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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

import org.apache.commons.text.StringSubstitutor;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.Getter;

public class EnvExpander {

    @Getter
    private StringSubstitutor substitutor;

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, String> envVars;

    public EnvExpander() {
        this(true, true);
    }

    public EnvExpander(boolean loadDotEnv, boolean loadSystemEnv) {
        this.envVars = new HashMap<>();

        if (loadSystemEnv) {
            this.envVars.putAll(System.getenv());
        }

        if (loadDotEnv) {
            var dotenv = Dotenv.configure().ignoreIfMissing().load();
            for (var e : dotenv.entries()) {
                this.envVars.put(e.getKey(), e.getValue());
            }
        }

        this.substitutor = new StringSubstitutor(this.envVars);
    }

    public EnvExpander set(@Nonnull String var, String value) {
        this.envVars.put(var, value);
        this.substitutor = new StringSubstitutor(this.envVars);

        return this;
    }

    public EnvExpander set(@Nonnull Map<String, String> vars) {
        this.envVars.putAll(vars);
        this.substitutor = new StringSubstitutor(this.envVars);

        return this;
    }

    public @Nonnull String expand(@Nonnull String input) {
        return requireNonNull(getSubstitutor().replace(input));
    }

    public @Nonnull String[] expand(@Nonnull String[] args) {
        String[] r = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            r[i] = expand(requireNonNull(args[i]));
        }
        return r;
    }

    public @Nonnull File expand(@Nonnull File input) {
        requireNonNull(input);
        return new File(expand(requireNonNull(input.getAbsolutePath())));
    }

}

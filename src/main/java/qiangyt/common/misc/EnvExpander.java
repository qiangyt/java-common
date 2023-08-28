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

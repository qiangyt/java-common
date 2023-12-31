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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

import org.apache.commons.text.StringSubstitutor;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

@Getter
public class EnvExpander {

    private static final ThreadLocal<EnvExpander> CURRENT = new ThreadLocal<>();

    @Nonnull
    private StringSubstitutor substitutor;

    @Nonnull
    private final Map<String, String> envVars;

    public EnvExpander() {
        this(true, true, null);
    }

    public EnvExpander(@Nonnull Map<String, String> extra) {
        this(true, true, extra);
    }

    public EnvExpander(@Nonnull EnvExpander parent) {
        this(false, false, parent.envVars);
    }

    public EnvExpander(boolean loadDotEnv, boolean loadSystemEnv, Map<String, String> extra) {
        this.envVars = (extra == null) ? new HashMap<>() : new HashMap<>(extra);

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

    @Nonnull
    public EnvExpander set(@Nonnull String var, String value) {
        this.envVars.put(var, value);
        this.substitutor = new StringSubstitutor(this.envVars);

        return this;
    }

    @Nonnull
    public EnvExpander set(@Nonnull Map<String, String> vars) {
        this.envVars.putAll(vars);
        this.substitutor = new StringSubstitutor(this.envVars);

        return this;
    }

    public static void setCurrent(@Nonnull EnvExpander expander) {
        CURRENT.set(expander);
    }

    public static EnvExpander getCurrent() {
        return CURRENT.get();
    }

    public static String expands(@Nonnull String input) {
        var inst = CURRENT.get();
        requireNonNull(inst);

        return inst.expand(input);
    }

    public static String tryExpands(@Nonnull String input) {
        var inst = CURRENT.get();
        if (inst == null) {
            return input;
        }

        return inst.expand(input);
    }

    public static File tryExpands(@Nonnull File input) {
        var inst = CURRENT.get();
        if (inst == null) {
            return input;
        }

        return inst.expand(input);
    }

    public @Nonnull String expand(@Nonnull String input) {
        return requireNonNull(getSubstitutor().replace(input));
    }

    public @Nonnull String expand(@Nonnull String text, Function<String, String> func) {
        CURRENT.set(this);
        try {
            return func.apply(text);
        } finally {
            CURRENT.remove();
        }
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
        return new File(expand(requireNonNull(input.getPath())));
    }

}

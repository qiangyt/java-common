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
import java.util.Arrays;
import java.util.Map;
import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;
import org.buildobjects.process.ExternalProcessFailureException;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.buildobjects.process.StartupException;
import org.buildobjects.process.TimeoutException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcBuilderExt extends ProcBuilder {

    public static final short DEFAULT_TIMEOUT_SECONDS = 60;

    @Nonnull
    private final EnvExpander envExpander;

    @SuppressWarnings("null")
    @Nonnull
    public static Pair<String, String[]> splitCommandAndArgs(@Nonnull String cmd) {
        requireNonNull(cmd);

        var cmdAndArgs = cmd.split(" ");
        return Pair.of(cmdAndArgs[0], Arrays.copyOfRange(cmdAndArgs, 1, cmdAndArgs.length));
    }

    public ProcBuilderExt(@Nonnull EnvExpander envExpander, @Nonnull String command) {
        this(envExpander, splitCommandAndArgs(command));
    }

    public ProcBuilderExt(@Nonnull EnvExpander envExpander, @Nonnull Pair<String, String[]> cmdAndArgs) {
        this(envExpander, requireNonNull(cmdAndArgs.getLeft()), requireNonNull(cmdAndArgs.getRight()));
    }

    public ProcBuilderExt(@Nonnull EnvExpander envExpander, @Nonnull String command, @Nonnull String[] args) {
        super(envExpander.expand(command), envExpander.expand(requireNonNull(args)));

        this.envExpander = envExpander;
        withVars(envExpander.getEnvVars());
    }

    @Override
    public ProcBuilder withWorkingDirectory(File directory) {
        requireNonNull(directory);
        super.withWorkingDirectory(getEnvExpander().expand(directory));
        return this;
    }

    @Override
    public ProcBuilderExt withVar(String var, String value) {
        requireNonNull(var);

        super.withVar(var, value);
        getEnvExpander().set(var, value);
        return this;
    }

    @Override
    public ProcBuilderExt withVars(Map<String, String> vars) {
        requireNonNull(vars);

        super.withVars(vars);
        getEnvExpander().set(vars);
        return this;
    }

    @Override
    public ProcResult run() throws StartupException, TimeoutException, ExternalProcessFailureException {
        return super.run();
    }

}

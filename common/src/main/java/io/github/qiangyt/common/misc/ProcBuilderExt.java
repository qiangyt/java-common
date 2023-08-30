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

    public static Pair<String, String[]> splitCommandAndArgs(String cmd) {
        var cmdAndArgs = cmd.split(" ");
        return Pair.of(cmdAndArgs[0], Arrays.copyOfRange(cmdAndArgs, 1, cmdAndArgs.length));
    }

    public ProcBuilderExt(@Nonnull String command) {
        this(splitCommandAndArgs(command));
    }

    private ProcBuilderExt(Pair<String, String[]> cmdAndArgs) {
        this(new EnvExpander(), requireNonNull(cmdAndArgs.getLeft()), cmdAndArgs.getRight());
    }

    public ProcBuilderExt(@Nonnull EnvExpander envExpander, @Nonnull String command, String[] args) {
        super(envExpander.expand(command), envExpander.expand(requireNonNull(args)));

        this.envExpander = envExpander;
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

    public static String run(@Nonnull String cmd, String... args) {
        var builder = (new ProcBuilderExt(cmd)).withArgs(args);
        return builder.run().getOutputString();
    }

    public static String filter(@Nonnull String input, String cmd, String... args) {
        requireNonNull(cmd);

        var builder = (new ProcBuilderExt(cmd)).withArgs(args).withInput(input);
        return builder.run().getOutputString();
    }

}
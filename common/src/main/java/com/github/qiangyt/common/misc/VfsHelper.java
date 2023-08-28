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
package com.github.qiangyt.common.misc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.util.FileObjectUtils;

import com.github.qiangyt.common.err.BadStateException;

public class VfsHelper {

    /**
     * Resolve a file by path.
     *
     * @param path
     *            path
     *
     * @return file object
     */
    public static @Nonnull FileObject resolveFile(@Nonnull String path) {
        requireNonNull(path);

        FileObject r;
        try {
            r = VFS.getManager().resolveFile(new File(""), path);
        } catch (IOException e) {
            throw new BadStateException(e);
        }

        requireNonNull(r);
        return r;
    }

    public static boolean fileExists(@Nonnull String path) {
        var f = resolveFile(path);
        return fileExists(f);
    }

    public static boolean fileExists(@Nonnull FileObject file) {
        requireNonNull(file);

        try {
            return file.exists();
        } catch (FileSystemException e) {
            throw new BadStateException(e);
        }
    }

    public static @Nonnull String readFileText(@Nonnull String path) {
        var f = resolveFile(path);
        return readFileText(f);
    }

    public static @Nonnull String readFileText(@Nonnull FileObject file) {
        requireNonNull(file);

        String r;
        try {
            r = FileObjectUtils.getContentAsString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new BadStateException(e);
        }

        requireNonNull(r);
        return r;
    }

    public static void writeFileText(@Nonnull String path, @Nonnull String text) {
        var f = resolveFile(path);
        writeFileText(f, text);
    }

    public static void writeFileText(@Nonnull FileObject file, @Nonnull String text) {
        requireNonNull(file);
        requireNonNull(text);

        try (FileContent content = file.getContent()) {
            try (var output = content.getOutputStream(false)) {
                var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
                writer.write(text);
            }
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

}

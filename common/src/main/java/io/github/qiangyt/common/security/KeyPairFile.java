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
package io.github.qiangyt.common.security;

import static java.util.Objects.requireNonNull;

import java.security.KeyPair;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.shredzone.acme4j.util.KeyPairUtils;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.EnvExpander;
import io.github.qiangyt.common.misc.VfsHelper;
import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public class KeyPairFile {

    KeyPair content;

    final String file;

    FileObject fileExpanded;

    public KeyPairFile(@Nonnull String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return getFile();
    }

    public void resolveContent(@Nonnull EnvExpander expander, int newKeySize) {
        var exists = readContent(expander);
        if (exists == false) {
            createNewKey(newKeySize);
        }
    }

    public boolean readContent(@Nonnull EnvExpander expander) {
        var expanded = (expander == null) ? file : expander.expand(file);
        this.fileExpanded = VfsHelper.resolveFile(expanded);

        if (VfsHelper.fileExists(this.fileExpanded)) {
            this.content = KeysHelper.readKeyPairFile(expanded);
            return true;
        }

        return false;
    }

    public void writeContent(KeyPair content) {
        KeysHelper.writeKeyPairFile(content, getFileExpanded());
        this.content = content;
    }

    public void createNewKeyIfNotExists(int newKeySize) {
        try {
            if (getFileExpanded().exists()) {
                return;
            }
        } catch (FileSystemException e) {
            throw new BadStateException(e);
        }

        createNewKey(newKeySize);
    }

    public void createNewKey(int newKeySize) {
        var k = KeyPairUtils.createKeyPair(newKeySize);
        requireNonNull(k);
        writeContent(k);
    }

}

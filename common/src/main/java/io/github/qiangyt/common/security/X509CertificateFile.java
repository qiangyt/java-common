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

import java.io.File;
import java.security.cert.X509Certificate;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.shredzone.acme4j.Certificate;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.EnvExpander;
import io.github.qiangyt.common.misc.VfsHelper;
import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public class X509CertificateFile {

    X509Certificate content;

    final FileObject file;

    FileObject fileExpanded;

    public X509CertificateFile(@Nonnull File file) {
        this(file.getPath());
    }

    public X509CertificateFile(@Nonnull String path) {
        this(VfsHelper.resolveFile(path));
    }

    public X509CertificateFile(@Nonnull FileObject file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return getFile().getPath().toString();
    }

    public boolean readContent(@Nonnull EnvExpander expander) {
        var expanded = (expander == null) ? file : expander.expand(file);
        this.fileExpanded = expanded;

        try {
            if (expanded.exists()) {
                this.content = KeysHelper.readCertPemFile(expanded);
                return true;
            }
        } catch (FileSystemException e) {
            throw new BadStateException(e);
        }

        return false;
    }

    public void writeContent(Certificate content) {
        KeysHelper.writeCertificateFile(content, getFileExpanded());
        this.content = content.getCertificate();// TODO
    }

}

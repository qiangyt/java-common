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

import java.security.cert.CertificateFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

import jakarta.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.Codec;
import io.github.qiangyt.common.misc.VfsHelper;

public class KeysHelper {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static boolean isKey(@Nonnull String keyText) {
        return keyText.contains("-----BEGIN");
    }

    public static @Nonnull KeyPair readKeyPair(@Nonnull Reader reader) {
        requireNonNull(reader);

        try {
            var r = KeyPairUtils.readKeyPair(reader);
            requireNonNull(r);
            return r;
        } catch (IOException e) {
            throw new CertifyException(e);
        }
    }

    public static @Nonnull KeyPair readKeyPair(@Nonnull String text) {
        requireNonNull(text);

        try (var reader = new StringReader(text)) {
            return readKeyPair(reader);
        }
    }

    public static @Nonnull KeyPair readKeyPairFile(@Nonnull String file) {
        requireNonNull(file);

        var fo = VfsHelper.resolveFile(file);
        return readKeyPairFile(fo);
    }

    public static @Nonnull KeyPair readKeyPairFile(@Nonnull FileObject keyPairFile) {
        requireNonNull(keyPairFile);

        String keyPairText = VfsHelper.readFileText(keyPairFile);
        return readKeyPair(keyPairText);
    }

    @Nonnull
    public static KeyPair loadOrCreateKeyPairFile(int keySize, @Nonnull String keyPairFilePath) {
        requireNonNull(keyPairFilePath);

        var fo = VfsHelper.resolveFile(keyPairFilePath);
        return loadOrCreateKeyPairFile(keySize, fo);
    }

    @Nonnull
    public static KeyPair loadOrCreateKeyPairFile(int keySize, @Nonnull FileObject keyPairFile) {
        requireNonNull(keyPairFile);

        try {
            if (keyPairFile.exists()) {
                // If there is a key file, read it
                return readKeyPairFile(keyPairFile);
            }
        } catch (FileSystemException e) {
            throw new CertifyException(e);
        }

        // If there is none, create a new key pair and save it
        var r = KeyPairUtils.createKeyPair(keySize);
        requireNonNull(r);
        writeKeyPairFile(r, keyPairFile);

        return r;
    }

    public static @Nonnull KeyPair createKeyPair(int keySize) {
        var r = KeyPairUtils.createKeyPair(keySize);
        requireNonNull(r);
        return r;
    }

    public static void writeKeyPair(@Nonnull KeyPair keyPair, @Nonnull Writer writer) {
        requireNonNull(keyPair);
        requireNonNull(writer);

        try {
            KeyPairUtils.writeKeyPair(keyPair, writer);
        } catch (IOException e) {
            throw new CertifyException(e);
        }
    }

    public static void writeKeyPairFile(@Nonnull KeyPair keyPair, @Nonnull String keyPairFilePath) {
        requireNonNull(keyPair);
        requireNonNull(keyPairFilePath);

        var fo = VfsHelper.resolveFile(keyPairFilePath);
        writeKeyPairFile(keyPair, fo);
    }

    public static void writeKeyPairFile(@Nonnull KeyPair keyPair, @Nonnull FileObject keyPairFile) {
        requireNonNull(keyPair);
        requireNonNull(keyPairFile);

        try (FileContent content = keyPairFile.getContent()) {
            try (var output = content.getOutputStream(false)) {
                var writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
                writeKeyPair(keyPair, writer);
            }
        } catch (IOException e) {
            throw new CertifyException(e);
        }
    }

    // Generate a CSR for all of the domains, and sign it with the domain key pair.
    public static @Nonnull CertifySR createAndSignCSR(@Nonnull Collection<String> domains, @Nonnull KeyPair keyPair) {
        requireNonNull(domains);
        requireNonNull(keyPair);

        var csrb = new CSRBuilder();
        csrb.addDomains(domains);
        try {
            csrb.sign(keyPair);
        } catch (IOException e) {
            throw new CertifyException(e);
        }

        return CertifySR.build(csrb);
    }

    // Write a combined file containing the certificate and chain.
    public static @Nonnull String writeCertificate(@Nonnull Certificate cert) {
        requireNonNull(cert);

        // Write the CSR
        try (var w = new StringWriter()) {
            cert.writeCertificate(w);

            var r = w.toString();
            requireNonNull(r);
            return r;
        } catch (IOException e) {
            throw new CertifyException(e);
        }
    }

    public static void writeCertificateFile(@Nonnull Certificate cert, @Nonnull String certFilePath) {
        requireNonNull(cert);
        requireNonNull(certFilePath);

        var fo = VfsHelper.resolveFile(certFilePath);
        writeCertificateFile(cert, fo);
    }

    public static void writeCertificateFile(@Nonnull Certificate cert, @Nonnull FileObject certFile) {
        requireNonNull(cert);
        requireNonNull(certFile);

        try (FileContent content = certFile.getContent()) {
            try (var output = content.getOutputStream(false)) {
                var w = new OutputStreamWriter(output, StandardCharsets.UTF_8);
                cert.writeCertificate(w);
                w.flush();
            }
        } catch (IOException e) {
            throw new CertifyException(e);
        }
    }

    public static X509Certificate readCertPem(@Nonnull String pemText) {
        try (var reader = new StringReader(pemText);) {
            PEMParser p = new PEMParser(new StringReader(pemText));
            var holder = (X509CertificateHolder) p.readObject();
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
        } catch (IOException | CertificateException e) {
            throw new CertifyException(e);
        }
    }

    @Nonnull
    public static X509Certificate loadCertPemFile(@Nonnull String pemFilePath) {
        var r = readCertPemFile(pemFilePath);
        if (r == null) {
            throw new BadStateException("cert file not found: " + pemFilePath);
        }
        return r;
    }

    public static X509Certificate readCertPemFile(@Nonnull String pemFilePath) {
        requireNonNull(pemFilePath);

        var fo = VfsHelper.resolveFile(pemFilePath);
        return readCertPemFile(fo);
    }

    public static X509Certificate readCertPemFile(@Nonnull FileObject permFile) {
        requireNonNull(permFile);
        try {
            if (!permFile.exists()) {
                return null;
            }
        } catch (FileSystemException e) {
            throw new BadStateException(e);
        }

        String pemText = VfsHelper.readFileText(permFile);
        return requireNonNull(readCertPem(pemText));
    }

    public static @Nonnull List<X509Certificate> readCertsPem(@Nonnull String pemText) {
        requireNonNull(pemText);

        pemText = pemText.replace(KeyLabel.CERTIFICATE.beginLine, "").replace(KeyLabel.CERTIFICATE.endLine, "");
        // .replaceAll("\r\n", "").replaceAll("\n\r", "").replaceAll("\n", "");

        byte[] encoded = Codec.base64ToBytes(pemText);
        try (InputStream in = new ByteArrayInputStream(encoded)) {
            var cf = CertificateFactory.getInstance("X.509");
            var r = cf.generateCertificates(in).stream().map(c -> (X509Certificate) c).collect(toList());
            requireNonNull(r);
            return r;
        } catch (IOException | CertificateException e) {
            throw new CertifyException(e);
        }
    }

    public static List<X509Certificate> readCertsPemFile(@Nonnull String pemFilePath) {
        requireNonNull(pemFilePath);

        var fo = VfsHelper.resolveFile(pemFilePath);
        return readCertsPemFile(fo);
    }

    public static List<X509Certificate> readCertsPemFile(@Nonnull FileObject permFile) {
        requireNonNull(permFile);
        try {
            if (!permFile.exists()) {
                return null;
            }
        } catch (FileSystemException e) {
            throw new BadStateException(e);
        }

        String pemText = VfsHelper.readFileText(permFile);
        return readCertsPem(pemText);
    }

}

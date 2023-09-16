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

import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.shredzone.acme4j.util.CSRBuilder;

import lombok.Getter;
import io.github.qiangyt.common.bean.Dumpable;
import io.github.qiangyt.common.misc.Codec;

@Getter
public class CertifySR implements Dumpable {

    @Nonnull
    final String text;

    @Nonnull
    final byte[] encoded;

    public CertifySR(@Nonnull String text, @Nonnull byte[] encoded) {
        this.text = requireNonNull(text);
        this.encoded = requireNonNull(encoded);
    }

    public static @Nonnull CertifySR build(@Nonnull CSRBuilder builder) {
        requireNonNull(builder);

        // Write the CSR
        try (var w = new StringWriter()) {
            builder.write(w);
            var text = w.toString();
            requireNonNull(text);

            var encoded = builder.getEncoded();
            requireNonNull(encoded);

            return new CertifySR(text, encoded);
        } catch (IOException e) {
            throw new CertifyException(e);
        }
    }

    @Override
    @SuppressWarnings({ "null", "unchecked" })
    public @Nonnull Map<String, Object> toMap(Map<Object, Object> visited) {
        if (visited == null) {
            visited = new HashMap<>();
        }

        return (Map<String, Object>) visited.computeIfAbsent(this, k -> {
            var r = new HashMap<String, Object>();
            r.put("text", getText());
            r.put("encoded", Codec.encodeHex(getEncoded()));
            return r;
        });
    }

    @Override
    public String toString() {
        return requireNonNull(dumpAsYaml(null));
    }

}

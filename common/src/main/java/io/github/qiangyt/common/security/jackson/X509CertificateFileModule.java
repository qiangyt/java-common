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
package io.github.qiangyt.common.security.jackson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.security.X509CertificateFile;
import jakarta.annotation.Nonnull;

public class X509CertificateFileModule {

    public static class Serializer extends JacksonSerializer<X509CertificateFile> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void serialize(@Nonnull X509CertificateFile value, @Nonnull JsonGenerator gen) throws Exception {
            gen.writeString(value.getFile());
        }

        @Override
        protected void dump(@Nonnull X509CertificateFile value, @Nonnull JsonGenerator gen) throws Exception {
            Map<String, Object> map = new HashMap<>();
            map.put("file", value.getFile());
            map.put("content", X509CertificateModule.Serializer.staticDump(value.getContent()));

            gen.writeObject(map);
        }
    }

    public static class Deserializer extends JacksonDeserializer<X509CertificateFile> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected X509CertificateFile deserialize(@Nonnull String path) throws Exception {
            return new X509CertificateFile(path);
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(X509CertificateFile.class, new Serializer(dump));
        r.addDeserializer(X509CertificateFile.class, new Deserializer(expandEnv));
        return r;
    }

}

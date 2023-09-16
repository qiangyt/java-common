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

import org.shredzone.acme4j.Certificate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadValueException;
import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.security.KeysHelper;
import jakarta.annotation.Nonnull;

public class CertificateModule {

    public static class Serializer extends JacksonSerializer<Certificate> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void dump(Certificate value, @Nonnull JsonGenerator gen) throws Exception {
            var r = new HashMap<String, Object>();
            r.put("location", value.getLocation());
            // r.put("alternateCertificates", value.getAlternateCertificates());
            r.put("alternates", value.getAlternates());
            r.put("certificateChain", value.getCertificateChain());
            r.put("certificate", value.getCertificate());
            r.put("text", KeysHelper.writeCertificate(value));

            gen.writeObject(r);

        }

        @Override
        protected void serialize(Certificate value, @Nonnull JsonGenerator gen) throws Exception {
            var text = KeysHelper.writeCertificate(value);
            gen.writeString(text);
        }

    }

    public static class Deserializer extends JacksonDeserializer<Certificate> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected Certificate deserialize(@Nonnull String text) throws Exception {
            throw new BadValueException("%s deserialization is NOT supported", Certificate.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(Certificate.class, new Serializer(dump));
        r.addDeserializer(Certificate.class, new Deserializer(expandEnv));
        return r;
    }

}

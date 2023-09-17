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

import java.io.StringWriter;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import org.shredzone.acme4j.util.KeyPairUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.security.KeysHelper;
import jakarta.annotation.Nonnull;

public class KeyPairModule {

    public static class Serializer extends JacksonSerializer<KeyPair> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void serialize(@Nonnull KeyPair value, @Nonnull JsonGenerator gen) throws Exception {
            staticSerialize(value, gen);
        }

        public static void staticSerialize(@Nonnull KeyPair value, @Nonnull JsonGenerator gen) throws Exception {
            var w = new StringWriter();
            KeyPairUtils.writeKeyPair(value, w);

            gen.writeString(w.toString());
        }

        @Override
        protected void dump(@Nonnull KeyPair value, @Nonnull JsonGenerator gen) throws Exception {
            gen.writeObject(staticDump(value));
        }

        public static Map<String, Object> staticDump(@Nonnull KeyPair value) {
            var r = new HashMap<String, Object>();

            if (value == null) {
                r.put("private", null);
                r.put("public", null);
            } else {
                r.put("private", value.getPrivate());
                r.put("public", value.getPublic());
            }

            return r;
        }
    }

    public static class Deserializer extends JacksonDeserializer<KeyPair> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected KeyPair deserialize(@Nonnull String text) throws Exception {
            return staticDeserialize(text);
        }

        public static KeyPair staticDeserialize(@Nonnull String text) throws Exception {
            return KeysHelper.readKeyPair(text);
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(KeyPair.class, new Serializer(dump));
        r.addDeserializer(KeyPair.class, new Deserializer(expandEnv));
        return r;
    }

}

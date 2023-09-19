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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadValueException;
import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.security.KeyPairFile;
import jakarta.annotation.Nonnull;

public class KeyPairFileModule {

    public static class Serializer extends JacksonSerializer<KeyPairFile> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void serialize(@Nonnull KeyPairFile value, @Nonnull JsonGenerator gen) throws Exception {
            gen.writeString(value.getFile());
        }

        @Override
        protected void dump(@Nonnull KeyPairFile value, @Nonnull JsonGenerator gen) {
            Map<String, Object> map = new HashMap<>();
            map.put("file", value.getFile());
            map.put("content", KeyPairModule.Serializer.staticDump(value.getContent()));

            try {
                gen.writeObject(map);
            } catch (IOException e) {
                throw new BadValueException(e);
            }
        }
    }

    public static class Deserializer extends JacksonDeserializer<KeyPairFile> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected KeyPairFile deserialize(@Nonnull String path) throws Exception {
            return new KeyPairFile(path);
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(KeyPairFile.class, new Serializer(dump));
        r.addDeserializer(KeyPairFile.class, new Deserializer(expandEnv));
        return r;
    }

}

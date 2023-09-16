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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.misc.VfsHelper;
import io.github.qiangyt.common.security.KeyPairSource;
import io.github.qiangyt.common.security.KeysHelper;
import jakarta.annotation.Nonnull;

public class KeyPairSourceModule {

    public static class Serializer extends JacksonSerializer<KeyPairSource> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void serialize(@Nonnull KeyPairSource value, @Nonnull JsonGenerator gen) throws Exception {
            if (value.getFile() != null) {
                gen.writeString(value.getFile().getPath().toString());
                return;
            }

            KeyPairModule.Serializer.staticSerialize(value.getData(), gen);
        }

        @Override
        protected void dump(@Nonnull KeyPairSource value, @Nonnull JsonGenerator gen) throws Exception {
            if (value.getFile() != null) {
                gen.writeString(value.getFile().getPath().toString());
                return;
            }

            KeyPairModule.Serializer.staticDump(value.getData(), gen);
        }
    }

    public static class Deserializer extends JacksonDeserializer<KeyPairSource> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected KeyPairSource deserialize(@Nonnull String text) throws Exception {
            var builder = KeyPairSource.builder();

            if (KeysHelper.isKey(text)) {
                var data = KeyPairModule.Deserializer.staticDeserialize(text);
                builder.data(data);
            } else {
                var file = VfsHelper.resolveFile(text);
                builder.file(file);
                builder.data(KeysHelper.readKeyPairFile(file));
            }

            return builder.build();
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(KeyPairSource.class, new Serializer(dump));
        r.addDeserializer(KeyPairSource.class, new Deserializer(expandEnv));
        return r;
    }

}

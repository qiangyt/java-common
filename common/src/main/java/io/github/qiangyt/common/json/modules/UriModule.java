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
package io.github.qiangyt.common.json.modules;

import java.net.URI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import jakarta.annotation.Nonnull;

public class UriModule {

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(URI.class, new Serializer(dump));
        r.addDeserializer(URI.class, new Deserializer(expandEnv));
        return r;
    }

    public static class Serializer extends JacksonSerializer<URI> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void dump(URI value, @Nonnull JsonGenerator gen) throws Exception {
            gen.writeString(value.toString());
        }
    }

    public static class Deserializer extends JacksonDeserializer<URI> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected URI deserialize(@Nonnull String text) throws Exception {
            return new URI(text);
        }
    }

}

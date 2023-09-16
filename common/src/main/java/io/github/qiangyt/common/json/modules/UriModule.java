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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadValueException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UriModule {

    @Nonnull
    public static SimpleModule build() {
        var r = new SimpleModule();
        r.addSerializer(URI.class, new Serializer());
        r.addDeserializer(URI.class, new Deserialize());
        return r;
    }

    public static class Serializer extends JsonSerializer<URI> {

        @Override
        public void serialize(URI value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toString());
            }
        }
    }

    public static class Deserialize extends JsonDeserializer<URI> {

        @Override
        public URI deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

            String valueText = p.getValueAsString();

            try {
                return new URI(valueText);
            } catch (URISyntaxException ex) {
                throw new BadValueException(ex, "%s is NOT a URI value", valueText);
            }
        }
    }

}

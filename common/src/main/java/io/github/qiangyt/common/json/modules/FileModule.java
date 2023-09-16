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
import java.io.File;

import jakarta.annotation.Nonnull;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadValueException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class FileModule {

    @Nonnull
    public static SimpleModule build() {
        var r = new SimpleModule();
        r.addSerializer(File.class, new Serializer());
        r.addDeserializer(File.class, new Deserialize());
        return r;
    }

    public static class Serializer extends JsonSerializer<File> {

        @Override
        public void serialize(File value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.getPath());
            }
        }
    }

    public static class Deserialize extends JsonDeserializer<File> {

        @Override
        public File deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

            String valueText = p.getValueAsString();

            try {
                return new File(valueText);
            } catch (NumberFormatException ex) {
                throw new BadValueException(ex, "%s is NOT a long value", valueText);
            }
        }
    }

}

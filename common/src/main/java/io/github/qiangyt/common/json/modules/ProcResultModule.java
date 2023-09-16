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
import java.util.HashMap;

import javax.annotation.Nonnull;

import org.buildobjects.process.ProcResult;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadValueException;

public class ProcResultModule {

    @Nonnull
    public static SimpleModule build() {
        var r = new SimpleModule();

        r.addSerializer(ProcResult.class, new ProcResultSerializer());
        r.addDeserializer(ProcResult.class, new ProcResultDeserializer());

        return r;
    }

    public static class ProcResultSerializer extends JsonSerializer<ProcResult> {

        @Override
        public void serialize(ProcResult value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var r = new HashMap<String, Object>();
                r.put("exitValue", value.getExitValue());
                r.put("stdout", value.getOutputString());
                r.put("stderr", value.getErrorString());
                r.put("executionTime", value.getExecutionTime());
                r.put("commandLine", value.getCommandLine());

                gen.writeObject(r);
            }
        }
    }

    public static class ProcResultDeserializer extends JsonDeserializer<ProcResult> {

        @Override
        public ProcResult deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            throw new BadValueException("%s deserialization is NOT supported", ProcResult.class.getSimpleName());
        }
    }

}

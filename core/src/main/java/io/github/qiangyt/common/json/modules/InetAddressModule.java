/*
 * io.github.qiangyt:qiangyt-common-core - Common library by Yiting Qiang
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
import java.net.InetSocketAddress;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.JsonProcessingException;

public class InetAddressModule {

    public static SimpleModule build() {
        var r = new SimpleModule();
        r.addSerializer(InetSocketAddress.class, new Serializer());
        r.addDeserializer(InetSocketAddress.class, new Deserializer());
        return r;
    }

    public static class Serializer extends JsonSerializer<InetSocketAddress> {

        @Override
        public void serialize(InetSocketAddress value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var text = value.getHostString() + ":" + value.getPort();
                gen.writeString(text);
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<InetSocketAddress> {

        @Override
        public InetSocketAddress deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            String valueText = p.getValueAsString();

            var idx = valueText.lastIndexOf(':');
            var host = valueText.substring(0, idx);
            var port = Integer.valueOf(valueText.substring(idx + 1));
            return new InetSocketAddress(host, port);
        }
    }

}

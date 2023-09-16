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

import org.shredzone.acme4j.challenge.Dns01Challenge;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import jakarta.annotation.Nonnull;

public class Dns01ChallengeModule {

    public static class Serializer extends JacksonSerializer<Dns01Challenge> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void dump(Dns01Challenge value, @Nonnull JsonGenerator gen) throws Exception {
            var r = new HashMap<String, Object>();
            r.put("authorization", value.getAuthorization());
            r.put("digest", value.getDigest());
            r.put("error", value.getError());
            r.put("location", value.getLocation());
            r.put("type", value.getType());
            r.put("validated", value.getValidated());
            r.put("status", value.getStatus());

            gen.writeObject(r);
        }
    }

    public static class Deserializer extends JacksonDeserializer<Dns01Challenge> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(Dns01Challenge.class, new Serializer(dump));
        r.addDeserializer(Dns01Challenge.class, new Deserializer(expandEnv));
        return r;
    }

}

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

import org.shredzone.acme4j.Order;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import jakarta.annotation.Nonnull;

public class OrderModule {

    public static class Serializer extends JacksonSerializer<Order> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void dump(Order value, @Nonnull JsonGenerator gen) throws Exception {

            var r = new HashMap<String, Object>();
            r.put("location", value.getLocation());
            r.put("status", value.getStatus());
            r.put("certificate", value.getCertificate());
            r.put("expires", value.getExpires());
            r.put("authorizations", value.getAuthorizations());
            r.put("autoRenewalCertificate", value.getAutoRenewalCertificate());
            r.put("autoRenewalEndDate", value.getAutoRenewalEndDate());
            r.put("autoRenewalLifetimeAdjust", value.getAutoRenewalLifetimeAdjust());
            r.put("autoRenewalStartDate", value.getAutoRenewalStartDate());
            r.put("autoRenewalLifetime", value.getAutoRenewalLifetime());
            r.put("autoRenewalGetEnabled", value.isAutoRenewalGetEnabled());
            r.put("autoRenewing", value.isAutoRenewing());
            r.put("error", value.getError());
            r.put("finalizeLocation", value.getFinalizeLocation());
            r.put("identifiers", value.getIdentifiers());
            r.put("notBefore", value.getNotBefore());
            r.put("notAfter", value.getNotAfter());

            gen.writeObject(r);

        }

    }

    public static class Deserializer extends JacksonDeserializer<Order> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(Order.class, new Serializer(dump));
        r.addDeserializer(Order.class, new Deserializer(expandEnv));
        return r;
    }

}

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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.misc.Codec;
import io.github.qiangyt.common.misc.StringHelper;
import io.github.qiangyt.common.security.KeysHelper;
import jakarta.annotation.Nonnull;

public class X509CertificateModule {

    public static class Serializer extends JacksonSerializer<X509Certificate> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void dump(X509Certificate value, @Nonnull JsonGenerator gen) throws Exception {
            var map = staticDump(value);
            gen.writeObject(map);
        }

        public static Map<String, Object> staticDump(X509Certificate value) {
            var r = new HashMap<String, Object>();
            try {
                r.put("notBefore", value.getNotBefore());
                r.put("notAfter", value.getNotAfter());
                r.put("serialNumber", value.getSerialNumber());
                r.put("signature", Codec.bytesToBase64(value.getSignature()));
                r.put("type", value.getType());
                r.put("version", value.getVersion());
                r.put("publicKey", value.getPublicKey());
                r.put("sigAlgName", value.getSigAlgName());
                r.put("sigAlgOID", value.getSigAlgOID());
                r.put("sigAlgParams", Codec.bytesToBase64(value.getSigAlgParams()));
                r.put("TBSCertificate", Codec.bytesToBase64(value.getTBSCertificate()));
                r.put("subjectUniqueID", value.getSubjectUniqueID());
                r.put("subjectX500Principal", value.getSubjectX500Principal());
                r.put("basicConstraints", value.getBasicConstraints());
                r.put("extendedKeyUsage", value.getExtendedKeyUsage());
                r.put("criticalExtensionOIDs", value.getCriticalExtensionOIDs());
                r.put("issuerAlternativeNames", value.getIssuerAlternativeNames());
                r.put("issuerUniqueID", StringHelper.toString(value.getIssuerUniqueID()));
                r.put("issuerX500Principal", value.getIssuerX500Principal());
                r.put("nonCriticalExtensionOIDs", value.getNonCriticalExtensionOIDs());
                r.put("keyUsage", value.getKeyUsage());
                r.put("subjectAlternativeNames", value.getSubjectAlternativeNames());
                r.put("encoded", Codec.bytesToPem(value.getEncoded(), null));
            } catch (CertificateException e) {
                throw new BadStateException(e);
            }
            return r;
        }
    }

    public static class Deserializer extends JacksonDeserializer<X509Certificate> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Nonnull
        protected X509Certificate deserialize(@Nonnull String text) throws Exception {
            return staticDeserialize(text);
        }

        public static X509Certificate staticDeserialize(@Nonnull String text) throws Exception {
            var list = KeysHelper.readCertsPem(text);
            return list.get(0);
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(X509Certificate.class, new Serializer(dump));
        r.addDeserializer(X509Certificate.class, new Deserializer(expandEnv));
        return r;
    }

}

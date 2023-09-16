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
import java.security.Key;
import java.security.KeyPair;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.Codec;
import io.github.qiangyt.common.misc.StringHelper;

public class SecurityModule {

    public static void build(@Nonnull ObjectMapper mapper) {
        mapper.registerModule(build4Key());
        mapper.registerModule(build4KeyPair());
        mapper.registerModule(build4Principal());
        mapper.registerModule(build4X509Certificate());
    }

    public static class KeySerializer extends JsonSerializer<Key> {

        @SuppressWarnings("null")
        @Override
        public void serialize(Key value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var r = new HashMap<String, Object>();
                r.put("algorithm", value.getAlgorithm());
                r.put("format", value.getFormat());
                r.put("encoded", Codec.bytesToPem(value.getEncoded(), null));

                gen.writeObject(r);
            }
        }
    }

    public static class KeyDeserializer extends JsonDeserializer<Key> {

        @Override
        public Key deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

            throw new BadStateException("%s deserialization is NOT supported", Key.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Key() {
        var r = new SimpleModule();
        r.addSerializer(Key.class, new KeySerializer());
        r.addDeserializer(Key.class, new KeyDeserializer());
        return r;
    }

    public static class KeyPairSerializer extends JsonSerializer<KeyPair> {

        @Override
        public void serialize(KeyPair value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var r = new HashMap<String, Object>();
                r.put("private", value.getPrivate());
                r.put("public", value.getPublic());

                gen.writeObject(r);
            }
        }
    }

    public static class KeyPairDeserializer extends JsonDeserializer<KeyPair> {

        @Override
        public KeyPair deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            throw new BadStateException("%s deserialization is NOT supported", KeyPair.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4KeyPair() {
        var r = new SimpleModule();
        r.addSerializer(KeyPair.class, new KeyPairSerializer());
        r.addDeserializer(KeyPair.class, new KeyPairDeserializer());
        return r;
    }

    public static class PrincipalSerializer extends JsonSerializer<Principal> {

        @Override
        public void serialize(Principal value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var r = new HashMap<String, Object>();
                r.put("name", value.getName());
                r.put("toString", value.toString());

                gen.writeObject(r);
            }
        }
    }

    public static class PrincipalDeserializer extends JsonDeserializer<Principal> {

        @Override
        public Principal deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            throw new BadStateException("%s deserialization is NOT supported", Principal.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Principal() {
        var r = new SimpleModule();
        r.addSerializer(Principal.class, new PrincipalSerializer());
        r.addDeserializer(Principal.class, new PrincipalDeserializer());
        return r;
    }

    public static class X509CertificateSerializer extends JsonSerializer<X509Certificate> {

        @SuppressWarnings("null")
        @Override
        public void serialize(X509Certificate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                try {
                    var r = new HashMap<String, Object>();
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

                    gen.writeObject(r);
                } catch (CertificateException e) {
                    throw new BadStateException(e);
                }
            }
        }
    }

    public static class X509CertificateDeserializer extends JsonDeserializer<X509Certificate> {

        @Override
        public X509Certificate deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {

            throw new BadStateException("%s deserialization is NOT supported", X509Certificate.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4X509Certificate() {
        var r = new SimpleModule();
        r.addSerializer(X509Certificate.class, new X509CertificateSerializer());
        r.addDeserializer(X509Certificate.class, new X509CertificateDeserializer());
        return r;
    }

}

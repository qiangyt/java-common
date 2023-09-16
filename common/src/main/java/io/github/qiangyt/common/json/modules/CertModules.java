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
import io.github.qiangyt.common.security.KeysHelper;

import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Identifier;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Problem;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Dns01Challenge;

import io.github.qiangyt.common.err.BadValueException;

public class CertModules {

    public static void register(@Nonnull ObjectMapper mapper) {
        mapper.registerModule(build4Key());
        mapper.registerModule(build4KeyPair());
        mapper.registerModule(build4Principal());
        mapper.registerModule(build4X509Certificate());

        mapper.registerModule(build4Identifier());
        mapper.registerModule(build4Status());
        mapper.registerModule(build4Authorization());
        mapper.registerModule(build4Account());
        mapper.registerModule(build4Problem());
        mapper.registerModule(build4Dns01Challenge());
        mapper.registerModule(build4Certificate());
        mapper.registerModule(build4Order());
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

    public static class IdentifierSerializer extends JsonSerializer<Identifier> {

        @Override
        public void serialize(Identifier value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeObject(value.toMap());
            }
        }

    }

    public static class IdentifierDeserializer extends JsonDeserializer<Identifier> {

        @Override
        public Identifier deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Identifier.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Identifier() {
        var r = new SimpleModule();
        r.addSerializer(Identifier.class, new IdentifierSerializer());
        r.addDeserializer(Identifier.class, new IdentifierDeserializer());
        return r;
    }

    public static class StatusSerializer extends JsonSerializer<Status> {

        @Override
        public void serialize(Status value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value.toString());
            }
        }
    }

    public static class StatusDeserializer extends JsonDeserializer<Status> {

        @Override
        public Status deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Status.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Status() {
        var r = new SimpleModule();
        r.addSerializer(Status.class, new StatusSerializer());
        r.addDeserializer(Status.class, new StatusDeserializer());
        return r;
    }

    public static class AuthorizationSerializer extends JsonSerializer<Authorization> {

        @Override
        public void serialize(Authorization value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var r = new HashMap<String, Object>();
                r.put("identifier", value.getIdentifier());
                r.put("expires", value.getExpires());
                r.put("location", value.getLocation());
                r.put("wirdcard", value.isWildcard());
                r.put("status", value.getStatus());

                gen.writeObject(r);
            }
        }

    }

    public static class AuthorizationDeserializer extends JsonDeserializer<Authorization> {

        @Override
        public Authorization deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Authorization.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Authorization() {
        var r = new SimpleModule();
        r.addSerializer(Authorization.class, new AuthorizationSerializer());
        r.addDeserializer(Authorization.class, new AuthorizationDeserializer());
        return r;
    }

    public static class AccountSerializer extends JsonSerializer<Account> {

        @Override
        public void serialize(Account value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                var r = new HashMap<String, Object>();
                r.put("location", value.getLocation());
                r.put("keyIdentifier", value.getKeyIdentifier());
                r.put("status", value.getStatus());
                r.put("contact", value.getContacts());
                r.put("termsOfServiceAgreed", value.getTermsOfServiceAgreed());

                gen.writeObject(r);
            }
        }
    }

    public static class AccountDeserializer extends JsonDeserializer<Account> {

        @Override
        public Account deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Account.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Account() {
        var r = new SimpleModule();
        r.addSerializer(Account.class, new AccountSerializer());
        r.addDeserializer(Account.class, new AccountDeserializer());
        return r;
    }

    public static class ProblemSerializer extends JsonSerializer<Problem> {

        @Override
        public void serialize(Problem value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeObject(value.asJSON().toMap());
            }
        }

    }

    public static class ProblemDeserializer extends JsonDeserializer<Problem> {

        @Override
        public Problem deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Problem.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Problem() {
        var r = new SimpleModule();
        r.addSerializer(Problem.class, new ProblemSerializer());
        r.addDeserializer(Problem.class, new ProblemDeserializer());
        return r;
    }

    public static class Dns01ChallengeSerializer extends JsonSerializer<Dns01Challenge> {

        @Override
        public void serialize(Dns01Challenge value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {

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
    }

    public static class Dns01ChallengeDeserializer extends JsonDeserializer<Dns01Challenge> {

        @Override
        public Dns01Challenge deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Dns01Challenge.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Dns01Challenge() {
        var r = new SimpleModule();
        r.addSerializer(Dns01Challenge.class, new Dns01ChallengeSerializer());
        r.addDeserializer(Dns01Challenge.class, new Dns01ChallengeDeserializer());
        return r;
    }

    public static class CertificateSerializer extends JsonSerializer<Certificate> {

        @Override
        public void serialize(Certificate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {

                var r = new HashMap<String, Object>();
                r.put("location", value.getLocation());
                // r.put("alternateCertificates", value.getAlternateCertificates());
                r.put("alternates", value.getAlternates());
                r.put("certificateChain", value.getCertificateChain());
                r.put("certificate", value.getCertificate());
                r.put("text", KeysHelper.writeCertificate(value));

                gen.writeObject(r);
            }
        }

    }

    public static class CertificateDeserializer extends JsonDeserializer<Certificate> {

        @Override
        public Certificate deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Certificate.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Certificate() {
        var r = new SimpleModule();
        r.addSerializer(Certificate.class, new CertificateSerializer());
        r.addDeserializer(Certificate.class, new CertificateDeserializer());
        return r;
    }

    public static class OrderSerializer extends JsonSerializer<Order> {

        @Override
        public void serialize(Order value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException, JsonProcessingException {

            if (value == null) {
                gen.writeNull();
            } else {

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

    }

    public static class OrderDeserializer extends JsonDeserializer<Order> {

        @Override
        public Order deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            throw new BadValueException("%s deserialization is NOT supported", Order.class.getSimpleName());
        }
    }

    @Nonnull
    public static SimpleModule build4Order() {
        var r = new SimpleModule();
        r.addSerializer(Order.class, new OrderSerializer());
        r.addDeserializer(Order.class, new OrderDeserializer());
        return r;
    }

}

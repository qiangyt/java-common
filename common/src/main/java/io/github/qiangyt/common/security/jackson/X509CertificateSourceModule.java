package io.github.qiangyt.common.security.jackson;

import java.io.StringWriter;
import java.util.HashMap;

import org.shredzone.acme4j.util.KeyPairUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.qiangyt.common.json.JacksonDeserializer;
import io.github.qiangyt.common.json.JacksonSerializer;
import io.github.qiangyt.common.misc.VfsHelper;
import io.github.qiangyt.common.security.X509CertificateSource;
import io.github.qiangyt.common.security.KeysHelper;
import jakarta.annotation.Nonnull;

public class X509CertificateSourceModule {
    
    public static class Serializer extends JacksonSerializer<X509CertificateSource> {

        public Serializer(boolean dump) {
            super(dump);
        }

        @Override
        protected void serialize(@Nonnull X509CertificateSource value, @Nonnull JsonGenerator gen) throws Exception {
            if (value.getFile() != null) {
                gen.writeString(value.getFile().getPath().toString());
                return;
            }

            var w = new StringWriter();
            KeyPairUtils.writeKeyPair(value.getData(), w);

            gen.writeString(w.toString());
        }

        @Override
        protected void dump(@Nonnull X509CertificateSource value, @Nonnull JsonGenerator gen) throws Exception {
            if (value.getFile() != null) {
                gen.writeString(value.getFile().getPath().toString());
                return;
            }

            var data = value.getData();
            var r = new HashMap<String, Object>();
            r.put("private", data.getPrivate());
            r.put("public", data.getPublic());

            gen.writeObject(r);
        }
    }

    public static class Deserializer extends JacksonDeserializer<X509CertificateSource> {

        public Deserializer(boolean expandEnv) {
            super(expandEnv);
        }

        @Override
        protected X509CertificateSource deserialize(@Nonnull String text) throws Exception {
            var builder = X509CertificateSource.builder();

            if (KeysHelper.isKey(text)) {
                var data = KeysHelper.readKeyPair(text);
                builder.data(data);
            } else {
                var file = VfsHelper.resolveFile(text);
                builder.file(file);
                builder.data(KeysHelper.readKeyPairFile(file));
            }

            return builder.build();
        }
    }

    @Nonnull
    public static SimpleModule build(boolean expandEnv, boolean dump) {
        var r = new SimpleModule();
        r.addSerializer(X509CertificateSource.class, new Serializer(dump));
        r.addDeserializer(X509CertificateSource.class, new Deserializer(expandEnv));
        return r;
    }

}

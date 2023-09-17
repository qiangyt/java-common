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
package io.github.qiangyt.common.json;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.qiangyt.common.bean.Dumpable;
import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.json.modules.DateModule;
import io.github.qiangyt.common.json.modules.FileModule;
import io.github.qiangyt.common.json.modules.FileObjectModule;
import io.github.qiangyt.common.json.modules.InetAddressModule;
import io.github.qiangyt.common.json.modules.InstantModule;
import io.github.qiangyt.common.json.modules.ProcResultModule;
import io.github.qiangyt.common.json.modules.UriModule;
import io.github.qiangyt.common.json.modules.UrlModule;
import io.github.qiangyt.common.misc.StringHelper;
import io.github.qiangyt.common.security.JacksonModules;
import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
// @ThreadSafe
public class Jackson {

    @Nonnull
    public static final Jackson DEFAULT = new Jackson(buildDefaultMapper(false, false));

    @Nonnull
    public static final Jackson ENV_DEFAULT = new Jackson(buildDefaultMapper(true, false));

    @Nonnull
    public static final Jackson DUMP = new Jackson(buildDefaultMapper(false, false));
    @Nonnull
    public static final Jackson ENV_DUMP = new Jackson(buildDefaultMapper(true, false));

    public final ObjectMapper mapper;

    public Jackson(@Nonnull ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper);
    }

    @Nonnull
    public static ObjectMapper buildDefaultMapper(boolean expandEnv, boolean dump) {
        var r = new ObjectMapper();
        initDefaultMapper(r, expandEnv, dump);
        return r;
    }

    public static void initDefaultMapper(@Nonnull ObjectMapper mapper, boolean expandEnv, boolean dump) {
        requireNonNull(mapper);

        mapper.registerModule(FileModule.build(expandEnv, dump));
        mapper.registerModule(FileObjectModule.build(expandEnv, dump));
        mapper.registerModule(DateModule.build(expandEnv, dump));
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(InstantModule.build(expandEnv, dump));
        mapper.registerModule(ProcResultModule.build(expandEnv, dump));
        mapper.registerModule(UriModule.build(expandEnv, dump));
        mapper.registerModule(UrlModule.build(expandEnv, dump));
        mapper.registerModule(InetAddressModule.build(expandEnv, dump));

        JacksonModules.register(mapper, expandEnv, dump);

        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    }

    public void registerModule(@Nonnull com.fasterxml.jackson.databind.Module module) {
        requireNonNull(module);
        getMapper().registerModule(module);
    }

    public <T> T from(String text, @Nonnull Class<T> clazz) {
        if (StringHelper.isBlank(text)) {
            return null;
        }

        try {
            return getMapper().readValue(text, clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public <T> T from(ByteBuffer buf, @Nonnull Class<T> clazz) {
        if (buf == null) {
            return null;
        }

        try {
            if (!buf.hasArray()) {
                byte[] bytes = new byte[buf.remaining()];
                buf.get(bytes);
                return getMapper().readValue(bytes, clazz);
            }

            final int offset = buf.arrayOffset();
            return getMapper().readValue(buf.array(), offset + buf.position(), buf.remaining(), clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public <T> T from(byte[] bytes, @Nonnull Class<T> clazz) {
        if (bytes == null) {
            return null;
        }

        try {
            return getMapper().readValue(bytes, clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public <T> T from(String text, @Nonnull TypeReference<T> typeReference) {
        if (StringHelper.isBlank(text)) {
            return null;
        }

        try {
            return getMapper().readValue(text, typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public <T> T from(ByteBuffer buf, @Nonnull TypeReference<T> typeReference) {
        if (buf == null) {
            return null;
        }

        try {
            if (!buf.hasArray()) {
                byte[] bytes = new byte[buf.remaining()];
                buf.get(bytes);
                return getMapper().readValue(bytes, typeReference);
            }

            final int offset = buf.arrayOffset();
            return getMapper().readValue(buf.array(), offset + buf.position(), buf.remaining(), typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public <T> T from(byte[] bytes, @Nonnull TypeReference<T> typeReference) {
        if (bytes == null) {
            return null;
        }

        try {
            return getMapper().readValue(bytes, typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public String pretty(Object object) {
        return toString(object, true);
    }

    public String pretty(Dumpable dumpable) {
        return pretty(Dumpable.toMap(dumpable, null));
    }

    public String toString(Object object) {
        return toString(object, false);
    }

    public byte[] toBytes(Object object) {
        return toBytes(object, false);
    }

    public ByteBuffer toByteBuffer(Object object) {
        return toByteBuffer(object, false);
    }

    public String toString(Object object, boolean pretty) {
        if (object == null) {
            return null;
        }

        try {
            if (pretty) {
                return getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
            }
            return getMapper().writeValueAsString(object);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public byte[] toBytes(Object object, boolean pretty) {
        if (object == null) {
            return null;
        }
        return toByteBuffer(object, pretty).array();
    }

    public ByteBuffer toByteBuffer(Object object, boolean pretty) {
        if (object == null) {
            return null;
        }

        try {
            var buf = new ByteArrayBuilder();

            if (pretty) {
                getMapper().writerWithDefaultPrettyPrinter().writeValue(buf, object);
            } else {
                getMapper().writeValue(buf, object);
            }

            return ByteBuffer.wrap(buf.getCurrentSegment(), 0, buf.getCurrentSegmentLength());
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }
}

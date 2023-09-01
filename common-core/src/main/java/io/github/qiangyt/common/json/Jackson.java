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
package io.github.qiangyt.common.json;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.qiangyt.common.bean.Dumpable;
import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.StringHelper;
import lombok.Getter;
import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;
import static java.util.Objects.requireNonNull;

@Getter
@ThreadSafe
public class Jackson {

    public static final Jackson DEFAULT = new Jackson(buildDefaultMapper());

    @Nonnull
    public final ObjectMapper mapper;

    public Jackson(@Nonnull ObjectMapper mapper) {
        this.mapper = requireNonNull(mapper);
    }

    @Nonnull
    public static ObjectMapper buildDefaultMapper() {
        var r = new ObjectMapper();
        initDefaultMapper(r);
        return r;
    }

    public static void initDefaultMapper(@Nonnull ObjectMapper mapper) {
        requireNonNull(mapper);

        var dateModule = new SimpleModule();
        dateModule.addSerializer(Date.class, new DateSerializer());
        dateModule.addDeserializer(Date.class, new DateDeserialize());
        mapper.registerModule(dateModule);
        mapper.registerModule(new JavaTimeModule());

        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    }

    @Nullable
    public <T> T from(@Nullable String text, @Nonnull Class<T> clazz) {
        requireNonNull(clazz);

        if (StringHelper.isBlank(text)) {
            return null;
        }

        try {
            return getMapper().readValue(text, clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable ByteBuffer buf, @Nonnull Class<T> clazz) {
        requireNonNull(clazz);

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

    @Nullable
    public <T> T from(@Nullable byte[] bytes, @Nonnull Class<T> clazz) {
        requireNonNull(clazz);

        if (bytes == null) {
            return null;
        }

        try {
            return getMapper().readValue(bytes, clazz);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable String text, @Nonnull TypeReference<T> typeReference) {
        requireNonNull(typeReference);

        if (StringHelper.isBlank(text)) {
            return null;
        }

        try {
            return getMapper().readValue(text, typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public <T> T from(@Nullable ByteBuffer buf, @Nonnull TypeReference<T> typeReference) {
        requireNonNull(typeReference);

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

    @Nullable
    public <T> T from(@Nullable byte[] bytes, @Nonnull TypeReference<T> typeReference) {
        requireNonNull(typeReference);

        if (bytes == null) {
            return null;
        }

        try {
            return getMapper().readValue(bytes, typeReference);
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public String pretty(@Nullable Object object) {
        return toString(object, true);
    }

    @Nullable
    public String pretty(@Nullable Dumpable dumpable) {
        return pretty(Dumpable.toMap(dumpable, null));
    }

    @Nullable
    public String toString(@Nullable Object object) {
        return toString(object, false);
    }

    @Nullable
    public byte[] toBytes(@Nullable Object object) {
        return toBytes(object, false);
    }

    @Nullable
    public ByteBuffer toByteBuffer(@Nullable Object object) {
        return toByteBuffer(object, false);
    }

    @Nullable
    public String toString(@Nullable Object object, boolean pretty) {
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

    @Nullable
    public byte[] toBytes(@Nullable Object object, boolean pretty) {
        if (object == null) {
            return null;
        }
        return toByteBuffer(object, pretty).array();
    }

    @Nullable
    public ByteBuffer toByteBuffer(@Nullable Object object, boolean pretty) {
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

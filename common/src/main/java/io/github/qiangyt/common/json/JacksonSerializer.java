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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import io.github.qiangyt.common.err.BadValueException;
import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public abstract class JacksonSerializer<T> extends JsonSerializer<T> {

    final boolean dump;

    protected JacksonSerializer(boolean dump) {
        this.dump = dump;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {

        if (value == null) {
            gen.writeNull();
        } else {
            try {
                if (isDump()) {
                    dump(value, gen);
                } else {
                    serialize(value, gen);
                }
            } catch (RuntimeException | IOException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BadValueException(ex);
            }
        }
    }

    @Nonnull
    protected void dump(@Nonnull T value, @Nonnull JsonGenerator gen) throws Exception {
        throw new BadValueException("serialization is NOT supported");
    }

    @Nonnull
    protected void serialize(@Nonnull T value, @Nonnull JsonGenerator gen) throws Exception {
        dump(value, gen);
    }

}

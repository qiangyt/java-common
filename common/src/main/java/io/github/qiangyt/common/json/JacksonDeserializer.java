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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.github.qiangyt.common.err.BadValueException;
import io.github.qiangyt.common.misc.EnvExpander;
import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public abstract class JacksonDeserializer<T> extends JsonDeserializer<T> {

    final boolean expandEnv;

    protected JacksonDeserializer(boolean expandEnv) {
        this.expandEnv = expandEnv;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
        String text = p.getValueAsString();
        if (text == null) {
            return null;
        }

        if (isExpandEnv()) {
            text = EnvExpander.tryExpands(text);
        }

        try {
            return deserialize(text);
        } catch (RuntimeException | IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadValueException(ex);
        }
    }

    @Nonnull
    protected T deserialize(@Nonnull String text) throws Exception {
        throw new BadValueException("deserialization is NOT supported");
    }

}

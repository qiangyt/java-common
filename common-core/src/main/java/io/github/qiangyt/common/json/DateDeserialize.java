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
import java.util.Date;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.github.qiangyt.common.err.BadValueException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public class DateDeserialize extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        String valueText = p.getValueAsString();

        try {
            return new Date(Long.valueOf(valueText));
        } catch (NumberFormatException ex) {
            throw new BadValueException(ex, "%s is NOT a long value", valueText);
        }
    }
}

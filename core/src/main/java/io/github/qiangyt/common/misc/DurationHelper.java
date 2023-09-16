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
package io.github.qiangyt.common.misc;

import java.time.Duration;

import java.time.Instant;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

public class DurationHelper {

    private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([smhdw])");

    public static Duration parse(String text) {
        if (StringHelper.isBlank(text)) {
            return null;
        }

        if (Character.isDigit(text.charAt(0)) == false) {
            return Duration.parse(text);
        }

        text = text.toLowerCase(Locale.ENGLISH);

        Matcher matcher = PERIOD_PATTERN.matcher(text);
        Instant i = Instant.EPOCH;
        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String typ = matcher.group(2);
            switch (typ) {
            case "s":
                i = i.plus(Duration.ofSeconds(num));
                break;
            case "m":
                i = i.plus(Duration.ofMinutes(num));
                break;
            case "h":
                i = i.plus(Duration.ofHours(num));
                break;
            case "d":
                i = i.plus(Duration.ofDays(num));
                break;
            case "w":
                i = i.plus(Period.ofWeeks(num));
                break;
            }
        }

        return Duration.ofMillis(i.toEpochMilli());
    }

}

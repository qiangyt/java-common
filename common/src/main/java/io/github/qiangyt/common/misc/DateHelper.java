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
package io.github.qiangyt.common.misc;

import java.time.format.DateTimeFormatter;

import jakarta.annotation.Nonnull;

public class DateHelper {

    @Nonnull
    public static final String DAY_PATTERN = "yyyy-MM-dd";

    @Nonnull
    public static final String TIME_PATTERN = "HH:mm:ss.SSS";

    @Nonnull
    public static final String RFC3339_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Nonnull
    public static final String DAYTIME_PATTERN = DAY_PATTERN + " " + TIME_PATTERN;

    @Nonnull
    @SuppressWarnings("null")
    public static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern(DAY_PATTERN);

    @Nonnull
    @SuppressWarnings("null")
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern(TIME_PATTERN);

    @Nonnull
    @SuppressWarnings("null")
    public static final DateTimeFormatter RFC3339 = DateTimeFormatter.ofPattern(RFC3339_PATTERN);

    @Nonnull
    @SuppressWarnings("null")
    public static final DateTimeFormatter DAYTIME = DateTimeFormatter.ofPattern(DAYTIME_PATTERN);
}

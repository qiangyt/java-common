/*
 * qiangyt-common 1.0.0 - Common library by Yiting Qiang
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package qiangyt.common.misc;

import java.time.format.DateTimeFormatter;

public class DateHelper {

    public static final String DAY_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss.SSS";
    public static final String RFC3339_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DAYTIME_PATTERN = DAY_PATTERN + " " + TIME_PATTERN;

    public static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern(DAY_PATTERN);
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern(TIME_PATTERN);
    public static final DateTimeFormatter RFC3339 = DateTimeFormatter.ofPattern(RFC3339_PATTERN);
    public static final DateTimeFormatter DAYTIME = DateTimeFormatter.ofPattern(DAYTIME_PATTERN);
}

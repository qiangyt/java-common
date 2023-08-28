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
package qiangyt.common.err;

import jakarta.annotation.Nonnull;

public class GeneralException extends RuntimeException {

    public GeneralException(@Nonnull String messageFormat, @Nonnull Object... messageArgs) {
        super(String.format(messageFormat, messageArgs));
    }

    public GeneralException(@Nonnull Throwable cause) {
        super(cause);
    }

    public GeneralException(@Nonnull Throwable cause, @Nonnull String messageFormat, @Nonnull Object... messageArgs) {
        super(String.format(messageFormat, messageArgs), cause);
    }

}

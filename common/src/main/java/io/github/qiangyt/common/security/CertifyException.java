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
package io.github.qiangyt.common.security;

import jakarta.annotation.Nonnull;

import io.github.qiangyt.common.err.BadStateException;

public class CertifyException extends BadStateException {

    private static final long serialVersionUID = 1L;

    public CertifyException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }

    public CertifyException(@Nonnull String message) {
        super(message);
    }

    public CertifyException(@Nonnull Throwable cause) {
        super(cause);
    }

}

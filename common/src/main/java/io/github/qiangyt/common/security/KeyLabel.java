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

/**
 * Enumeration of PEM labels.
 */
public enum KeyLabel {
    CERTIFICATE("CERTIFICATE"), CERTIFICATE_REQUEST("CERTIFICATE REQUEST"), PRIVATE_KEY("PRIVATE KEY"),
    PUBLIC_KEY("PUBLIC KEY");

    public final String label;
    public final String beginLine, endLine;

    KeyLabel(String label) {
        this.label = label;
        this.beginLine = String.format("-----BEGIN %s-----", label);
        this.endLine = String.format("-----END %s-----", label);
    }

    @Override
    public String toString() {
        return label;
    }
}

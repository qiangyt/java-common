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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.regex.Pattern;

import jakarta.annotation.Nullable;
import io.github.qiangyt.common.err.BadStateException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * 基本的编解码工具
 */
public class Codec {

    public static final Base64.Encoder KEY_ENCODER = Base64.getMimeEncoder(64,
            "\n".getBytes(StandardCharsets.US_ASCII));

    public static final Pattern BASE64URL_PATTERN = Pattern.compile("[0-9A-Za-z_-]*");

    public static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    public static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    @Nullable
    public static byte[] decodeHex(@Nullable String hex) {
        if (hex == null) {
            return null;
        }
        try {
            return Hex.decodeHex(hex);
        } catch (DecoderException e) {
            throw new BadStateException(e);
        }
    }

    @Nullable
    public static String encodeHex(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return Hex.encodeHexString(bytes);
    }

    /**
     * 字节数组编码成Base64（可以用于URL）
     */
    public static String bytesToBase64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }

    /**
     * Base64转字节数组
     */
    public static byte[] base64ToBytes(String base64ed) {
        if (base64ed == null) {
            return null;
        }
        return BASE64_URL_DECODER.decode(base64ed);
    }

    /**
     * Validates that the given {@link String} is a valid base64url encoded value.
     *
     * @param base64
     *            {@link String} to validate
     *
     * @return {@code true}: String contains a valid base64url encoded value. {@code false} if the {@link String} was
     *         {@code null} or contained illegal characters.
     *
     * @since 2.6
     */
    public static boolean isValidBase64Url(@Nullable String base64) {
        return base64 != null && BASE64URL_PATTERN.matcher(base64).matches();
    }

    /**
     * long转换成字节数组
     */
    static void longTobytes(long value, byte[] bytes, int offset) {
        for (int i = 7; i > -1; i--) {
            bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
        }
    }

    /**
     * 字节数组转换成long
     */
    static long bytesTolong(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 7; i > -1; i--) {
            value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
        }
        return value;
    }

    /**
     * Writes an encoded key or certificate to a file in PEM format.
     *
     * @param encoded
     *            Encoded data to write
     * @param label
     *            {@link KeyLabel} to be used
     * @param out
     *            {@link Writer} to write to. It will not be closed after use!
     */
    public static void bytesToPem(byte[] encoded, KeyLabel label, Writer out) {
        try {
            if (label != null) {
                out.append(label.beginLine).append("\n");
            }
            out.append(new String(KEY_ENCODER.encode(encoded), StandardCharsets.US_ASCII));
            if (label != null) {
                out.append(label.endLine).append("\n");
            }
        } catch (IOException e) {
            throw new BadStateException(e);
        }
    }

    public static String bytesToPem(byte[] encoded, KeyLabel label) {
        var w = new StringWriter(encoded.length * 2);
        bytesToPem(encoded, label, w);
        return w.toString();
    }

}

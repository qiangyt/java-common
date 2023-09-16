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

import java.util.UUID;
import static java.util.Objects.requireNonNull;
import javax.annotation.Nonnull;

/**
 * UUID工具
 */
public class UuidHelper {

    /**
     * 生成短UUID字符串（22位）
     *
     * @return
     */
    @Nonnull
    @SuppressWarnings("null")
    public static String shortUuid() {
        var uuid = UUID.randomUUID();
        return compress(uuid);
    }

    /**
     * 把UUID对象压缩成短UUID字符串
     */
    public static String compress(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        byte[] byUuid = new byte[16];
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();
        Codec.longTobytes(most, byUuid, 0);
        Codec.longTobytes(least, byUuid, 8);
        return Codec.bytesToBase64(byUuid);
    }

    /**
     * 把短UUID字符串解压缩UUID对象
     */
    public static UUID uncompress(String compressedUuid) {
        if (compressedUuid == null) {
            return null;
        }

        if (compressedUuid.length() != 22) {
            throw new IllegalArgumentException("Invalid uuid!");
        }

        byte[] byUuid = Codec.base64ToBytes(compressedUuid);
        requireNonNull(byUuid);

        long most = Codec.bytesTolong(byUuid, 0);
        long least = Codec.bytesTolong(byUuid, 8);
        return new UUID(most, least);
    }

}

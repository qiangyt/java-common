/*
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.qiangyt.common.misc;

import java.util.UUID;

/**
 * UUID工具
 */
public class UuidHelper {

    /**
     * 生成短UUID字符串（22位）
     *
     * @return
     */
    public static String shortUuid() {
        var uuid = UUID.randomUUID();
        return compress(uuid);
    }

    /**
     * 把UUID对象压缩成短UUID字符串
     */
    public static String compress(UUID uuid) {
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
        if (compressedUuid.length() != 22) {
            throw new IllegalArgumentException("Invalid uuid!");
        }
        byte[] byUuid = Codec.base64ToBytes(compressedUuid);
        long most = Codec.bytesTolong(byUuid, 0);
        long least = Codec.bytesTolong(byUuid, 8);
        return new UUID(most, least);
    }

}

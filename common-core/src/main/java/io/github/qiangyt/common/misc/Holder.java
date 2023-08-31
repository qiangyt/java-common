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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class Holder<T> {

    private volatile T value;

    public Holder() {
        this(null);
    }

    public Holder(@Nullable T value) {
        this.value = value;
    }

    public static @Nonnull <T> Holder<T> of(@Nullable T value) {
        return new Holder<>(value);
    }

    public void set(@Nullable T value) {
        this.value = value;
    }

    public @Nullable T get() {
        return this.value;
    }

}

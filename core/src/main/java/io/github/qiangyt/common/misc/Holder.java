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

import javax.annotation.Nonnull;

public class Holder<T> {

    private volatile T value;

    public Holder() {
        this(null);
    }

    public Holder(T value) {
        this.value = value;
    }

    public static @Nonnull <T> Holder<T> of(T value) {
        return new Holder<>(value);
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

}

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

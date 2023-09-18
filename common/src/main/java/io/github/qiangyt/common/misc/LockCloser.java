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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import jakarta.annotation.Nonnull;

public class LockCloser implements AutoCloseable {

    @Nonnull
    final Lock lock;

    public LockCloser(@Nonnull Lock lock) {
        this.lock = lock;
    }

    @Nonnull
    public static LockCloser read(@Nonnull ReadWriteLock rwlock) {
        var lock = rwlock.readLock();
        return new LockCloser(lock);
    }

    @Nonnull
    public static LockCloser write(@Nonnull ReadWriteLock rwlock) {
        var lock = rwlock.writeLock();
        return new LockCloser(lock);
    }

    @Override
    public void close() {
        this.lock.unlock();
    }

}

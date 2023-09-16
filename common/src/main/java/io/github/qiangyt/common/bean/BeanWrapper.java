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
package io.github.qiangyt.common.bean;

import io.github.qiangyt.common.misc.ClassHelper;
import jakarta.annotation.Nonnull;
import lombok.Getter;

public class BeanWrapper<T> implements Bean {

    @Getter
    @Nonnull
    final BeanInfo<BeanWrapper<T>> beanInfo;

    public BeanWrapper(@Nonnull T instance, @Nonnull String name) {
        if (name == null) {
            name = ClassHelper.parseTitle(getClass());
        }

        this.beanInfo = (BeanInfo<BeanWrapper<T>>) Container.loadCurrent().tryToRegisterBean(this, name);
    }

}
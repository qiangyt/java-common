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

import jakarta.annotation.Nonnull;
import lombok.Getter;

public abstract class AbstractBean<T extends Bean> implements Bean {

    @Getter
    @Nonnull
    final BeanInfo<T> beanInfo;

    protected AbstractBean(@Nonnull Object... dependsOn) {
        this(null, dependsOn);
    }

    @SuppressWarnings("unchecked")
    protected AbstractBean(String name, @Nonnull Object... dependsOn) {
        if (name == null) {
            name = Bean.parseBeanName(getClass());
        }
        this.beanInfo = (BeanInfo<T>) Container.loadCurrent().registerBean(this, name, dependsOn);
    }

}

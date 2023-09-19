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

import java.util.Arrays;
import java.util.Collection;

import jakarta.annotation.Nonnull;
import lombok.Getter;

@Getter
public abstract class WrapperBean<T> implements Bean {

    @Nonnull
    final BeanMetadata<T> beanMetadata;

    @Nonnull
    final T instance;

    public WrapperBean(String name, @Nonnull BeanContainer container, @Nonnull T instance,
            @Nonnull Object... dependsOn) {
        this(name, container, instance, Arrays.asList(dependsOn));
    }

    @SuppressWarnings("unchecked")
    public WrapperBean(String name, @Nonnull BeanContainer container, @Nonnull T instance,
            @Nonnull Collection<?> dependsOn) {
        if (name == null) {
            name = Bean.parseBeanName(instance.getClass());
        }

        this.instance = instance;
        this.beanMetadata = (BeanMetadata<T>) container.registerBean(this, name);

        var dependsOnMetadatas = container.normalizeMetadatas(dependsOn);
        this.beanMetadata.dependsOn(dependsOnMetadatas);
    }

    @SuppressWarnings("unchecked")
    public WrapperBean(String name, @Nonnull BeanContainer container, @Nonnull T instance,
            Class<?> interfaceDependsOn) {
        if (name == null) {
            name = Bean.parseBeanName(instance.getClass());
        }

        this.instance = instance;
        this.beanMetadata = (BeanMetadata<T>) container.registerBean(this, name);

        this.beanMetadata.dependsOn(interfaceDependsOn);
    }

}

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
package io.github.qiangyt.common.bean;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import io.github.qiangyt.common.misc.ClassHelper;

public abstract class AbstractBean implements SimpleBean {

    @Nonnull
    final BeanInfo<AbstractBean> beanInfo;

    protected AbstractBean(@Nonnull SimpleBean... dependentBeans) {
        this(null, dependentBeans);
    }

    protected AbstractBean(String name, @Nonnull SimpleBean... dependentBeans) {
        if (name == null) {
            name = ClassHelper.parseTitle(getClass());
        }

        this.beanInfo = new BeanInfo<>(this, name, dependentBeans);
    }

    public void addDependencies(@Nonnull SimpleBean... dependentBeans) {
        getBeanInfo().addDependencies(dependentBeans);
    }

    @Override
    @Nonnull
    public BeanInfo<AbstractBean> getBeanInfo() {
        return this.beanInfo;
    }

    @Nonnull
    public Logger log() {
        return getBeanInfo().getLog();
    }

}

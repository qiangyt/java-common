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
package io.github.qiangyt.common.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import io.github.qiangyt.common.bean.WrapperBean;
import io.github.qiangyt.common.bean.BeanContainer;
import io.github.qiangyt.common.err.BadStateException;
import jakarta.annotation.Nonnull;

public class SchedulerBean extends WrapperBean<Scheduler> {

    public SchedulerBean(@Nonnull BeanContainer container, @Nonnull Scheduler instance) {
        super("scheduler", container, instance);
    }

    @Override
    public void doInit() throws Exception {
        getInstance().start();
    }

    @Override
    public void doDestroy() throws Exception {
        getInstance().shutdown(/* true *//* TODO */);
    }

    public static Scheduler newInstance() {
        try {
            return StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new BadStateException(e);
        }
    }

    public static SchedulerBean newBean(@Nonnull BeanContainer container) {
        var inst = newInstance();
        return new SchedulerBean(container, inst);
    }

}

/*
 * io.github.qiangyt:qiangyt-common-spring - Common library by Yiting Qiang
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
package io.github.qiangyt.common.spring;

import jakarta.annotation.Nonnull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import static java.util.Objects.requireNonNull;
import io.github.qiangyt.common.err.BadStateException;

/**
 * 用于某些特殊场景下获取Spring Bean实例的工具。大部分场景下使用@Autowired等注解，应该避免使用这个工具。
 */
@Component
public class ContextHelper implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(@Nonnull Class<T> clazz) {
        return getContext().getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> T loadBean(@Nonnull Class<?> clazz) {
        T r = (T) getBean(clazz);
        if (r == null) {
            throw new BadStateException("bean not found: name=%s", clazz.getName());
        }
        return r;
    }

    public static <T> T getBean(@Nonnull String name, @Nonnull Class<T> clazz) {
        return getContext().getBean(name, clazz);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> T loadBean(@Nonnull String name, @Nonnull Class<?> clazz) {
        T r = (T) getBean(name, clazz);
        if (r == null) {
            throw new BadStateException("bean not found: name=%s, type=%s", name, clazz);
        }
        return r;
    }

    public static boolean containsBean(@Nonnull String name) {
        return getContext().containsBean(name);
    }

    @Nonnull
    public static ApplicationContext getContext() {
        return requireNonNull(context);
    }

    @Override
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        if (context != null) {
            throw new BadStateException("context already initialized");
        }

        context = requireNonNull(appContext);
    }

    public static void autowireBean(Object bean) {
        requireNonNull(context).getAutowireCapableBeanFactory().autowireBean(bean);
    }

}

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
package io.github.qiangyt.common.spring;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

    @Nullable
    private static ApplicationContext context;

    public static <T> T getBean(@Nonnull Class<T> clazz) {
        return getContext().getBean(clazz);
    }

    public static <T> T getBean(@Nonnull String name, @Nonnull Class<T> clazz) {
        return getContext().getBean(name, clazz);
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

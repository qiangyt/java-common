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
package com.github.qiangyt.common.bean;

import static java.util.Objects.requireNonNull;
import java.util.Date;

/**
 * 具有唯一性id标示的基类，主要用于实体类的VO。 所有property必须和@see qiangyt.common.entity.GenericEO一一对应，因为 EO和VO间的property copy机制依赖于这个规定。
 *
 * 这里限制了id必须是String，所以需要非String的id时需要另外实现。
 *
 */
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
// @lombok.experimental.SuperBuilder
public class GenericBean implements Dumpable {

    /**
     * 唯一性标示
     */
    String id;

    /**
     * 创建时间
     */
    Date createdAt;

    @Override
    public String toString() {
        return requireNonNull(dumpAsYaml(null));
    }
}

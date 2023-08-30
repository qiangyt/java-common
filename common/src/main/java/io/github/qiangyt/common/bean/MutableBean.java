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
package io.github.qiangyt.common.bean;

import java.util.Date;

/**
 * 可修改的实体类的VO。
 *
 * 所有property必须和@see qiangyt.common.entity.MutableEO一一对应，因为 EO和VO间的property copy机制依赖于这个规定。
 *
 */
@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
// @lombok.experimental.SuperBuilder
public class MutableBean extends GenericBean {

    /**
     * 版本号。用于乐观锁(optimistic locking)
     */
    int version;

    /**
     * 修改时间
     */
    Date updatedAt;

}

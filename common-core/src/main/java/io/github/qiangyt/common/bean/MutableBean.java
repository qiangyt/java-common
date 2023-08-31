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

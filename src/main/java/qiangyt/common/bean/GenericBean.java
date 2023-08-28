/*
 * qiangyt-common 1.0.0 - Common library by Yiting Qiang
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package qiangyt.common.bean;

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

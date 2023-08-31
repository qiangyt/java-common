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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;

import io.github.qiangyt.common.json.Jackson;
import io.github.qiangyt.common.json.JacksonHelper;
import io.github.qiangyt.common.yaml.SnakeYaml;

import jakarta.annotation.Nonnull;

/**
 * 方便调试输出（JSON、YAML格式）
 */
public interface Dumpable {

    @Nonnull
    @SuppressWarnings({ "unchecked", "null" })
    default Map<String, Object> toMap(Map<Object, Object> visited) {
        if (visited == null) {
            visited = new HashMap<>();
        }

        return (Map<String, Object>) visited.computeIfAbsent(this, k -> {
            var jackson = Jackson.DEFAULT;
            var json = jackson.toString(this, false);
            return jackson.from(json, Map.class);
        });
    }

    @Nullable
    static Map<String, Object> toMap(@Nullable Dumpable dumpable, Map<Object, Object> visited) {
        if (dumpable == null) {
            return null;
        }

        return dumpable.toMap(visited);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    static List<Map<String, Object>> toMap(@Nullable List<? extends Dumpable> list, Map<Object, Object> visited) {
        if (list == null) {
            return null;
        }

        if (visited == null) {
            visited = new HashMap<>();
        }
        if (visited.containsKey(list)) {
            return (List<Map<String, Object>>) visited.get(list);
        }

        var r = new ArrayList<Map<String, Object>>(list.size());
        for (var element : list) {
            if (element == null) {
                r.add(null);
            } else {
                r.add(element.toMap(visited));
            }
        }
        visited.put(list, r);
        return r;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    static Map<String, Map<String, Object>> toMap(@Nullable Map<String, ? extends Dumpable> map,
            Map<Object, Object> visited) {
        if (map == null) {
            return null;
        }

        if (visited == null) {
            visited = new HashMap<>();
        }
        if (visited.containsKey(map)) {
            return (Map<String, Map<String, Object>>) visited.get(map);
        }

        var r = new HashMap<String, Map<String, Object>>(map.size());
        for (var entry : map.entrySet()) {
            var value = entry.getValue();
            r.put(entry.getKey(), (value == null) ? null : value.toMap(visited));
        }
        visited.put(map, r);
        return r;
    }

    @Nonnull
    @SuppressWarnings("null")
    default String dumpAsJson(Map<Object, Object> visited) {
        return JacksonHelper.pretty(toMap(visited));
    }

    @Nonnull
    default String dumpAsJson() {
        return dumpAsJson(null);
    }

    @Nonnull
    @SuppressWarnings("null")
    default String dumpAsYaml(Map<Object, Object> visited) {
        return SnakeYaml.build().dump(toMap(visited));
    }

    @Nonnull
    default String dumpAsYaml() {
        return dumpAsYaml(null);
    }

}

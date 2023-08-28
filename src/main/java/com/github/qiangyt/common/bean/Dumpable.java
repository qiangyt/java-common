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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;

import com.github.qiangyt.common.json.Jackson;
import com.github.qiangyt.common.json.JacksonHelper;
import com.github.qiangyt.common.yaml.SnakeYaml;

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

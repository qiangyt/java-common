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
package com.github.qiangyt.common.json;

import jakarta.annotation.Nonnull;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Nullable;

import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import com.github.qiangyt.common.bean.Dumpable;
import com.github.qiangyt.common.err.BadStateException;

@Getter
@Setter
@Validated
public class JsonAPIConfig implements Dumpable {

    public static final int DEFAULT_CONNECT_TIMEOUT = 10;
    public static final int DEFAULT_READ_TIMEOUT = 10;
    // TODO: proxy

    @Nonnull
    public static final HttpClient.Redirect DEFAULT_REDIRECT = HttpClient.Redirect.ALWAYS;

    @Nonnull
    private URI endpoint;

    private int connectTimeoutSeconds = DEFAULT_CONNECT_TIMEOUT;

    private int readTimeoutSeconds = DEFAULT_READ_TIMEOUT;

    @Nonnull
    private HttpClient.Redirect redirect = DEFAULT_REDIRECT;

    @Nullable
    private InetSocketAddress proxy;

    public JsonAPIConfig(@Nonnull String endpointString) {
        this(parseEndpoint(endpointString));
    }

    public JsonAPIConfig(@Nonnull URI endpoint) {
        this.endpoint = requireNonNull(endpoint);
    }

    @Nonnull
    public static URI parseEndpoint(String endpointString) {
        try {
            return new URI(endpointString);
        } catch (URISyntaxException e) {
            throw new BadStateException(e);
        }
    }

    @SuppressWarnings({ "null", "unchecked" })
    @Override
    public @Nonnull Map<String, Object> toMap(Map<Object, Object> visited) {
        if (visited == null) {
            visited = new HashMap<>();
        }
        if (visited.containsKey(this)) {
            return (Map<String, Object>) visited.get(this);
        }

        var r = new HashMap<String, Object>();
        r.put("endpoint", getEndpoint());
        r.put("connectTimeoutSeconds", getConnectTimeoutSeconds());
        r.put("readTimeoutSeconds", getReadTimeoutSeconds());
        r.put("redirect", getRedirect());
        r.put("proxy", getProxy());

        visited.put(this, r);
        return r;
    }

    @Override
    public String toString() {
        return requireNonNull(dumpAsJson(null));
    }

}

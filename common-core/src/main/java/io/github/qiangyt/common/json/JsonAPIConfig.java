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
package io.github.qiangyt.common.json;

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
import io.github.qiangyt.common.bean.Dumpable;
import io.github.qiangyt.common.err.BadStateException;

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

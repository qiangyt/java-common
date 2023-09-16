/*
 * io.github.qiangyt:qiangyt-common - Common library by Yiting Qiang
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

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.type.TypeReference;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import io.github.qiangyt.common.bean.Dumpable;
import io.github.qiangyt.common.err.BadStateException;
import io.github.qiangyt.common.misc.StringHelper;

@Getter
public class JsonAPI implements Dumpable {

    @Nonnull
    final JsonAPIConfig config;

    final String path;

    @Nonnull
    final HttpRequest.Builder request;

    @Nonnull
    final HttpClient.Builder client;

    HttpResponse<String> response;

    @Nonnull
    JsonAPIErrorHandler errorHandler;

    public JsonAPI(@Nonnull JsonAPIConfig config) {
        this(config, "");
    }

    public JsonAPI(@Nonnull JsonAPIConfig config, @Nonnull JsonAPIErrorHandler errorHandler) {
        this(config, "", errorHandler);
    }

    public JsonAPI(@Nonnull JsonAPIConfig config, String path) {
        this(config, path, JsonAPIErrorHandler.DEFAULT);
    }

    public JsonAPI(@Nonnull JsonAPIConfig config, String path, @Nonnull JsonAPIErrorHandler errorHandler) {
        this.config = requireNonNull(config);
        this.path = requireNonNull(path);
        this.errorHandler = errorHandler;
        this.request = initRequest();
        this.client = initClient();
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
        r.put("config", getConfig().toMap(visited));
        r.put("path", getPath());
        r.put("request", getRequest());
        r.put("client", getClient());

        if (getResponse() != null) {
            r.put("response", getResponse());
        }

        visited.put(this, r);
        return r;
    }

    @Override
    public String toString() {
        return requireNonNull(dumpAsJson(null));
    }

    protected @Nonnull HttpRequest.Builder initRequest() {
        var cfg = getConfig();

        var paz = getPath();
        if (paz.startsWith("/")) {
            paz = paz.substring(1);
        }

        var url = cfg.getEndpoint().toString();
        if (StringHelper.notBlank(paz)) {
            if (url.endsWith("/")) {
                url += paz;
            } else {
                url += "/" + paz;
            }
        }

        var r = HttpRequest.newBuilder();
        try {
            r.uri(new URI(url));
        } catch (URISyntaxException e) {
            throw new BadStateException(e);
        }

        r.timeout(Duration.ofSeconds(config.getReadTimeoutSeconds()));
        r.header("Content-Type", "application/json");
        r.header("Accepted", "application/json");

        return r;
    }

    public <T> T GET(Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("GET", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T GET(Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("GET", requestBody, responseTypeReference);
    }

    public <T> T GET(@Nonnull Class<T> responseBodyClass) {
        return GET(null, responseBodyClass);
    }

    @Nonnull
    public <T> T GET(@Nonnull TypeReference<T> responseTypeReference) {
        return GET(null, responseTypeReference);
    }

    public <T> T PATCH(Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("PATCH", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T PATCH(Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("PATCH", requestBody, responseTypeReference);
    }

    @Nonnull
    public <T> T PATCH(@Nonnull TypeReference<T> responseTypeReference) {
        return PATCH(null, responseTypeReference);
    }

    public <T> T PATCH(@Nonnull Class<T> responseBodyClass) {
        return PATCH(null, responseBodyClass);
    }

    public <T> T PUT(Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("PUT", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T PUT(Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("PUT", requestBody, responseTypeReference);
    }

    public <T> T PUT(@Nonnull Class<T> responseBodyClass) {
        return PUT(null, responseBodyClass);
    }

    @Nonnull
    public <T> T PUT(@Nonnull TypeReference<T> responseTypeReference) {
        return PUT(null, responseTypeReference);
    }

    public <T> T POST(Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("POST", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T POST(Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("POST", requestBody, responseTypeReference);
    }

    public <T> T POST(@Nonnull Class<T> responseBodyClass) {
        return POST(null, responseBodyClass);
    }

    @Nonnull
    public <T> T POST(@Nonnull TypeReference<T> responseTypeReference) {
        return POST(null, responseTypeReference);
    }

    public <T> T DELETE(Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("DELETE", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T DELETE(Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("DELETE", requestBody, responseTypeReference);
    }

    public <T> T DELETE(@Nonnull Class<T> responseBodyClass) {
        return DELETE(null, responseBodyClass);
    }

    @Nonnull
    public <T> T DELETE(@Nonnull TypeReference<T> responseTypeReference) {
        return DELETE(null, responseTypeReference);
    }

    protected <T> T execute(@Nonnull String method, Object requestBody, @Nonnull Class<T> responseBodyClass) {
        var resp = doExecute(method, requestBody);

        var respBodyJson = resp.body();
        return JacksonHelper.from(respBodyJson, responseBodyClass);
    }

    @Nonnull
    protected <T> T execute(@Nonnull String method, Object requestBody,
            @Nonnull TypeReference<T> responseTypeReference) {
        var resp = doExecute(method, requestBody);

        var respBodyJson = resp.body();
        var r = JacksonHelper.from(respBodyJson, responseTypeReference);
        return requireNonNull(r);
    }

    @Nonnull
    protected HttpResponse<String> doExecute(@Nonnull String method, Object requestBody) {
        requireNonNull(method);

        String reqBodyJson;
        if (requestBody != null) {
            reqBodyJson = JacksonHelper.to(requestBody);
        } else {
            reqBodyJson = "";
        }
        getRequest().method(method, BodyPublishers.ofString(reqBodyJson));

        HttpResponse<String> resp = null;
        try {
            resp = client.build().send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw getErrorHandler().onIOError(e, this);
        } catch (InterruptedException e) {
            throw new BadStateException(e);
        }

        this.response = resp;
        int sc = resp.statusCode();
        if (sc / 100 != 2) {
            getErrorHandler().onErrorResponse(sc, this);
        }

        return resp;
    }

    @Nonnull
    protected HttpClient.Builder initClient() {
        var cfg = getConfig();

        var r = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(cfg.getConnectTimeoutSeconds()))
                .followRedirects(cfg.getRedirect());

        if (cfg.getProxy() != null) {
            r.proxy(ProxySelector.of(cfg.getProxy()));
        }
        return requireNonNull(r);
    }

}

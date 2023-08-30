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
package io.github.qiangyt.common.json;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

@Getter
public class JsonAPI implements Dumpable {

    @Nonnull
    final JsonAPIConfig config;

    @Nonnull
    final String path;

    @Nonnull
    final HttpRequest.Builder request;

    @Nonnull
    final HttpClient.Builder client;

    @Nullable
    HttpResponse<String> response;

    @Nonnull
    JsonAPIErrorHandler errorHandler;

    public JsonAPI(@Nonnull JsonAPIConfig config) {
        this(config, "");
    }

    public JsonAPI(@Nonnull JsonAPIConfig config, @Nonnull JsonAPIErrorHandler errorHandler) {
        this(config, "", errorHandler);
    }

    public JsonAPI(@Nonnull JsonAPIConfig config, @Nonnull String path) {
        this(config, path, JsonAPIErrorHandler.DEFAULT);
    }

    public JsonAPI(@Nonnull JsonAPIConfig config, @Nonnull String path, @Nonnull JsonAPIErrorHandler errorHandler) {
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

        var url = cfg.getEndpoint().toString() + getPath();

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

    @Nullable
    public <T> T GET(@Nullable Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("GET", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T GET(@Nullable Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("GET", requestBody, responseTypeReference);
    }

    @Nullable
    public <T> T GET(@Nonnull Class<T> responseBodyClass) {
        return GET(null, responseBodyClass);
    }

    @Nonnull
    public <T> T GET(@Nonnull TypeReference<T> responseTypeReference) {
        return GET(null, responseTypeReference);
    }

    @Nullable
    public <T> T PATCH(@Nullable Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("PATCH", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T PATCH(@Nullable Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("PATCH", requestBody, responseTypeReference);
    }

    @Nonnull
    public <T> T PATCH(@Nonnull TypeReference<T> responseTypeReference) {
        return PATCH(null, responseTypeReference);
    }

    @Nullable
    public <T> T PATCH(@Nonnull Class<T> responseBodyClass) {
        return PATCH(null, responseBodyClass);
    }

    @Nullable
    public <T> T PUT(@Nullable Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("PUT", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T PUT(@Nullable Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("PUT", requestBody, responseTypeReference);
    }

    @Nullable
    public <T> T PUT(@Nonnull Class<T> responseBodyClass) {
        return PUT(null, responseBodyClass);
    }

    @Nonnull
    public <T> T PUT(@Nonnull TypeReference<T> responseTypeReference) {
        return PUT(null, responseTypeReference);
    }

    @Nullable
    public <T> T POST(@Nullable Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("POST", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T POST(@Nullable Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("POST", requestBody, responseTypeReference);
    }

    @Nullable
    public <T> T POST(@Nonnull Class<T> responseBodyClass) {
        return POST(null, responseBodyClass);
    }

    @Nonnull
    public <T> T POST(@Nonnull TypeReference<T> responseTypeReference) {
        return POST(null, responseTypeReference);
    }

    @Nullable
    public <T> T DELETE(@Nullable Object requestBody, @Nonnull Class<T> responseBodyClass) {
        return execute("DELETE", requestBody, responseBodyClass);
    }

    @Nonnull
    public <T> T DELETE(@Nullable Object requestBody, @Nonnull TypeReference<T> responseTypeReference) {
        return execute("DELETE", requestBody, responseTypeReference);
    }

    @Nullable
    public <T> T DELETE(@Nonnull Class<T> responseBodyClass) {
        return DELETE(null, responseBodyClass);
    }

    @Nonnull
    public <T> T DELETE(@Nonnull TypeReference<T> responseTypeReference) {
        return DELETE(null, responseTypeReference);
    }

    @Nullable
    protected <T> T execute(@Nonnull String method, @Nullable Object requestBody, @Nonnull Class<T> responseBodyClass) {
        var resp = doExecute(method, requestBody);

        var respBodyJson = resp.body();
        return JacksonHelper.from(respBodyJson, responseBodyClass);
    }

    @Nonnull
    protected <T> T execute(@Nonnull String method, @Nullable Object requestBody,
            @Nonnull TypeReference<T> responseTypeReference) {
        var resp = doExecute(method, requestBody);

        var respBodyJson = resp.body();
        var r = JacksonHelper.from(respBodyJson, responseTypeReference);
        return requireNonNull(r);
    }

    @Nonnull
    protected HttpResponse<String> doExecute(@Nonnull String method, @Nullable Object requestBody) {
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

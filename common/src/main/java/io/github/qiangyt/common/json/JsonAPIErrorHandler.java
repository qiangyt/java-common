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

import java.io.IOException;

import jakarta.annotation.Nonnull;

import io.github.qiangyt.common.err.BadStateException;

public interface JsonAPIErrorHandler {

    @Nonnull
    public static final JsonAPIErrorHandler DEFAULT = new JsonAPIErrorHandler() {
    };

    @Nonnull
    default RuntimeException onIOError(@Nonnull IOException exception, @Nonnull JsonAPI api) {
        return new BadStateException("Unexpected io error", exception);
    }

    default void onErrorResponse(int statusCode, @Nonnull JsonAPI api) {
        switch (statusCode / 100) {
        case 4:
            on4xxResponse(statusCode, api);
            return;
        case 5:
            on5xxResponse(statusCode, api);
            return;
        default:
            onUnexpectedResponse(statusCode, api);
            return;
        }
    }

    default void on4xxResponse(int statusCode, @Nonnull JsonAPI api) {
        onUnexpectedResponse(statusCode, api);
    }

    default void on5xxResponse(int statusCode, @Nonnull JsonAPI api) {
        onUnexpectedResponse(statusCode, api);
    }

    default void onUnexpectedResponse(int statusCode, @Nonnull JsonAPI api) {
        var resp = api.getResponse();
        var bodyText = (resp == null) ? "<unknown>" : resp.body();
        throw new BadStateException("Unexpected status %d: %s", statusCode, bodyText);
    }

}

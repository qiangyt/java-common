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

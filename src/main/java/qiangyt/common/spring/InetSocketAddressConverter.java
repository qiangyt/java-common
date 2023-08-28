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
package qiangyt.common.spring;

import java.net.InetSocketAddress;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import qiangyt.common.misc.StringHelper;

@Component
@ConfigurationPropertiesBinding
public class InetSocketAddressConverter implements Converter<String, InetSocketAddress> {

    @Override
    public InetSocketAddress convert(String source) {
        if (StringHelper.isBlank(source)) {
            return null;
        }

        String[] parts = source.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid InetSocketAddress: " + source);
        }

        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        return new InetSocketAddress(host, port);
    }

}

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
package io.github.qiangyt.common.security.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nonnull;

public class SecurityModules {

    public static void register(@Nonnull ObjectMapper mapper, boolean expandEnv, boolean dump) {
        mapper.registerModule(KeyModule.build(expandEnv, dump));
        mapper.registerModule(KeyPairModule.build(expandEnv, dump));
        mapper.registerModule(KeyPairSourceModule.build(expandEnv, dump));
        mapper.registerModule(PrincipalModule.build(expandEnv, dump));
        mapper.registerModule(X509CertificateModule.build(expandEnv, dump));
        mapper.registerModule(IdentifierModule.build(expandEnv, dump));
        mapper.registerModule(StatusModule.build(expandEnv, dump));
        mapper.registerModule(AccountModule.build(expandEnv, dump));
        mapper.registerModule(ProblemModule.build(expandEnv, dump));
        mapper.registerModule(Dns01ChallengeModule.build(expandEnv, dump));
        mapper.registerModule(CertificateModule.build(expandEnv, dump));
        mapper.registerModule(OrderModule.build(expandEnv, dump));
    }

}

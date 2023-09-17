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
package io.github.qiangyt.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.qiangyt.common.security.jackson.AccountModule;
import io.github.qiangyt.common.security.jackson.CertificateModule;
import io.github.qiangyt.common.security.jackson.Dns01ChallengeModule;
import io.github.qiangyt.common.security.jackson.IdentifierModule;
import io.github.qiangyt.common.security.jackson.KeyModule;
import io.github.qiangyt.common.security.jackson.X509CertificateFileModule;
import io.github.qiangyt.common.security.jackson.KeyPairModule;
import io.github.qiangyt.common.security.jackson.OrderModule;
import io.github.qiangyt.common.security.jackson.PrincipalModule;
import io.github.qiangyt.common.security.jackson.ProblemModule;
import io.github.qiangyt.common.security.jackson.StatusModule;
import io.github.qiangyt.common.security.jackson.X509CertificateModule;
import jakarta.annotation.Nonnull;

public class JacksonModules {

    public static void register(@Nonnull ObjectMapper mapper, boolean expandEnv, boolean dump) {
        mapper.registerModule(KeyModule.build(expandEnv, dump));
        mapper.registerModule(KeyPairModule.build(expandEnv, dump));
        mapper.registerModule(X509CertificateFileModule.build(expandEnv, dump));
        mapper.registerModule(PrincipalModule.build(expandEnv, dump));
        mapper.registerModule(X509CertificateModule.build(expandEnv, dump));
        mapper.registerModule(IdentifierModule.build(expandEnv, dump));
        mapper.registerModule(StatusModule.build(expandEnv, dump));
        mapper.registerModule(AccountModule.build(expandEnv, dump));
        mapper.registerModule(ProblemModule.build(expandEnv, dump));
        mapper.registerModule(Dns01ChallengeModule.build(expandEnv, dump));
        mapper.registerModule(CertificateModule.build(expandEnv, dump));
        mapper.registerModule(OrderModule.build(expandEnv, dump));
        mapper.registerModule(X509CertificateFileModule.build(expandEnv, dump));
    }

}

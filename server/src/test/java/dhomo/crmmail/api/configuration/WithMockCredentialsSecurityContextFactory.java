/*
 * WithMockCredentialsSecurityContextFactory.java
 *
 * Created on 2019-02-25, 7:11
 *
 * Copyright 2018 Marc Nuri
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
 *
 */
package dhomo.crmmail.api.configuration;

import dhomo.crmmail.api.authentication.Credentials;
import dhomo.crmmail.api.credentials.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2019-02-25.
 */
public class WithMockCredentialsSecurityContextFactory implements WithSecurityContextFactory<WithMockCredentials> {
    @Override
    public SecurityContext createSecurityContext(WithMockCredentials mockCredentials) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        final Credentials credentials = Credentials.authenticated(new User(),
                mockCredentials.password(), Instant.now().plus(Duration.ofMinutes(15L)));
        securityContext.setAuthentication(credentials);
        credentials.getPrincipal().setServerHost(mockCredentials.serverHost());
        credentials.getPrincipal().setServerPort(mockCredentials.serverPort());
        credentials.getPrincipal().setUserName(mockCredentials.user());
        credentials.getPrincipal().setImapSsl(mockCredentials.imapSsl());
        credentials.getPrincipal().setSmtpHost(mockCredentials.smtpHost());
        credentials.getPrincipal().setSmtpPort(mockCredentials.smtpPort());
        credentials.getPrincipal().setSmtpSsl(mockCredentials.smtpSsl());
        return securityContext;
    }
}

/*
 * CredentialsRefreshFilter.java
 *
 * Created on 2019-02-22, 11:21
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
package dhomo.crmmail.api.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import dhomo.crmmail.api.configuration.AppConfiguration;
import dhomo.crmmail.api.credentials.UsersService;
import dhomo.crmmail.api.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
public class CredentialsRefreshFilter extends OncePerRequestFilter {

    private final UsersService usersService;
    private final AppConfiguration appConfiguration;

    /**
     * Refreshes {@link AuthenticationToken} expiry date and writes new values to the provided {@link HttpServletResponse} Headers.
     *
     * <p>Credentials only refreshed if they are close to expiry
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof Credentials user
                && user.isAuthenticated()) {
            final Instant timeToRefresh = user.getExpiryDate()
                    .minus(appConfiguration.getCredentialsRefreshBeforeDuration());
            if (Instant.now().isAfter(timeToRefresh)) {
                try {
                    response.setHeader(HttpHeaders.ISOTOPE_CREDENTIALS, usersService.getEncryptedAuthToken(user));
                } catch(JsonProcessingException ex) {
                    log.info("Couldn't refresh credentials", ex);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

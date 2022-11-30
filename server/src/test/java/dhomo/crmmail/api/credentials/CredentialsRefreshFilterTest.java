/*
 * CredentialsRefreshFilterTest.java
 *
 * Created on 2019-02-23, 16:21
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
package dhomo.crmmail.api.credentials;

import dhomo.crmmail.api.configuration.AppConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2019-02-23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CredentialsRefreshFilterTest {

    private UsersService usersService;
    private CredentialsRefreshFilter credentialsRefreshFilter;
    @MockBean
    private AppConfiguration appConfiguration;

    @Before
    public void setUp() {
        usersService = Mockito.mock(UsersService.class);
        credentialsRefreshFilter = new CredentialsRefreshFilter(usersService, appConfiguration);
    }

    @After
    public void tearDown() {
        credentialsRefreshFilter = null;
        usersService = null;
        appConfiguration = null;
    }

    @Test
    public void doFilter_authenticatedUser_shouldRefreshCredentials() throws Exception {
        // Given
        final Credentials credentials =  Credentials.authenticated(new User(),null, Instant.now().plus(Duration.ofMinutes(1L)));
        SecurityContextHolder.getContext().setAuthentication(credentials);
        final HttpServletResponse response = new MockHttpServletResponse();
        final FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        doReturn(Duration.ofMinutes(5L)).when(appConfiguration).getCredentialsRefreshBeforeDuration();

        // When
        credentialsRefreshFilter.doFilter(new MockHttpServletRequest(), response, mockFilterChain);

        // Then
        verify(usersService, times(1)).getEncryptedAuthToken(Mockito.eq(credentials));
        verify(mockFilterChain, times(1)).doFilter(Mockito.any(ServletRequest.class), Mockito.eq(response));
    }

    @Test
    public void doFilter_unAuthenticatedUser_shouldRefreshCredentials() throws Exception {
        // Given
        SecurityContextHolder.getContext().setAuthentication(null);
        final HttpServletResponse response = new MockHttpServletResponse();
        final FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        // When
        credentialsRefreshFilter.doFilter(new MockHttpServletRequest(), response, mockFilterChain);

        // Then
        verify(usersService, times(0)).getEncryptedAuthToken(Mockito.any(Credentials.class));
        verify(mockFilterChain, times(1)).doFilter(Mockito.any(ServletRequest.class), Mockito.eq(response));
    }
}

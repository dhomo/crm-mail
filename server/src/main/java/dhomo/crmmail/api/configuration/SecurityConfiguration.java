/*
 * SecurityConfiguration.java
 *
 * Created on 2019-02-23, 7:35
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

import dhomo.crmmail.api.authentication.CredentialsRefreshFilter;
import dhomo.crmmail.api.authentication.CustomAuthProvider;
import dhomo.crmmail.api.credentials.UsersService;
import dhomo.crmmail.api.authentication.TokenAutheConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final RequestMatcher publicMatchers =  new OrRequestMatcher(
            //                new RegexRequestMatcher(OPEN_API, "GET"),
            new RegexRequestMatcher("(/api)?/actuator/health", "GET"),
            new RegexRequestMatcher("(/api)?/v1/application/configuration", "GET"),
            new RegexRequestMatcher("(/api)?/v1/application/login", "POST")
    );

    private final UsersService usersService;
    private final CustomAuthProvider customAuthProvider;
    private final TokenAutheConverter tokenAuthConverter;
    private final AppConfiguration configuration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(@Value("${ADMIN_PASS:pass}") String pass) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode(pass))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder bCryptPasswordEncoder,
                                             UserDetailsService userDetailsService) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.authenticationProvider(customAuthProvider);
        authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http,
                                           final AuthenticationManager authenticationManager) throws Exception {

        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authenticationManager(authenticationManager);
        http.httpBasic();
        http.authorizeRequests()
                .requestMatchers(publicMatchers).permitAll()
                .anyRequest().authenticated();
        var authFilter = new AuthenticationFilter(authenticationManager, tokenAuthConverter);
        authFilter.setSuccessHandler((request, response, authentication) -> {/* do nothing*/});
        http.addFilterAfter(authFilter, BasicAuthenticationFilter.class);
        http.addFilterAfter(new CredentialsRefreshFilter(usersService, configuration),
                    AuthenticationFilter.class);
        return http.build();
    }
}

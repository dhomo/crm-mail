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

import dhomo.crmmail.api.credentials.CredentialsAuthenticationFilter;
import dhomo.crmmail.api.credentials.CredentialsRefreshFilter;
import dhomo.crmmail.api.credentials.CredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String ACTUATOR_REGEX = "(/api)?/actuator/health";
    private static final String CONFIGURATION_REGEX = "(/api)?/v1/application/configuration";
    private static final String LOGIN_REGEX = "(/api)?/v1/application/login";
//    public static final String OPEN_API = "/api-docs.*";

    private final CredentialsService credentialsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(@Value("${ADMIN_PASS:pass}") String pass) {
        UserDetails user = User
                .withUsername("admin")
                .password(passwordEncoder().encode(pass))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // если правильно понимаю то это создаст отдельную цепочку фильтров или вовсе не погонит по цепочке фильтров заматченные урлы
    // нужно протестить
//    @Bean
//    public WebSecurityCustomizer ignoreResources() {
//        return (webSecurity) -> webSecurity
//                .ignoring()
//                .antMatchers("/test/*");
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final RequestMatcher negatedPublicMatchers =  new NegatedRequestMatcher(new OrRequestMatcher(
//                new RegexRequestMatcher(OPEN_API, "GET"),
                new RegexRequestMatcher(ACTUATOR_REGEX, "GET"),
                new RegexRequestMatcher(CONFIGURATION_REGEX, "GET"),
                new RegexRequestMatcher(LOGIN_REGEX, "POST")
        ));
        http
            .csrf().disable()
            .httpBasic()
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .requestMatchers(negatedPublicMatchers).authenticated()
                .and()
            .cors()
                .and()
            .addFilterAfter(new CredentialsAuthenticationFilter(negatedPublicMatchers, credentialsService),
                    BasicAuthenticationFilter.class)
            .addFilterAfter(new CredentialsRefreshFilter(credentialsService),
                    CredentialsAuthenticationFilter.class);
        return http.build();
    }
}

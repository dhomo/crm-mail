/*
 * WebConfiguration.java
 *
 * Created on 2018-08-09, 7:55
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.mail.util.MailSSLSocketFactory;
import dhomo.crmmail.api.imap.ImapService;
import dhomo.crmmail.api.lead.LeadRepository;
import dhomo.crmmail.api.message.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer, AsyncConfigurer {

    public static final String IMAP_SERVICE_PROTOTYPE = "prototypeImapService";
    private static final String DEVELOPMENT_PROFILE = "dev";

    private final Environment environment;

    @Autowired
    public WebConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(getAsyncExecutor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new Hibernate5Module()
                        .configure(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true))
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }

    @Override
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);
        executor.setMaxPoolSize(42);
        executor.setQueueCapacity(11);
        executor.setThreadNamePrefix("MyExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        if (Stream.of(this.environment.getActiveProfiles()).anyMatch(DEVELOPMENT_PROFILE::equals)) {
            corsConfigurationSource.registerCorsConfiguration("/v1/**",
                    new CorsConfiguration().applyPermitDefaultValues());
        }
        return corsConfigurationSource;
    }

    @Bean(name = IMAP_SERVICE_PROTOTYPE)
    @Scope(SCOPE_PROTOTYPE)
    @Qualifier(IMAP_SERVICE_PROTOTYPE)
    public ImapService imapService(
            AppConfiguration appConfiguration, MailSSLSocketFactory mailSSLSocketFactory,
            MessageRepository messageRepository, LeadRepository leadRepository) {

        return new ImapService(appConfiguration, mailSSLSocketFactory, messageRepository, leadRepository);
    }
}

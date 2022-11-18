/*
 * Configuration.java
 *
 * Created on 2018-08-08, 16:35
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

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.keygen.KeyGenerators;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.Set;

@Configuration
@Getter
public class AppConfiguration {

    public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    @Value("${ENCRYPTION_PASSWORD:THIS IS PASSWORD DEFAULT. IT SHOULD BE REPLACED USING ENV VARIABLE\"}")
    private String encryptionPassword;

    // :#{null} работает так: если переменная окружения не определена, то оставляем предыдущее значение
    @Value("${TRUSTED_HOSTS:#{null}}")
    private Set<String> trustedHosts = Collections.emptySet();

    @Value("${EMBEDDED_IMAGE_SIZE_THRESHOLD:51200}")
    private long embeddedImageSizeThreshold;

    @Value("${CREDENTIALS_SALT:#{null}}")
    private String salt = KeyGenerators.string().generateKey();

    private TemporalAmount credentialsDuration;
    private TemporalAmount credentialsRefreshBeforeDuration ;


    public AppConfiguration(@Value("${CREDENTIALS_DURATION_MINUTES:15}") long credentialsDurationMinutes,
                            @Value("${CREDENTIALS_REFRESH_BEFORE_DURATION_MINUTES:10}") long crRefBeforeDurMinutes) {
        credentialsDuration = Duration.ofMinutes(credentialsDurationMinutes);
        credentialsRefreshBeforeDuration = Duration.ofMinutes(crRefBeforeDurMinutes);
    }
}

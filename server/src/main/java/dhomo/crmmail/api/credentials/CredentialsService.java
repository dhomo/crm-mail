/*
 * CredentialsService.java
 *
 * Created on 2018-08-15, 21:46
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dhomo.crmmail.api.configuration.AppConfiguration;
import dhomo.crmmail.api.exception.AuthenticationException;
import dhomo.crmmail.api.exception.NotFoundException;
import dhomo.crmmail.api.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static dhomo.crmmail.api.exception.AuthenticationException.Type.BLACKLISTED;

/**
 * Created by Marc Nuri <marc@marcnuri.com> on 2018-08-15.
 */
@Service
@RequiredArgsConstructor
public class CredentialsService {

    private static final Logger log = LoggerFactory.getLogger(CredentialsService.class);

    private final ObjectMapper objectMapper;
    private final AppConfiguration appConfiguration;
    private final CredentialsRepository credentialsRepository;

    public void checkHost(Credentials credentials) {
        final Set<String> trustedHosts = appConfiguration.getTrustedHosts();
        if (!trustedHosts.isEmpty() && !trustedHosts.contains(credentials.getServerHost())){
            throw new AuthenticationException(BLACKLISTED);
        }
    }

    /**
     * Parses {@link HttpServletRequest} for isotope credentials to decode them and return
     * a valid {@link Credentials} object.
     *
     * @param httpServletRequest from which to extract Isotope Credentials HttpHeaders
     * @return Credentials obtained and validated from httpServletRequest headers
     */
    Credentials fromRequest(HttpServletRequest httpServletRequest) {
        try {
            final String encryptedCredentials = httpServletRequest.getHeader(HttpHeaders.ISOTOPE_CREDENTIALS);
            if (StringUtils.isEmpty(encryptedCredentials)) {
                throw new AuthenticationException("Isotope credentials headers missing");
            }
            final Credentials credentials =  decrypt(encryptedCredentials);
            if (credentials.getExpiryDate().compareTo(ZonedDateTime.now(ZoneOffset.UTC)) < 0) {
                throw new AuthenticationException("Expired credentials");
            }
            return credentials;
        } catch(IOException ex) {
            throw new AuthenticationException("Invalid credentials", ex);
        }
    }

    /**
     * Refreshes {@link Credentials} expiry date and writes new values to the provided {@link HttpServletResponse} Headers.
     *
     * <p>Credentials only refreshed if they are close to expiry (remaining lifetime < 10 minutes)
     *
     * @param oldCredentials with "outdated" expiry date
     * @param response to which to write new encrypted credentials headers
     */
    void refreshCredentials(Credentials oldCredentials, HttpServletResponse response) {
        final ZonedDateTime timeToRefresh = oldCredentials.getExpiryDate()
                .minus(appConfiguration.getCredentialsRefreshBeforeDuration());
        if (ZonedDateTime.now(ZoneOffset.UTC).isBefore(timeToRefresh)) {
            return;
        }
        try {
            response.setHeader(HttpHeaders.ISOTOPE_CREDENTIALS, encrypt(oldCredentials));
        } catch(JsonProcessingException ex) {
            log.info("Couldn't refresh credentials", ex);
        }
    }

    public String encrypt(Credentials credentials) throws JsonProcessingException {
        // Add expiry date
        credentials.setExpiryDate(ZonedDateTime.now(ZoneOffset.UTC).plus(appConfiguration.getCredentialsDuration()));
        final TextEncryptor encryptor = Encryptors.text(appConfiguration.getEncryptionPassword(),
                appConfiguration.getSalt());
        return encryptor.encrypt(objectMapper.writeValueAsString(credentials));
    }

    private Credentials decrypt(String encrypted) throws JsonProcessingException {
        try {
            final TextEncryptor encryptor = Encryptors.text(appConfiguration.getEncryptionPassword(), appConfiguration.getSalt());
            return objectMapper.readValue(encryptor.decrypt(encrypted), Credentials.class);
        } catch(IllegalStateException ex) {
            throw new AuthenticationException("Key or salt is not compatible with encrypted credentials" +
                    " (Server has changed the password or user tampered with credentials.", ex);
        }
    }

    public Credentials findCredential(String user) {
        var credentials = credentialsRepository.findById(user)
                .orElseThrow(()->new NotFoundException("User not found"));
        return (credentials);
    }

    public List<Credentials> getAllCredentials(){
        return credentialsRepository.findAll();
    }

    public void saveCredential(Credentials credentials){
        credentialsRepository.save(credentials);
    }

}

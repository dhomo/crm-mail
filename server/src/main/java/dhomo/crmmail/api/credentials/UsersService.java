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
import dhomo.crmmail.api.authentication.Credentials;
import dhomo.crmmail.api.configuration.AppConfiguration;
import dhomo.crmmail.api.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static dhomo.crmmail.api.exception.AuthenticationException.Type.BLACKLISTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final ObjectMapper objectMapper;
    private final AppConfiguration appConfiguration;
    private final UserRepository userRepository;

    public void checkHost(Credentials credentials) {
        final Set<String> trustedHosts = appConfiguration.getTrustedHosts();
        if (!trustedHosts.isEmpty() && !trustedHosts.contains(credentials.getPrincipal().getServerHost())){
            throw new AuthenticationException(BLACKLISTED);
        }
    }

    public String getEncryptedAuthToken(Credentials credentials) throws JsonProcessingException {
        // Add expiry date
        credentials.setExpiryDate(Instant.now().plus(appConfiguration.getCredentialsDuration()));
        final TextEncryptor encryptor = Encryptors.text(appConfiguration.getTokenEncryptionPassword(),
                appConfiguration.getTokenSalt());
        return encryptor.encrypt(objectMapper.writeValueAsString(credentials));
    }

    public User findUser(String user) {
        return (userRepository.findByNameIgnoreCase(user)
                .orElseThrow(()->new AuthenticationException(AuthenticationException.Type.NOT_FOUND)));
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional
    public User saveUser(User user){
        return userRepository.save(user);
    }

}

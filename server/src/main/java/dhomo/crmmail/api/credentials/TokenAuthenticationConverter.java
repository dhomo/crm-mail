package dhomo.crmmail.api.credentials;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dhomo.crmmail.api.configuration.AppConfiguration;
import dhomo.crmmail.api.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationConverter implements AuthenticationConverter {

    private final AppConfiguration appConf;
    private final ObjectMapper objectMapper;

    @Override
    public AuthenticationToken convert(HttpServletRequest request) {
        final String encryptedHeader = request.getHeader(HttpHeaders.ISOTOPE_CREDENTIALS);
        if (!StringUtils.hasText(encryptedHeader)) {
            return null;
        }
        return decrypt(encryptedHeader);
    }

    /**
     * Расшифровываем хедер, при ошибке вызывается {@link BadCredentialsException}
     * @param encrypted шифрованный хедер
     * @return {@link AuthenticationToken}
     */
    private AuthenticationToken decrypt(String encrypted)   {
        final TextEncryptor encryptor = Encryptors.text(appConf.getTokenEncryptionPassword(), appConf.getTokenSalt());
        try {
            return objectMapper.readValue(encryptor.decrypt(encrypted), AuthenticationToken.class);
        } catch(JsonProcessingException ex) {
            throw new BadCredentialsException("Key or salt is not compatible with encrypted credentials" +
                    " (Server has changed the password or user tampered with credentials.", ex);
        }
    }
}

package dhomo.crmmail.api.authentication;

import dhomo.crmmail.api.credentials.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    /**
     *
     * @return аутентифицированный Credentials
     */
    @Override
    public Credentials authenticate(Authentication auth)
           throws AuthenticationException {

        var token = (AuthenticationToken) auth;
        String username = token.getName();
        var user = userRepository.findByNameIgnoreCase(username).orElseThrow(
                () -> new BadCredentialsException("User not found"));

        if (!user.isEnabled())  {
            log.debug("Failed to authenticate since user account is disabled");
            throw new DisabledException("User is disabled");
        }
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new AccountExpiredException("Expired credentials token");
        }
        return Credentials.authenticated(user, token.getCredentials().toString(), token.getExpiryDate());
   }

   @Override
   public boolean supports(Class<?> auth) {
       return (AuthenticationToken.class.isAssignableFrom(auth));
   }
}

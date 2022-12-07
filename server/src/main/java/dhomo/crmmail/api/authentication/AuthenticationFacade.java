package dhomo.crmmail.api.authentication;

import dhomo.crmmail.api.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    public Credentials getCredentials(){
        return (Credentials) SecurityContextHolder.getContext().getAuthentication();
    }

    public User getUser(){
        return getCredentials().getPrincipal();
    }
}

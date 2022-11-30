package dhomo.crmmail.api.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


import java.time.Instant;
import java.util.Collection;


@Accessors(chain = true)
@EqualsAndHashCode
public class Credentials implements Authentication {

    private final User user;

    private final String password;

    private boolean authenticated = false;

    @Setter
    @Getter
    private Instant expiryDate;


    private Credentials(User user, String password, Instant expiryDate, boolean authenticated) {
        this.user = user;
        this.password = password;
        this.authenticated = authenticated;
        this.expiryDate = expiryDate;
    }

    public static Credentials unauthenticated(User user, String password, Instant expiryDate){
        return new Credentials(user, password, expiryDate, false);
    }
    public static Credentials unauthenticated(User user, String password){
        return new Credentials(user, password, null, false);
    }

    public static Credentials authenticated(User user, String password, Instant expiryDate){
        return new Credentials(user, password, expiryDate, true);
    }

    @Override
    public String getName() {
        return this.user.getUserName();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getRoles();
    }

    @JsonIgnore
    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    /**
     *
     * @return String password
     */
    @Override
    public String getCredentials() {
        return this.password;
    }

    @JsonIgnore
    @Override
    public Object getDetails() {
        return null;
    }

    /**
     *
     * @return {@link User}
     */
    @JsonIgnore
    @Override
    public User getPrincipal() {
        return this.user;
    }
}

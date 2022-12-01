package dhomo.crmmail.api.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;

@Getter
@Setter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private Instant expiryDate;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>AuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     *
     */
    public AuthenticationToken(@JsonProperty("name") Object name,
                               @JsonProperty("credentials") Object credentials,
                               @JsonProperty("expiryDate") Instant expiryDate) {
        super(name, credentials);
        this.expiryDate = expiryDate;
    }
}
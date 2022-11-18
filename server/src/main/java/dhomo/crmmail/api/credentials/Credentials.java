/*
 * Credentials.java
 *
 * Created on 2018-08-15, 18:10
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"authorities"})
@Entity
@Getter
@Setter
public class Credentials extends AbstractAuthenticationToken implements Serializable {

    @Serial
    private static final long serialVersionUID = -3763522029969923952L;

    private static final GrantedAuthority ISOTOPE_USER = new SimpleGrantedAuthority("ISOTOPE_USER");

    @NotNull(groups=Login.class)
    private String serverHost;

    @NotNull(groups=Login.class)
    @Positive(groups=Login.class)
    private Integer serverPort;

    @Id @NotNull(groups=Login.class)
    @Column(name = "user_name")
    private String user;

    @Transient
    @NotNull(groups=Login.class)
    private String password;

    @NotNull(groups=Login.class)
    private Boolean imapSsl;

    private String smtpHost;

    @NotNull(groups=Login.class)
    @Positive(groups=Login.class)
    private Integer smtpPort;

    @NotNull(groups=Login.class)
    private Boolean smtpSsl;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @Transient
    private ZonedDateTime expiryDate;



    @JsonCreator
    public Credentials() {
        super(Collections.singleton(ISOTOPE_USER));
    }

    @Transient
    @Override
    public Object getPrincipal() {
        return getUser();
    }

    @Transient
    @Override
    public Object getCredentials() {
        return getPassword();
    }


    @Transient
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> (GrantedAuthority) role).toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Credentials that = (Credentials) o;
        return  Objects.equals(serverHost, that.serverHost) &&
                Objects.equals(serverPort, that.serverPort) &&
                Objects.equals(user, that.user) &&
                Objects.equals(password, that.password) &&
                Objects.equals(imapSsl, that.imapSsl) &&
                Objects.equals(smtpHost, that.smtpHost) &&
                Objects.equals(smtpPort, that.smtpPort) &&
                Objects.equals(smtpSsl, that.smtpSsl) &&
                Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serverHost, serverPort, user, password, imapSsl, smtpHost, smtpPort, smtpSsl, expiryDate);
    }

    /**
     * Validation Group interface for login
     */
    public interface Login {}

}

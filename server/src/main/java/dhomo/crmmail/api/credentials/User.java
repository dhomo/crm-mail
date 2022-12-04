package dhomo.crmmail.api.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @NotNull
    private String serverHost;

    @NotNull
    @Positive
    private Integer serverPort;

    @NotBlank
    @NotNull
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @NotNull
    private Boolean imapSsl;

    @NotNull
    @NotBlank
    private String smtpHost;

    @NotNull
    @Positive
    private Integer smtpPort;

    @NotNull
    private Boolean smtpSsl;

    private boolean enabled = true;

    @NotNull
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"))
    @Fetch(FetchMode.SUBSELECT)
    private Set<Role> roles = new LinkedHashSet<>();

    public User addRole(Role role){
        this.roles.add(role);
        return this;
    }

    public User removeRole(Role role){
        this.roles.remove(role);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "id = " + id + ", " + "name = " + userName + ")";
    }

}

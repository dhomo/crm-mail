package dhomo.crmmail.api.user;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class EmailServer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    @NotNull
    private String imapHost;
    @NotNull
    @Positive
    private Integer imapPort;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EmailServer that = (EmailServer) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "id = " + id + ", " + "imapHost = " + imapHost + ", " + "imapPort = " + imapPort + ", " + "imapSsl = " + imapSsl + ", " + "smtpHost = " + smtpHost + ", " + "smtpPort = " + smtpPort + ", " + "smtpSsl = " + smtpSsl + ")";
    }
}
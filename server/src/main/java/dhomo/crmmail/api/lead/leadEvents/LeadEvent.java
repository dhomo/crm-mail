package dhomo.crmmail.api.lead.leadEvents;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dhomo.crmmail.api.credentials.Role;
import dhomo.crmmail.api.credentials.User;
import dhomo.crmmail.api.lead.Lead;
import dhomo.crmmail.api.lead.SecurityData;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
// @SuperBuilder
// @NoArgsConstructor
@Accessors(chain = true)
public abstract class LeadEvent implements SecurityData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(nullable = false)
    private User owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "lead_event_roles", joinColumns = @JoinColumn(name = "lead_event_id"), inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Set<Role> allowed = new LinkedHashSet<>();

    private ZonedDateTime createdDate = ZonedDateTime.now();

    @JsonBackReference
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Transient
    public abstract String getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        LeadEvent leadEvent = (LeadEvent) o;
        return id != null && Objects.equals(id, leadEvent.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.credentials.Role;
import dhomo.crmmail.api.credentials.User;
import dhomo.crmmail.api.lead.leadEvents.LeadEvent;
import dhomo.crmmail.api.lead.leadStatus.LeadStatus;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Table(name = "Lead", indexes = {@Index(name = "idx_lead_name", columnList = "name")})
@Getter
@Setter
@Accessors(chain = true)
public class Lead implements SecurityData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // закладываемся на то что имя может измениться, поэтому не используем его в качестве NaturalId
    @Column(unique = true)
    @NotBlank(groups = {Lead.New.class})
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotNull
    private ZonedDateTime creationDateTime = ZonedDateTime.now();;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "status_id")
    private LeadStatus status;

    @Column(length = 1000)
    private String summary = "";

    // если пусто, то доступ для всех разрешен
    //  если содержит несколько ролей, то доступ только для тех кто имеет ВСЕ указанные роли
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "lead_roles", joinColumns = @JoinColumn(name = "lead_id"), inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private Set<Role> allowed = new LinkedHashSet<>();

    public Lead addAccess(Role role) {
        this.allowed.add(role);
        return this;
    }

    public Lead removeAccess(Role role){
        this.allowed.remove(role);
        return this;
    }

    @OneToMany(mappedBy = "lead",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<LeadEvent> leadEvents = new LinkedHashSet<>();

    public void addLeadEvent(LeadEvent leadEvent){
        this.leadEvents.add(leadEvent);
        leadEvent.setLead(this);
    }

    public void removeLeadEvent(LeadEvent leadEvent){
        this.leadEvents.remove(leadEvent);
        // учитывая orphanRemoval = true, возможно это избыточно
        leadEvent.setLead(null);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Lead lead = (Lead) o;
        return id != null && Objects.equals(id, lead.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public interface New {}
}

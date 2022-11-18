package dhomo.crmmail.api.lead;

import com.fasterxml.jackson.annotation.JsonFormat;
import dhomo.crmmail.api.lead.leadStatus.LeadStatus;
import dhomo.crmmail.api.message.Message;
import lombok.*;
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
@ToString
@RequiredArgsConstructor
public class Lead {
    @NotNull(groups = {Lead.New.class})
    @Id
    private UUID id;

    // закладываемся на то что имя может измениться, поэтому не используем его в качестве NaturalId
    @Column(unique = true)
    @NotBlank(groups = {Lead.New.class})
    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDateTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "status_id")
    private LeadStatus status;

    @NotNull
    @Column(length = 1000)
    private String summary = "";


    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "lead_message",
            joinColumns = @JoinColumn(name = "lead_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id"))
    private Set<Message> messages = new LinkedHashSet<>();

    public void addMessage(Message message){
        this.messages.add(message);
        message.getLeads().add(this);
    }

    public void removeMessage(Message message){
        this.messages.remove(message);
        message.getLeads().remove(this);
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

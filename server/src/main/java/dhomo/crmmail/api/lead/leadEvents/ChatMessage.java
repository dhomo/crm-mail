package dhomo.crmmail.api.lead.leadEvents;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Transient;


@Getter
@Setter
// @SuperBuilder
// @NoArgsConstructor
@Accessors(chain = true)
@Entity
public class ChatMessage extends LeadEvent {

    private String message;

    @Transient
    @Override
    public String getType() {
        return "ChatMessage";
    }
    //
    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    //     ChatMessage that = (ChatMessage) o;
    //     return getId() != null && Objects.equals(getId(), that.getId());
    // }
    //
    // @Override
    // public int hashCode() {
    //     return getClass().hashCode();
    // }
}

package dhomo.crmmail.api.lead.dto;

import dhomo.crmmail.api.lead.Lead;
import dhomo.crmmail.api.lead.leadStatus.LeadStatus;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * A Projection for the {@link Lead} entity
 */
public interface LeadInfo {
    UUID getId();

    String getName();

    ZonedDateTime getCreationDateTime();

    LeadStatusInfo getStatus();

    Set<MessageInfo> getMessages();

    /**
     * A Projection for the {@link LeadStatus} entity
     */
    interface LeadStatusInfo {
        Long getId();
    }

    /**
     * A Projection for the {@link dhomo.crmmail.api.message.Message} entity
     */
    interface MessageInfo {
        String getMessageId();
    }
}
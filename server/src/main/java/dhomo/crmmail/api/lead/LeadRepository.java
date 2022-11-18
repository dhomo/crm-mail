package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.lead.dto.LeadDto_id_name;
import dhomo.crmmail.api.lead.dto.LeadInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    @EntityGraph(attributePaths = {"status", "messages"})
    List<LeadInfo> findAllLeadInfoBy();

    Set<LeadDto_id_name> findByMessages_MessageId(String messageId);
}
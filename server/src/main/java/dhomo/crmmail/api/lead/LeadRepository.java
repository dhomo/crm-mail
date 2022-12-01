package dhomo.crmmail.api.lead;


import dhomo.crmmail.api.lead.dto.LeadDto_id_name;
import dhomo.crmmail.api.lead.dto.LeadInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    @EntityGraph(attributePaths = {"status", "messages"})
    List<LeadInfo> findAllLeadInfoBy();

    @Query("select new dhomo.crmmail.api.lead.dto.LeadDto_id_name(l.id, l.name) from Message m inner join m.lead l where m.messageId = ?1")
    Set<LeadDto_id_name> findByMessages_MessageId(String messageId);
}
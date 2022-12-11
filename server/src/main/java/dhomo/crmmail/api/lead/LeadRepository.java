package dhomo.crmmail.api.lead;


import dhomo.crmmail.api.lead.dto.LeadDto_id_name;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    @EntityGraph(attributePaths = {"leadEvents.allowed", "allowed"}, type = EntityGraph.EntityGraphType.LOAD)
    @Override
    Optional<Lead> findById(Long Long);

    @Override
    @EntityGraph(attributePaths = {"owner", "status", "leadEvents.owner", "leadEvents.allowed", "allowed"})
    List<Lead> findAll();

    @Query("select new dhomo.crmmail.api.lead.dto.LeadDto_id_name(l.id, l.name, m.messageId) from Message m inner join m.lead l where m.messageId = ?1")
    Set<LeadDto_id_name> findByMessages_MessageId(String messageId);

    @Query("select new dhomo.crmmail.api.lead.dto.LeadDto_id_name(l.id, l.name, m.messageId) from Message m inner join m.lead l where m.messageId IN (:messageIds)")
    Set<LeadDto_id_name> findByMessages_MessageIds(List<String> messageIds);
}
package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.user.RoleRepository;
import dhomo.crmmail.api.user.User;
import dhomo.crmmail.api.folder.Folder;
import dhomo.crmmail.api.imap.ImapService;
import dhomo.crmmail.api.lead.leadStatus.LeadStatus;
import dhomo.crmmail.api.lead.leadStatus.LeadStatusRepository;
import dhomo.crmmail.api.message.Message;
import dhomo.crmmail.api.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService {
    private final LeadStatusRepository leadStatusRepository;
    private final LeadRepository leadRepository;
    private final MessageRepository messageRepository;
    private final RoleRepository roleRepository;
    private final ObjectFactory<ImapService> imapServiceFactory;


    /**
     * заполняем дефолтными значениями пустые поля
     * @param lead лид который заполняем
     * @param owner изера которого укажем в качестве владельца, если не был null
     * @return заполненный лид
     */
    public Lead fillDefaults(Lead lead, User owner){
        if (lead.getOwner() == null ) lead.setOwner(owner);
        if (lead.getStatus() == null) lead.setStatus(defaultStatus());
        if (lead.getCreationDateTime() == null) lead.setCreationDateTime(ZonedDateTime.now());
        if (!StringUtils.hasText(lead.getName())) lead.setName(defaultName());
        return lead;
    }

    // TODO: реализовать
    public String defaultName() {
        return "666";
    }

    public LeadStatus defaultStatus(){
        return leadStatusRepository.findNew();
    }

    public void addEmailMessageToLead(Lead lead, String  folderId, Long messageUid, Set<Long> roleIds, User owner){
        Message message = imapServiceFactory.getObject().getMessage(Folder.toId(folderId), messageUid);
        message.setOwner(owner);
        message.setAllowed(new HashSet<>(roleRepository.findAllById(roleIds)));
        lead.addLeadEvent(message);
        leadRepository.save(lead);
    }

    /**
     *
     * @param user
     * @return Все лиды, со всем событиями. Результаты отфильтрованы по правам доступа
     */
    public List<Lead> getAllLeads(User user){
        final var securityFilter = new SecurityPredicate(user);
        var leads = leadRepository.findAll().stream().filter(securityFilter);
        return leads.map(lead ->
                lead.setLeadEvents(lead.getLeadEvents().stream()
                        .filter(securityFilter)
                        .collect(Collectors.toSet())))
                .toList();
    }

    /**
     * сохраняет новый или изменяет существующий лид
     * @param lead лид
     * @return сохраненный лид
     */
    public Lead save(Lead lead){
        return leadRepository.save(lead);
    }

    /**
     *
     * @param leadId
     * @param user
     * @return Возвращает лид если он существует и хватает прав
     */
    public Lead getLead(Long leadId, User user) {
        var securityFilter = new SecurityPredicate(user);
        Lead lead = leadRepository.findById(leadId).filter(securityFilter).orElseThrow();
        lead.setLeadEvents(lead.getLeadEvents().stream()
                .filter(securityFilter)
                .collect(Collectors.toSet()));
        return lead;
    }
}

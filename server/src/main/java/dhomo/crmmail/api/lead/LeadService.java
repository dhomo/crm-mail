package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.credentials.RoleRepository;
import dhomo.crmmail.api.credentials.User;
import dhomo.crmmail.api.exception.NotFoundException;
import dhomo.crmmail.api.folder.Folder;
import dhomo.crmmail.api.imap.ImapService;
import dhomo.crmmail.api.lead.dto.LeadInfo;
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


    public Lead findLead(Long id){
        return leadRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Lead " + id + "not found"));
    }

    public void addEmailMessageToLead(Lead lead, String  folderId, Long messageUid, Set<Long> roleIds, User owner){
        Message message = imapServiceFactory.getObject().getMessage(Folder.toId(folderId), messageUid);
        message.setOwner(owner);
        message.setAllowed(new HashSet<>(roleRepository.findAllById(roleIds)));
        lead.addLeadEvent(message);
        leadRepository.save(lead);
    }

    public List<LeadInfo> getaAllLeads(){
        //        var ret = leadRepository.findAll().stream()
        //                .map( lead -> {
        //                    var dto = mapper.map(lead, LeadDto.class);
        //                    dto.setStatusId(lead.getStatus().getId());
        //                    dto.setMessages(messageRepository.findByLeads_Id(lead.getId()));
        //                    return dto;
        //                })
        //                .toList();
        // return leadRepository.findAllLeadInfoBy().stream().filter();
        return null;
    }

    /**
     * сохраняет новый или изменяет существующий лид
     * @param lead лид
     * @return сохраненный лид
     */
    public Lead save(Lead lead){
        return leadRepository.save(lead);
    }

    public Lead getLead(Long leadId) {
        Lead lead = leadRepository.findById(leadId).filter(new SecurityPredicate()).orElseThrow();
        lead.setLeadEvents(lead.getLeadEvents().stream()
                .filter(new SecurityPredicate())
                .collect(Collectors.toSet()));
        return lead;
    }
}

package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.exception.NotFoundException;
import dhomo.crmmail.api.folder.Folder;
import dhomo.crmmail.api.imap.ImapService;
import dhomo.crmmail.api.lead.dto.LeadInfo;
import dhomo.crmmail.api.lead.leadStatus.LeadStatusRepository;
import dhomo.crmmail.api.message.Message;
import dhomo.crmmail.api.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadService {
    private final LeadStatusRepository leadStatusRepository;
    private final LeadRepository leadRepository;
    private final MessageRepository messageRepository;
    private final ObjectFactory<ImapService> imapServiceFactory;

    @Transactional
    public void addMessage(UUID leadUUID, String  folderId, Long messageUid){
        var lead = leadRepository.findById(leadUUID)
                .orElseThrow(()-> new NotFoundException("Lead " + leadUUID + "not found"));
        Message message = imapServiceFactory.getObject().getMessage(Folder.toId(folderId), messageUid);
        message = messageRepository.findById(message.getMessageId()).orElse(message);
        lead.addMessage(message);
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
        return leadRepository.findAllLeadInfoBy();
    }

    /**
     * Добавляет новый или изменяет существующий лид
     * @param lead лид
     * @return добавленый или обновленный лид
     */
    public Lead putLead(Lead lead){
        if (!leadRepository.existsById(lead.getId())) {
            // заполняем дефолтными значениями пустые поля
            if (lead.getStatus() == null) lead.setStatus(leadStatusRepository.findNew());
            if (lead.getCreationDateTime() == null) lead.setCreationDateTime(ZonedDateTime.now());
        }
        return leadRepository.save(lead);
    }
}

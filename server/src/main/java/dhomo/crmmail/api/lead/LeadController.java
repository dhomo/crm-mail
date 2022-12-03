package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.credentials.User;
import dhomo.crmmail.api.lead.dto.LeadInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping(path = "/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    Lead postLead(@RequestBody(required = false) Lead lead, Principal principal){
        if (lead == null) lead = new Lead();
        // обнуляем владельца и id независимо от того что пришло на вход
        lead.setOwner(null);
        lead.setId(null);
        var newLead = leadService.fillDefaults(lead, (User) principal);
        return leadService.save(newLead);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<LeadInfo> getAll(){
        return leadService.getaAllLeads();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    Lead putLead(@RequestBody Lead lead, Principal principal){
        if(new SecurityPredicate((User) principal).test(lead)){
            throw new BadCredentialsException("Недостаточно прав для изменения лида");
        }
        return leadService.save(lead);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/addEmail")
    void addMessage(@PathVariable Long id,
                                 @RequestParam("folderId") String  folderId,
                                 @RequestParam("messageUid") Long messageUid,
                                 @RequestParam(name = "roleIds", required = false) Set<Long> roleIds,
                                 Principal principal){

        leadService.addEmailMessageToLead(leadService.findLead(id), folderId, messageUid, roleIds, (User) principal);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/newWithEmail")
    void newLeadWithMessage(@RequestParam("folderId") String  folderId,
                                          @RequestParam("messageUid") Long messageUid,
                                          @RequestParam(name = "roleIds", required = false) Set<Long> roleIds,
                                          Principal principal){
        var newLead = leadService.fillDefaults(new Lead(), (User) principal);
        leadService.addEmailMessageToLead(newLead, folderId, messageUid, roleIds, (User) principal);
    }
}

package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.lead.dto.LeadInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PutMapping
    ResponseEntity<Lead> putLead(@Validated(Lead.New.class) @RequestBody Lead lead){
        return ResponseEntity.ok(leadService.putLead(lead));
    }

    @GetMapping
    ResponseEntity<List<LeadInfo>> getAll(){
        return ResponseEntity.ok(leadService.getaAllLeads());
    }

    @PutMapping("/{id}")
    ResponseEntity<?> addMessage(@PathVariable Long id,
                              @RequestParam("folderId") String  folderId, @RequestParam("messageUid") Long messageUid){

        leadService.addEmailMessage(id, folderId, messageUid);
        return ResponseEntity.ok().build();
    }
}

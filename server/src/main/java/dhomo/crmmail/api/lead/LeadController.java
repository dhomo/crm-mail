package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.lead.dto.LeadInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


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

    @PutMapping("/{uuid}")
    ResponseEntity addMessage(@PathVariable UUID uuid,
                              @RequestParam("folderId") String  folderId, @RequestParam("uid") Long uid){

        leadService.addMessage(uuid, folderId, uid);
        return ResponseEntity.ok().build();
    }
}

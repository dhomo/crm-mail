package dhomo.crmmail.api.lead.dto;

import dhomo.crmmail.api.lead.Lead;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * A DTO for the {@link Lead} entity
 */
@Data
public class LeadDto_id_name implements Serializable {
    @NotNull(groups = {Lead.New.class})
    private final UUID id;
    @NotBlank(groups = {Lead.New.class})
    private final String name;
}
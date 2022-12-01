package dhomo.crmmail.api.lead.dto;

import dhomo.crmmail.api.lead.Lead;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link Lead} entity
 */
@Data
public class LeadDto_id_name implements Serializable {
    private final Long id;
    private final String name;
}
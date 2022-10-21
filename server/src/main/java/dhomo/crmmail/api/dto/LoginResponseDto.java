package dhomo.crmmail.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link dhomo.crmmail.api.credentials.Credentials} entity
 */
@Data
public class LoginResponseDto implements Serializable {
    private String encrypted;
    private String salt; // всегда  = none
}
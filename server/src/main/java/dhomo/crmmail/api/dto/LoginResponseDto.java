package dhomo.crmmail.api.dto;

import dhomo.crmmail.api.authentication.Credentials;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link Credentials} entity
 */
@Data
public class LoginResponseDto implements Serializable {
    private String encrypted;
    private String salt; // всегда  = none
}
package dhomo.crmmail.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dhomo.crmmail.api.authentication.Credentials;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link Credentials} entity
 */
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class LoginRequestDto implements Serializable {
    @NotNull @NotBlank
    private String user;
    @NotNull @NotBlank
    private String password;
}
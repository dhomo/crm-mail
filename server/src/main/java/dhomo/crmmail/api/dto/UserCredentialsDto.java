package dhomo.crmmail.api.dto;

import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UserCredentialsDto {
    @NotNull
    private String user;

    @NotNull
    @NotBlank
    private String serverHost;

    @NotNull
    @Positive
    private Integer serverPort;

    @NotNull
    private Boolean imapSsl;

    @NotNull
    @NotBlank
    private String smtpHost;

    @NotNull
    @Positive
    private Integer smtpPort;

    @NotNull
    private Boolean smtpSsl;
}

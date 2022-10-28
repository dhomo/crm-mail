package dhomo.crmmail.api.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UsersSetDto {
    Set<UserCredentialsDto> usersSet;
}

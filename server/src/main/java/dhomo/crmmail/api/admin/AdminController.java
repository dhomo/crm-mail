package dhomo.crmmail.api.admin;

import dhomo.crmmail.api.credentials.Credentials;
import dhomo.crmmail.api.credentials.CredentialsRepository;
import dhomo.crmmail.api.credentials.CredentialsService;
import dhomo.crmmail.api.dto.UserCredentialsDto;
import dhomo.crmmail.api.dto.UsersSetDto;
import dhomo.crmmail.api.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ModelMapper mapper;
    // пока код простой - будет долбиться напрямик в репозиторий, а если логика потребуется посложнее то будем через сервис работать
    private final CredentialsRepository credentialsRepository;
    private final CredentialsService credentialsService;

    @RequestMapping(path ="/login",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST })
    public ResponseEntity login(){
        return ResponseEntity.ok().build();
    }

    //    Добавляем юзера в список тех кому разрешен доступ, но не проверяем т.к. пароля не знаем и не храним
    @PutMapping(path = "/users")
    public ResponseEntity putUser(@Validated() @RequestBody UserCredentialsDto userCredentialsDto) {

        log.info("Create or update user email credentials");
        try {
            var credentials = mapper.map(userCredentialsDto, Credentials.class);

            credentialsRepository.saveAndFlush(credentials);
            return ResponseEntity.ok("");
        } catch (RuntimeException ex){
            throw new AuthenticationException("Invalid credentials", ex);
        }
    }

    //    Добавляем юзера в список тех кому разрешен доступ, но не проверяем т.к. пароля не знаем и не храним
    @GetMapping(path = "/users")
    public ResponseEntity<UsersSetDto> getUsers() {
        log.info("Get all user email credentials");
        var usersSetDto = new UsersSetDto();
        usersSetDto.setUsersSet(credentialsService.getAllCredentials()
                .stream().map(credentials -> mapper.map(credentials, UserCredentialsDto.class))
                .collect(Collectors.toSet()));
        return ResponseEntity.ok(usersSetDto);
    }

}

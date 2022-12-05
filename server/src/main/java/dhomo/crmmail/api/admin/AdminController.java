package dhomo.crmmail.api.admin;


import dhomo.crmmail.api.exception.InvalidFieldException;
import dhomo.crmmail.api.lead.leadStatus.LeadStatus;
import dhomo.crmmail.api.lead.leadStatus.LeadStatusRepository;
import dhomo.crmmail.api.user.User;
import dhomo.crmmail.api.user.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ModelMapper mapper;
    private final UsersService usersService;
    private final LeadStatusRepository leadStatusRepository;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    void login(){
        log.info("проверка админского доступа");
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/users")
    List<User> getUsers() {
        log.info("Get all user email credentials");
        return usersService.findAll();
    }

    /**
     * Добавляем юзера в список тех кому разрешен доступ.
     * Данные не проверяем т.к. пароля не знаем и не храним
     */
    @PostMapping(path = "/users")
    User postUser(@Validated() @RequestBody User user) {
        log.info("Add new user");
        user.setId(null);
        try {
            return usersService.saveUser(user);
        } catch (DataIntegrityViolationException e)
        {
            throw new InvalidFieldException( user.getUserName() + " already exists");
        }
    }

    @PutMapping(path = "/users")
    User putUser(@Validated() @RequestBody User user) {
        log.info("Update user id:" + user.getId());
        if (user.getId() == null){
            throw new InvalidFieldException("Lead id should not be null ");
        }
        return usersService.saveUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/LeadStatuses")
    List<LeadStatus> getLeadStatuses(){
        return leadStatusRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping ("/LeadStatuses")
    LeadStatus postLeadStatus(@RequestBody LeadStatus leadStatus){
        return leadStatusRepository.save(leadStatus);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping ("/LeadStatuses")
    LeadStatus putLeadStatus(@RequestBody LeadStatus leadStatus){
        return leadStatusRepository.save(leadStatus);
    }
}

package dhomo.crmmail.api.admin;


import dhomo.crmmail.api.credentials.User;
import dhomo.crmmail.api.credentials.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(path ="/login",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST })
    public ResponseEntity<?> login(){
        return ResponseEntity.ok().build();
    }

    //    Добавляем юзера в список тех кому разрешен доступ, но не проверяем т.к. пароля не знаем и не храним
    @PutMapping(path = "/users")
    public ResponseEntity<User> putUser(@Validated() @RequestBody User user) {

        log.info("Create or update user ");
            return ResponseEntity.ok(usersService.putUser(user));
    }

    //    Добавляем юзера в список тех кому разрешен доступ, но не проверяем т.к. пароля не знаем и не храним
    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> getUsers() {
        log.info("Get all user email credentials");
        var users = usersService.findAll();
        return ResponseEntity.ok(users);
    }

}

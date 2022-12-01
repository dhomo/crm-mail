package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.credentials.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Predicate;

class SecurityPredicate implements Predicate<SecurityData> {

    private final User authenticatedUser;

    SecurityPredicate(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    SecurityPredicate(){
        this.authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * проверка прав юзера
     * @param object the input argument
     * @return true если владелец совпадает с юзером или если у юзера есть ВСЕ необходимые роли
     */
    @Override
    public boolean test(SecurityData object) {
        return authenticatedUser.equals(object.getOwner()) || authenticatedUser.getRoles().containsAll(object.getAllowed());
    }
}

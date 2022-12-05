package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.user.Role;
import dhomo.crmmail.api.user.User;

import java.util.Set;

public interface SecurityData {
    User getOwner();
    Set<Role> getAllowed();
}

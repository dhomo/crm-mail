package dhomo.crmmail.api.lead;

import dhomo.crmmail.api.credentials.Role;
import dhomo.crmmail.api.credentials.User;

import java.util.Set;

public interface SecurityData {
    User getOwner();
    Set<Role> getAllowed();
}

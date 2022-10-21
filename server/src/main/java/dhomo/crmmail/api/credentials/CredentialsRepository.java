package dhomo.crmmail.api.credentials;

import dhomo.crmmail.api.credentials.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<Credentials, String> {
}
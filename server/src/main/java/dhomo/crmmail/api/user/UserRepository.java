package dhomo.crmmail.api.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<User> findByUserNameIgnoreCase(String name);

    @EntityGraph(attributePaths = {"emailServer", "roles"}, type = EntityGraph.EntityGraphType.LOAD)
    @Override
    List<User> findAll();
}
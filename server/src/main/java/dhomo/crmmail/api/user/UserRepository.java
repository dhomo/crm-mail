package dhomo.crmmail.api.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"roles"})
    @Query("select u from User u where upper(u.userName) = upper(?1)")
    Optional<User> findByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = {"roles"})
    @Override
    List<User> findAll();
}
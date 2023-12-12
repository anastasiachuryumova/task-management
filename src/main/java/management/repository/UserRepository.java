package management.repository;

import management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String username);

    Boolean existsByLogin(String username);
}

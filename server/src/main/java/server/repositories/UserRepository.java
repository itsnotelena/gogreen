package server.repositories;

import org.springframework.data.repository.CrudRepository;
import shared.models.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByUsername(String username);
}

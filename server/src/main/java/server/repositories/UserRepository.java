package server.repositories;

import org.springframework.data.repository.CrudRepository;
import shared.models.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByUsername(String username);

    List<User> findAll();

    User findUserByEmail(String email);

}

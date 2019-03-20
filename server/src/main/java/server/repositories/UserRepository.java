package server.repositories;

import org.springframework.data.repository.CrudRepository;
import shared.models.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByUsername(String username);

    List<User> findByOrderByFoodPointsDesc();

}

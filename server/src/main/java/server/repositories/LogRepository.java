package server.repositories;

import org.springframework.data.repository.CrudRepository;
import shared.models.Log;
import shared.models.User;

import java.util.List;

public interface LogRepository extends CrudRepository<Log, Long> {
    //
    List<Log> findAll();
    List<Log> findByUser(User user);
}

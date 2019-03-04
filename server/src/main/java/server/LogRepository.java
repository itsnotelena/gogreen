package server;

import models.Log;
import models.User;
import org.springframework.data.repository.CrudRepository;

public interface LogRepository extends CrudRepository<Log, Long> {
    //
}

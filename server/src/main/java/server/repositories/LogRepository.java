package server.repositories;

import org.springframework.data.repository.CrudRepository;
import shared.models.Log;

public interface LogRepository extends CrudRepository<Log, Long> {
    //
}

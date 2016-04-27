package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.Event;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Event entity.
 */
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("select event from Event event where event.doneBy.login = ?#{principal.username}")
    List<Event> findByDoneByIsCurrentUser();

}

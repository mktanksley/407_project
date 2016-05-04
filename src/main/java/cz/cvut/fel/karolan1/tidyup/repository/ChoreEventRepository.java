package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ChoreEvent entity.
 */
public interface ChoreEventRepository extends JpaRepository<ChoreEvent,Long> {

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    List<ChoreEvent> findByDoneByIsCurrentUser();

}

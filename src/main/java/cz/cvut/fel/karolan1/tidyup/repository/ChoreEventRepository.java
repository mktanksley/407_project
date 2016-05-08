package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the ChoreEvent entity.
 */
public interface ChoreEventRepository extends JpaRepository<ChoreEvent,Long> {

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    List<ChoreEvent> findByDoneByIsCurrentUser();

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    Page<ChoreEvent> findByDoneByIsCurrentUser(Pageable pageable);
}

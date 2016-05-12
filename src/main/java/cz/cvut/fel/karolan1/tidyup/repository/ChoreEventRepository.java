package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the ChoreEvent entity.
 */
public interface ChoreEventRepository extends JpaRepository<ChoreEvent, Long> {

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    List<ChoreEvent> findByDoneByIsCurrentUser();

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    Page<ChoreEvent> findByDoneByIsCurrentUser(Pageable pageable);

    @Query("select choreEvent from ChoreEvent choreEvent inner join choreEvent.doneBy user where user.memberOf = ?1")
    Page<ChoreEvent> findEventsFromCurrentUsersFlat(Flat flat, Pageable pageable);


    // try with jpql for pagination:
//    @Query("SELECT e FROM ChoreEvent e WHERE e.doneBy IN (SELECT u FROM User u WHERE u.memberOf IN :#{[0]})")
//    @Query("SELECT e FROM ChoreEvent e INNER JOIN e.doneBy INNER JOIN User.memberOf WHERE User.memberOf = :#{[0]}")
//    Page<ChoreEvent> findEventsFromCurrentUsersFlatFriends(Set<Flat> friends, Pageable pageable);

    @Query(value = "SELECT * FROM chore_event e" +
        " INNER JOIN jhi_user ON e.done_by_id = jhi_user.id" +
        " INNER JOIN flat_friends ON jhi_user.member_of_id = flat_friends.friend_id" +
        " WHERE flat_friends.flat_id = :#{[0].id} ORDER BY e.date_done DESC LIMIT 5", nativeQuery = true)
    List<ChoreEvent> findEventsFromCurrentUsersFlatFriends(Flat flat);
}

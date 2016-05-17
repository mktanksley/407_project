package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import cz.cvut.fel.karolan1.tidyup.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the ChoreEvent entity.
 */
public interface ChoreEventRepository extends JpaRepository<ChoreEvent, Long> {

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    List<ChoreEvent> findByDoneByIsCurrentUser();

    @Query("select choreEvent from ChoreEvent choreEvent where choreEvent.doneBy.login = ?#{principal.username}")
    Page<ChoreEvent> findByDoneByIsCurrentUser(Pageable pageable);

    @Query("select choreEvent from ChoreEvent choreEvent inner join choreEvent.doneBy user where user.memberOf = ?1 and choreEvent.dateDone is not null")
    Page<ChoreEvent> findFinishedEventsFromCurrentUsersFlat(Flat flat, Pageable pageable);

    @Query("select choreEvent from ChoreEvent choreEvent inner join choreEvent.doneBy user where user.memberOf = ?1 and choreEvent.isType.repeatable = ?2")
    Page<ChoreEvent> findEventsFromCurrentUsersFlatByRepeatable(Flat flat, boolean repeatable, Pageable pageable);

    // try with jpql for pagination:
//    @Query("SELECT e FROM ChoreEvent e WHERE e.doneBy IN (SELECT u FROM User u WHERE u.memberOf IN :#{[0]})")
//    @Query("SELECT e FROM ChoreEvent e INNER JOIN e.doneBy INNER JOIN User.memberOf WHERE User.memberOf = :#{[0]}")
//    Page<ChoreEvent> findEventsFromCurrentUsersFlatFriends(Set<Flat> friends, Pageable pageable);

    /**
     * Query to find all events in Friend's flats.
     *
     * @param flat
     * @return
     */
    @Query(value = "SELECT * FROM chore_event e" +
        " INNER JOIN jhi_user ON e.done_by_id = jhi_user.id" +
        " INNER JOIN flat_friends ON jhi_user.member_of_id = flat_friends.friend_id" +
        " WHERE flat_friends.flat_id = :#{[0].id} AND e.date_done IS NOT NULL ORDER BY e.date_done DESC LIMIT 5", nativeQuery = true)
    List<ChoreEvent> findEventsFromCurrentUsersFlatFriends(Flat flat);

    /**
     * Select only chore to-be-done to display on homepage.
     *
     * @param doneBy
     * @return
     */
//    @Query(value = "SELECT e FROM chore_event e WHERE e.date_done IS NULL AND e.date_to IS NOT NULL ORDER BY e.date_to DESC LIMIT 1", nativeQuery = true)
//    @Query("select e from ChoreEvent e where e.doneBy = ?1 and e.dateDone is null and e.dateTo is not null order by e.dateTo desc")
    ChoreEvent findFirstByDoneByAndDateDoneIsNullAndDateToIsNotNullOrderByDateToAsc(User doneBy);

    @Query("select e from ChoreEvent e where e.isType.repeatable = TRUE and e.dateTo >= ?1 and e.dateTo < ?2")
    List<ChoreEvent> findRepeatableToCopy(ZonedDateTime dayBegin, ZonedDateTime dayEnd);
}

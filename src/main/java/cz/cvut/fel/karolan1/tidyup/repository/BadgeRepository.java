package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Badge entity.
 */
@SuppressWarnings("unused")
public interface BadgeRepository extends JpaRepository<Badge,Long> {

    @Query("select badge from Badge badge where badge.ownedBy.login = ?#{principal.username}")
    List<Badge> findByOwnedByIsCurrentUser();

}

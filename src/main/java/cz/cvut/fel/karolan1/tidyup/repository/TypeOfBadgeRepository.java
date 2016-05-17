package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.TypeOfBadge;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the TypeOfBadge entity.
 */
@SuppressWarnings("unused")
public interface TypeOfBadgeRepository extends JpaRepository<TypeOfBadge,Long> {

}

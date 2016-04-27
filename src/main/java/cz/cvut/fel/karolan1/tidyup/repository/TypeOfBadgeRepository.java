package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.TypeOfBadge;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TypeOfBadge entity.
 */
public interface TypeOfBadgeRepository extends JpaRepository<TypeOfBadge,Long> {

}

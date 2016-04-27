package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.TypeOfChore;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TypeOfChore entity.
 */
public interface TypeOfChoreRepository extends JpaRepository<TypeOfChore,Long> {

}

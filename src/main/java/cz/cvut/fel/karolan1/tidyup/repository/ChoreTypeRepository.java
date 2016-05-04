package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreType;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ChoreType entity.
 */
public interface ChoreTypeRepository extends JpaRepository<ChoreType,Long> {

}

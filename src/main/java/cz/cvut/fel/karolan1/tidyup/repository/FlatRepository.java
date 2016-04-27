package cz.cvut.fel.karolan1.tidyup.repository;

import cz.cvut.fel.karolan1.tidyup.domain.Flat;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Flat entity.
 */
public interface FlatRepository extends JpaRepository<Flat,Long> {

    @Query("select distinct flat from Flat flat left join fetch flat.friends")
    List<Flat> findAllWithEagerRelationships();

    @Query("select flat from Flat flat left join fetch flat.friends where flat.id =:id")
    Flat findOneWithEagerRelationships(@Param("id") Long id);

}

package cz.cvut.fel.karolan1.tidyup.service;

import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import cz.cvut.fel.karolan1.tidyup.repository.FlatRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.FlatSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Flat.
 */
@Service
@Transactional
public class FlatService {

    private final Logger log = LoggerFactory.getLogger(FlatService.class);
    
    @Inject
    private FlatRepository flatRepository;
    
    @Inject
    private FlatSearchRepository flatSearchRepository;
    
    /**
     * Save a flat.
     * 
     * @param flat the entity to save
     * @return the persisted entity
     */
    public Flat save(Flat flat) {
        log.debug("Request to save Flat : {}", flat);
        Flat result = flatRepository.save(flat);
        flatSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the flats.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Flat> findAll(Pageable pageable) {
        log.debug("Request to get all Flats");
        Page<Flat> result = flatRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one flat by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Flat findOne(Long id) {
        log.debug("Request to get Flat : {}", id);
        Flat flat = flatRepository.findOneWithEagerRelationships(id);
        return flat;
    }

    /**
     *  Delete the  flat by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Flat : {}", id);
        flatRepository.delete(id);
        flatSearchRepository.delete(id);
    }

    /**
     * Search for the flat corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Flat> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Flats for query {}", query);
        return flatSearchRepository.search(queryStringQuery(query), pageable);
    }
}

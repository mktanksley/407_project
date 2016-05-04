package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.ChoreType;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreTypeRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreTypeSearchRepository;
import cz.cvut.fel.karolan1.tidyup.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ChoreType.
 */
@RestController
@RequestMapping("/api")
public class ChoreTypeResource {

    private final Logger log = LoggerFactory.getLogger(ChoreTypeResource.class);
        
    @Inject
    private ChoreTypeRepository choreTypeRepository;
    
    @Inject
    private ChoreTypeSearchRepository choreTypeSearchRepository;
    
    /**
     * POST  /chore-types : Create a new choreType.
     *
     * @param choreType the choreType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new choreType, or with status 400 (Bad Request) if the choreType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chore-types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChoreType> createChoreType(@Valid @RequestBody ChoreType choreType) throws URISyntaxException {
        log.debug("REST request to save ChoreType : {}", choreType);
        if (choreType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("choreType", "idexists", "A new choreType cannot already have an ID")).body(null);
        }
        ChoreType result = choreTypeRepository.save(choreType);
        choreTypeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/chore-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("choreType", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /chore-types : Updates an existing choreType.
     *
     * @param choreType the choreType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated choreType,
     * or with status 400 (Bad Request) if the choreType is not valid,
     * or with status 500 (Internal Server Error) if the choreType couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chore-types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChoreType> updateChoreType(@Valid @RequestBody ChoreType choreType) throws URISyntaxException {
        log.debug("REST request to update ChoreType : {}", choreType);
        if (choreType.getId() == null) {
            return createChoreType(choreType);
        }
        ChoreType result = choreTypeRepository.save(choreType);
        choreTypeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("choreType", choreType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /chore-types : get all the choreTypes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of choreTypes in body
     */
    @RequestMapping(value = "/chore-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ChoreType> getAllChoreTypes() {
        log.debug("REST request to get all ChoreTypes");
        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        return choreTypes;
    }

    /**
     * GET  /chore-types/:id : get the "id" choreType.
     *
     * @param id the id of the choreType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the choreType, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/chore-types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChoreType> getChoreType(@PathVariable Long id) {
        log.debug("REST request to get ChoreType : {}", id);
        ChoreType choreType = choreTypeRepository.findOne(id);
        return Optional.ofNullable(choreType)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /chore-types/:id : delete the "id" choreType.
     *
     * @param id the id of the choreType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/chore-types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteChoreType(@PathVariable Long id) {
        log.debug("REST request to delete ChoreType : {}", id);
        choreTypeRepository.delete(id);
        choreTypeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("choreType", id.toString())).build();
    }

    /**
     * SEARCH  /_search/chore-types?query=:query : search for the choreType corresponding
     * to the query.
     *
     * @param query the query of the choreType search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/chore-types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ChoreType> searchChoreTypes(@RequestParam String query) {
        log.debug("REST request to search ChoreTypes for query {}", query);
        return StreamSupport
            .stream(choreTypeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

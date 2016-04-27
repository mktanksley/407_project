package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.Chore;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreSearchRepository;
import cz.cvut.fel.karolan1.tidyup.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Chore.
 */
@RestController
@RequestMapping("/api")
public class ChoreResource {

    private final Logger log = LoggerFactory.getLogger(ChoreResource.class);
        
    @Inject
    private ChoreRepository choreRepository;
    
    @Inject
    private ChoreSearchRepository choreSearchRepository;
    
    /**
     * POST  /chores : Create a new chore.
     *
     * @param chore the chore to create
     * @return the ResponseEntity with status 201 (Created) and with body the new chore, or with status 400 (Bad Request) if the chore has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chores",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Chore> createChore(@RequestBody Chore chore) throws URISyntaxException {
        log.debug("REST request to save Chore : {}", chore);
        if (chore.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("chore", "idexists", "A new chore cannot already have an ID")).body(null);
        }
        Chore result = choreRepository.save(chore);
        choreSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/chores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("chore", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /chores : Updates an existing chore.
     *
     * @param chore the chore to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated chore,
     * or with status 400 (Bad Request) if the chore is not valid,
     * or with status 500 (Internal Server Error) if the chore couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chores",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Chore> updateChore(@RequestBody Chore chore) throws URISyntaxException {
        log.debug("REST request to update Chore : {}", chore);
        if (chore.getId() == null) {
            return createChore(chore);
        }
        Chore result = choreRepository.save(chore);
        choreSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("chore", chore.getId().toString()))
            .body(result);
    }

    /**
     * GET  /chores : get all the chores.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of chores in body
     */
    @RequestMapping(value = "/chores",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Chore> getAllChores() {
        log.debug("REST request to get all Chores");
        List<Chore> chores = choreRepository.findAll();
        return chores;
    }

    /**
     * GET  /chores/:id : get the "id" chore.
     *
     * @param id the id of the chore to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the chore, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/chores/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Chore> getChore(@PathVariable Long id) {
        log.debug("REST request to get Chore : {}", id);
        Chore chore = choreRepository.findOne(id);
        return Optional.ofNullable(chore)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /chores/:id : delete the "id" chore.
     *
     * @param id the id of the chore to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/chores/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteChore(@PathVariable Long id) {
        log.debug("REST request to delete Chore : {}", id);
        choreRepository.delete(id);
        choreSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("chore", id.toString())).build();
    }

    /**
     * SEARCH  /_search/chores?query=:query : search for the chore corresponding
     * to the query.
     *
     * @param query the query of the chore search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/chores",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Chore> searchChores(@RequestParam String query) {
        log.debug("REST request to search Chores for query {}", query);
        return StreamSupport
            .stream(choreSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

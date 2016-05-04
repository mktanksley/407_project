package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreEventRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreEventSearchRepository;
import cz.cvut.fel.karolan1.tidyup.web.rest.util.HeaderUtil;
import cz.cvut.fel.karolan1.tidyup.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing ChoreEvent.
 */
@RestController
@RequestMapping("/api")
public class ChoreEventResource {

    private final Logger log = LoggerFactory.getLogger(ChoreEventResource.class);
        
    @Inject
    private ChoreEventRepository choreEventRepository;
    
    @Inject
    private ChoreEventSearchRepository choreEventSearchRepository;
    
    /**
     * POST  /chore-events : Create a new choreEvent.
     *
     * @param choreEvent the choreEvent to create
     * @return the ResponseEntity with status 201 (Created) and with body the new choreEvent, or with status 400 (Bad Request) if the choreEvent has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chore-events",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChoreEvent> createChoreEvent(@RequestBody ChoreEvent choreEvent) throws URISyntaxException {
        log.debug("REST request to save ChoreEvent : {}", choreEvent);
        if (choreEvent.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("choreEvent", "idexists", "A new choreEvent cannot already have an ID")).body(null);
        }
        ChoreEvent result = choreEventRepository.save(choreEvent);
        choreEventSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/chore-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("choreEvent", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /chore-events : Updates an existing choreEvent.
     *
     * @param choreEvent the choreEvent to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated choreEvent,
     * or with status 400 (Bad Request) if the choreEvent is not valid,
     * or with status 500 (Internal Server Error) if the choreEvent couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/chore-events",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChoreEvent> updateChoreEvent(@RequestBody ChoreEvent choreEvent) throws URISyntaxException {
        log.debug("REST request to update ChoreEvent : {}", choreEvent);
        if (choreEvent.getId() == null) {
            return createChoreEvent(choreEvent);
        }
        ChoreEvent result = choreEventRepository.save(choreEvent);
        choreEventSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("choreEvent", choreEvent.getId().toString()))
            .body(result);
    }

    /**
     * GET  /chore-events : get all the choreEvents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of choreEvents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/chore-events",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ChoreEvent>> getAllChoreEvents(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ChoreEvents");
        Page<ChoreEvent> page = choreEventRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/chore-events");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /chore-events/:id : get the "id" choreEvent.
     *
     * @param id the id of the choreEvent to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the choreEvent, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/chore-events/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChoreEvent> getChoreEvent(@PathVariable Long id) {
        log.debug("REST request to get ChoreEvent : {}", id);
        ChoreEvent choreEvent = choreEventRepository.findOne(id);
        return Optional.ofNullable(choreEvent)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /chore-events/:id : delete the "id" choreEvent.
     *
     * @param id the id of the choreEvent to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/chore-events/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteChoreEvent(@PathVariable Long id) {
        log.debug("REST request to delete ChoreEvent : {}", id);
        choreEventRepository.delete(id);
        choreEventSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("choreEvent", id.toString())).build();
    }

    /**
     * SEARCH  /_search/chore-events?query=:query : search for the choreEvent corresponding
     * to the query.
     *
     * @param query the query of the choreEvent search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/chore-events",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ChoreEvent>> searchChoreEvents(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ChoreEvents for query {}", query);
        Page<ChoreEvent> page = choreEventSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/chore-events");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

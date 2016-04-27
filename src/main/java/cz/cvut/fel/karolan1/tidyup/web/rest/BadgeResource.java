package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.Badge;
import cz.cvut.fel.karolan1.tidyup.repository.BadgeRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.BadgeSearchRepository;
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
 * REST controller for managing Badge.
 */
@RestController
@RequestMapping("/api")
public class BadgeResource {

    private final Logger log = LoggerFactory.getLogger(BadgeResource.class);
        
    @Inject
    private BadgeRepository badgeRepository;
    
    @Inject
    private BadgeSearchRepository badgeSearchRepository;
    
    /**
     * POST  /badges : Create a new badge.
     *
     * @param badge the badge to create
     * @return the ResponseEntity with status 201 (Created) and with body the new badge, or with status 400 (Bad Request) if the badge has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/badges",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Badge> createBadge(@RequestBody Badge badge) throws URISyntaxException {
        log.debug("REST request to save Badge : {}", badge);
        if (badge.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("badge", "idexists", "A new badge cannot already have an ID")).body(null);
        }
        Badge result = badgeRepository.save(badge);
        badgeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/badges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("badge", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /badges : Updates an existing badge.
     *
     * @param badge the badge to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated badge,
     * or with status 400 (Bad Request) if the badge is not valid,
     * or with status 500 (Internal Server Error) if the badge couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/badges",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Badge> updateBadge(@RequestBody Badge badge) throws URISyntaxException {
        log.debug("REST request to update Badge : {}", badge);
        if (badge.getId() == null) {
            return createBadge(badge);
        }
        Badge result = badgeRepository.save(badge);
        badgeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("badge", badge.getId().toString()))
            .body(result);
    }

    /**
     * GET  /badges : get all the badges.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of badges in body
     */
    @RequestMapping(value = "/badges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Badge> getAllBadges() {
        log.debug("REST request to get all Badges");
        List<Badge> badges = badgeRepository.findAll();
        return badges;
    }

    /**
     * GET  /badges/:id : get the "id" badge.
     *
     * @param id the id of the badge to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the badge, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/badges/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Badge> getBadge(@PathVariable Long id) {
        log.debug("REST request to get Badge : {}", id);
        Badge badge = badgeRepository.findOne(id);
        return Optional.ofNullable(badge)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /badges/:id : delete the "id" badge.
     *
     * @param id the id of the badge to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/badges/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        log.debug("REST request to delete Badge : {}", id);
        badgeRepository.delete(id);
        badgeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("badge", id.toString())).build();
    }

    /**
     * SEARCH  /_search/badges?query=:query : search for the badge corresponding
     * to the query.
     *
     * @param query the query of the badge search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/badges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Badge> searchBadges(@RequestParam String query) {
        log.debug("REST request to search Badges for query {}", query);
        return StreamSupport
            .stream(badgeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreEventRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreEventSearchRepository;
import cz.cvut.fel.karolan1.tidyup.security.AuthoritiesConstants;
import cz.cvut.fel.karolan1.tidyup.security.SecurityUtils;
import cz.cvut.fel.karolan1.tidyup.service.UserService;
import cz.cvut.fel.karolan1.tidyup.web.rest.util.HeaderUtil;
import cz.cvut.fel.karolan1.tidyup.web.rest.util.PaginationUtil;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

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

    @Inject
    private UserService userService;

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
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<ChoreEvent> createChoreEvent(@RequestBody ChoreEvent choreEvent) throws URISyntaxException {
        log.debug("REST request to save ChoreEvent : {}", choreEvent);
        if (choreEvent.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("choreEvent", "idexists", "A new choreEvent cannot already have an ID")).body(null);
        }

        // if not system or flat admin, user can create event only for himself.
        // if flat admin, user can create events for flat members.
        if (!SecurityUtils.isCurrentUserAdmin()) {
            // is current user admin of the flat and is creating event for a flat member?
            if (!SecurityUtils.isCurrentUserAdminOfFlat(choreEvent.getDoneBy().getMemberOf())) {
                // user can create events for himself:
                if (!choreEvent.getDoneBy().getLogin().equals(SecurityUtils.getCurrentUserLogin())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createFailureAlert("error", "error", "User can modify only his own data!")).body(null);
                }
            }
        }

        ChoreEvent result = choreEventRepository.save(choreEvent);
        choreEventSearchRepository.save(result);

        // add alert only for admin or flat-admin
        if (SecurityUtils.isCurrentUserAdmin() || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.FLAT_ADMIN)) {
            return ResponseEntity.created(new URI("/api/chore-events/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("choreEvent", result.getId().toString()))
                .body(result);
        } else {
            return ResponseEntity.created(new URI("/api/chore-events/" + result.getId()))
                .body(result);
        }
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
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<ChoreEvent> updateChoreEvent(@RequestBody ChoreEvent choreEvent) throws URISyntaxException {
        log.debug("REST request to update ChoreEvent : {}", choreEvent);

        if (!SecurityUtils.isCurrentUserAdmin() && !SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.FLAT_ADMIN) && choreEvent.getDoneBy().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("choreEvent", "error", "non-admin can edit only his own chores.")).body(null);
        }

        if (choreEvent.getId() == null) {
            return createChoreEvent(choreEvent);
        }
        ChoreEvent result = choreEventRepository.save(choreEvent);
        choreEventSearchRepository.save(result);

        return SecurityUtils.isCurrentUserAdmin() ?
            ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("choreEvent", choreEvent.getId().toString()))
                .body(result) :
            ResponseEntity.ok()
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
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<ChoreEvent>> getAllChoreEvents(Pageable pageable, Boolean repeatable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ChoreEvents");


        // if not admin, return events from user's flat
        if (!SecurityUtils.isCurrentUserAdmin()) {
            Page<ChoreEvent> page = repeatable == null ?
                choreEventRepository.findFinishedEventsFromCurrentUsersFlat(userService.getUserWithAuthorities().getMemberOf(), pageable) :
                choreEventRepository.findEventsFromCurrentUsersFlatByRepeatable(userService.getUserWithAuthorities().getMemberOf(), repeatable, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/chore-events");
            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        }
        Page<ChoreEvent> page = choreEventRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/chore-events");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /chore-events : get all the choreEvents.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of choreEvents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/friends-chore-events",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    @Secured(AuthoritiesConstants.USER)
    public List<ChoreEvent> getFriendsChoreEvents()
        throws URISyntaxException {
        log.debug("REST request to get all friend's ChoreEvents");

        Flat flat = userService.getUserWithAuthorities().getMemberOf();
        if (flat.getFriends() != null && flat.getFriends().size() > 0) {
            Hibernate.initialize(flat.getFriends());
            return choreEventRepository.findEventsFromCurrentUsersFlatFriends(flat);
        }
        return new ArrayList<>(0);
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
    @Secured(AuthoritiesConstants.USER)
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
     * GET  /to-do : get the choreEvent to be done by current user.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the choreEvent, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/to-do",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<ChoreEvent> getChoreEventToDo() {
        log.debug("REST request to get ChoreEvent TODO");
        ChoreEvent choreEvent = choreEventRepository.findFirstByDoneByAndDateDoneIsNullAndDateToIsNotNullOrderByDateToAsc(userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin()).get());
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
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.FLAT_ADMIN})
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

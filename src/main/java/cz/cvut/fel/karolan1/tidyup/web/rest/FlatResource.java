package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import cz.cvut.fel.karolan1.tidyup.domain.User;
import cz.cvut.fel.karolan1.tidyup.repository.FlatRepository;
import cz.cvut.fel.karolan1.tidyup.repository.UserRepository;
import cz.cvut.fel.karolan1.tidyup.security.AuthoritiesConstants;
import cz.cvut.fel.karolan1.tidyup.security.SecurityUtils;
import cz.cvut.fel.karolan1.tidyup.service.FlatService;
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
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Flat.
 */
@RestController
@RequestMapping("/api")
public class FlatResource {

    private final Logger log = LoggerFactory.getLogger(FlatResource.class);

    @Inject
    private FlatService flatService;

    @Inject
    private FlatRepository flatRepository;

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /flats : Create a new flat.
     *
     * @param flat the flat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new flat, or with status 400 (Bad Request) if the flat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/flats",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Flat> createFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to save Flat : {}", flat);
        if (flat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("flat", "idexists", "A new flat cannot already have an ID")).body(null);
        }

        // check if the name already exists
        if (flatRepository.findOneByName(flat.getName()) != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("flat", "nameexist", "This name (" + flat.getName() + ") is already used!")).body(null);
        }

        // set current time
        flat.setDateCreated(ZonedDateTime.now());

        // if the create request is made by common user, assign the flat to him
        if (!SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            User user = userService.getUserWithAuthorities();
            flat.setHasAdmin(user);
            flat.setResidents(Collections.singleton(user));
            Flat result = flatService.save(flat);

            // update the user as well
            user.setMemberOf(result);
            userRepository.save(user);

            return ResponseEntity.created(new URI("/api/flats/" + result.getId()))
                .headers(HeaderUtil.createAlert("tidyUpApp.flat.registered", result.getName()))
                .body(result);
        }

        Flat result = flatService.save(flat);
        return ResponseEntity.created(new URI("/api/flats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("flat", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /flats : Updates an existing flat.
     *
     * @param flat the flat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated flat,
     * or with status 400 (Bad Request) if the flat is not valid,
     * or with status 500 (Internal Server Error) if the flat couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/flats",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Flat> updateFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to update Flat : {}", flat);
        if (flat.getId() == null) {
            return createFlat(flat);
        }

        // check that date is not being updated
        Flat fromDb = flatService.findOne(flat.getId());
        if (!flat.getDateCreated().isEqual(fromDb.getDateCreated())) {
            log.warn("Attempt to update creation date on Flat entity: [{}], old date: {}, new date: {}.", flat, flat.getDateCreated(), fromDb.getDateCreated());
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("flat", "dateexist", "Creation date cannot be modified!")).body(null);
        }

        Flat result = flatService.save(flat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("flat", flat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /flats : get all the flats.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of flats in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/flats",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Flat>> getAllFlats(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Flats");
        Page<Flat> page = flatService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/flats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /flats/:id : get the "id" flat.
     *
     * @param id the id of the flat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the flat, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/flats/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    @Transactional
    public ResponseEntity<Flat> getFlat(@PathVariable Long id) {
        log.debug("REST request to get Flat : {}", id);

        Flat flat = flatService.findOne(id);
        Hibernate.initialize(flat.getResidents()); // eagerly load residents
        Hibernate.initialize(flat.getFriends()); // eagerly load friends

        // if the user is not admin and not member of the flat, he cannot get this!
        if (!SecurityUtils.isCurrentUserAdmin() && !userService.getUserWithAuthorities().getMemberOf().equals(flat)) {
            log.warn("User tried to view foreign flat!", userService.getUserWithAuthorities());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(HeaderUtil.createFailureAlert("error", "error", "User can see only his account's data!")).body(null);
        }
        return Optional.ofNullable(flat)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /flats/:id : delete the "id" flat.
     *
     * @param id the id of the flat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/flats/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        log.debug("REST request to delete Flat : {}", id);
        flatService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("flat", id.toString())).build();
    }

    /**
     * SEARCH  /_search/flats?query=:query : search for the flat corresponding
     * to the query.
     *
     * @param query the query of the flat search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/flats",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Flat>> searchFlats(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Flats for query {}", query);
        Page<Flat> page = flatService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/flats");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

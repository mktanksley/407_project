package cz.cvut.fel.karolan1.tidyup.web.rest;

import com.codahale.metrics.annotation.Timed;
import cz.cvut.fel.karolan1.tidyup.domain.TypeOfBadge;
import cz.cvut.fel.karolan1.tidyup.repository.TypeOfBadgeRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.TypeOfBadgeSearchRepository;
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
 * REST controller for managing TypeOfBadge.
 */
@RestController
@RequestMapping("/api")
public class TypeOfBadgeResource {

    private final Logger log = LoggerFactory.getLogger(TypeOfBadgeResource.class);
        
    @Inject
    private TypeOfBadgeRepository typeOfBadgeRepository;
    
    @Inject
    private TypeOfBadgeSearchRepository typeOfBadgeSearchRepository;
    
    /**
     * POST  /type-of-badges : Create a new typeOfBadge.
     *
     * @param typeOfBadge the typeOfBadge to create
     * @return the ResponseEntity with status 201 (Created) and with body the new typeOfBadge, or with status 400 (Bad Request) if the typeOfBadge has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/type-of-badges",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeOfBadge> createTypeOfBadge(@Valid @RequestBody TypeOfBadge typeOfBadge) throws URISyntaxException {
        log.debug("REST request to save TypeOfBadge : {}", typeOfBadge);
        if (typeOfBadge.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("typeOfBadge", "idexists", "A new typeOfBadge cannot already have an ID")).body(null);
        }
        TypeOfBadge result = typeOfBadgeRepository.save(typeOfBadge);
        typeOfBadgeSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/type-of-badges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("typeOfBadge", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /type-of-badges : Updates an existing typeOfBadge.
     *
     * @param typeOfBadge the typeOfBadge to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated typeOfBadge,
     * or with status 400 (Bad Request) if the typeOfBadge is not valid,
     * or with status 500 (Internal Server Error) if the typeOfBadge couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/type-of-badges",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeOfBadge> updateTypeOfBadge(@Valid @RequestBody TypeOfBadge typeOfBadge) throws URISyntaxException {
        log.debug("REST request to update TypeOfBadge : {}", typeOfBadge);
        if (typeOfBadge.getId() == null) {
            return createTypeOfBadge(typeOfBadge);
        }
        TypeOfBadge result = typeOfBadgeRepository.save(typeOfBadge);
        typeOfBadgeSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("typeOfBadge", typeOfBadge.getId().toString()))
            .body(result);
    }

    /**
     * GET  /type-of-badges : get all the typeOfBadges.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of typeOfBadges in body
     */
    @RequestMapping(value = "/type-of-badges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TypeOfBadge> getAllTypeOfBadges() {
        log.debug("REST request to get all TypeOfBadges");
        List<TypeOfBadge> typeOfBadges = typeOfBadgeRepository.findAll();
        return typeOfBadges;
    }

    /**
     * GET  /type-of-badges/:id : get the "id" typeOfBadge.
     *
     * @param id the id of the typeOfBadge to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the typeOfBadge, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/type-of-badges/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeOfBadge> getTypeOfBadge(@PathVariable Long id) {
        log.debug("REST request to get TypeOfBadge : {}", id);
        TypeOfBadge typeOfBadge = typeOfBadgeRepository.findOne(id);
        return Optional.ofNullable(typeOfBadge)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /type-of-badges/:id : delete the "id" typeOfBadge.
     *
     * @param id the id of the typeOfBadge to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/type-of-badges/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTypeOfBadge(@PathVariable Long id) {
        log.debug("REST request to delete TypeOfBadge : {}", id);
        typeOfBadgeRepository.delete(id);
        typeOfBadgeSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("typeOfBadge", id.toString())).build();
    }

    /**
     * SEARCH  /_search/type-of-badges?query=:query : search for the typeOfBadge corresponding
     * to the query.
     *
     * @param query the query of the typeOfBadge search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/type-of-badges",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TypeOfBadge> searchTypeOfBadges(@RequestParam String query) {
        log.debug("REST request to search TypeOfBadges for query {}", query);
        return StreamSupport
            .stream(typeOfBadgeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

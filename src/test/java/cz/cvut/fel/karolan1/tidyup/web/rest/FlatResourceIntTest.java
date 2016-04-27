package cz.cvut.fel.karolan1.tidyup.web.rest;

import cz.cvut.fel.karolan1.tidyup.TidyUpApp;
import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import cz.cvut.fel.karolan1.tidyup.repository.FlatRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.FlatSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FlatResource REST controller.
 *
 * @see FlatResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TidyUpApp.class)
@WebAppConfiguration
@IntegrationTest
public class FlatResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final ZonedDateTime DEFAULT_DATE_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_CREATED_STR = dateTimeFormatter.format(DEFAULT_DATE_CREATED);

    @Inject
    private FlatRepository flatRepository;

    @Inject
    private FlatSearchRepository flatSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFlatMockMvc;

    private Flat flat;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FlatResource flatResource = new FlatResource();
        ReflectionTestUtils.setField(flatResource, "flatSearchRepository", flatSearchRepository);
        ReflectionTestUtils.setField(flatResource, "flatRepository", flatRepository);
        this.restFlatMockMvc = MockMvcBuilders.standaloneSetup(flatResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        flatSearchRepository.deleteAll();
        flat = new Flat();
        flat.setName(DEFAULT_NAME);
        flat.setDateCreated(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    public void createFlat() throws Exception {
        int databaseSizeBeforeCreate = flatRepository.findAll().size();

        // Create the Flat

        restFlatMockMvc.perform(post("/api/flats")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(flat)))
                .andExpect(status().isCreated());

        // Validate the Flat in the database
        List<Flat> flats = flatRepository.findAll();
        assertThat(flats).hasSize(databaseSizeBeforeCreate + 1);
        Flat testFlat = flats.get(flats.size() - 1);
        assertThat(testFlat.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFlat.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);

        // Validate the Flat in ElasticSearch
        Flat flatEs = flatSearchRepository.findOne(testFlat.getId());
        assertThat(flatEs).isEqualToComparingFieldByField(testFlat);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = flatRepository.findAll().size();
        // set the field null
        flat.setName(null);

        // Create the Flat, which fails.

        restFlatMockMvc.perform(post("/api/flats")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(flat)))
                .andExpect(status().isBadRequest());

        List<Flat> flats = flatRepository.findAll();
        assertThat(flats).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFlats() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);

        // Get all the flats
        restFlatMockMvc.perform(get("/api/flats?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(flat.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED_STR)));
    }

    @Test
    @Transactional
    public void getFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);

        // Get the flat
        restFlatMockMvc.perform(get("/api/flats/{id}", flat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(flat.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED_STR));
    }

    @Test
    @Transactional
    public void getNonExistingFlat() throws Exception {
        // Get the flat
        restFlatMockMvc.perform(get("/api/flats/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);
        flatSearchRepository.save(flat);
        int databaseSizeBeforeUpdate = flatRepository.findAll().size();

        // Update the flat
        Flat updatedFlat = new Flat();
        updatedFlat.setId(flat.getId());
        updatedFlat.setName(UPDATED_NAME);
        updatedFlat.setDateCreated(UPDATED_DATE_CREATED);

        restFlatMockMvc.perform(put("/api/flats")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFlat)))
                .andExpect(status().isOk());

        // Validate the Flat in the database
        List<Flat> flats = flatRepository.findAll();
        assertThat(flats).hasSize(databaseSizeBeforeUpdate);
        Flat testFlat = flats.get(flats.size() - 1);
        assertThat(testFlat.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFlat.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);

        // Validate the Flat in ElasticSearch
        Flat flatEs = flatSearchRepository.findOne(testFlat.getId());
        assertThat(flatEs).isEqualToComparingFieldByField(testFlat);
    }

    @Test
    @Transactional
    public void deleteFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);
        flatSearchRepository.save(flat);
        int databaseSizeBeforeDelete = flatRepository.findAll().size();

        // Get the flat
        restFlatMockMvc.perform(delete("/api/flats/{id}", flat.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean flatExistsInEs = flatSearchRepository.exists(flat.getId());
        assertThat(flatExistsInEs).isFalse();

        // Validate the database is empty
        List<Flat> flats = flatRepository.findAll();
        assertThat(flats).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);
        flatSearchRepository.save(flat);

        // Search the flat
        restFlatMockMvc.perform(get("/api/_search/flats?query=id:" + flat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flat.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED_STR)));
    }
}

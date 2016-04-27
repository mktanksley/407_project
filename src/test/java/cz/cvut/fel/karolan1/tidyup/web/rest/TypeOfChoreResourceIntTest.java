package cz.cvut.fel.karolan1.tidyup.web.rest;

import cz.cvut.fel.karolan1.tidyup.TidyUpApp;
import cz.cvut.fel.karolan1.tidyup.domain.TypeOfChore;
import cz.cvut.fel.karolan1.tidyup.repository.TypeOfChoreRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.TypeOfChoreSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TypeOfChoreResource REST controller.
 *
 * @see TypeOfChoreResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TidyUpApp.class)
@WebAppConfiguration
@IntegrationTest
public class TypeOfChoreResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final Boolean DEFAULT_REPEATABLE = false;
    private static final Boolean UPDATED_REPEATABLE = true;

    private static final Integer DEFAULT_INTERVAL = 1;
    private static final Integer UPDATED_INTERVAL = 2;

    private static final Integer DEFAULT_POINTS = 1;
    private static final Integer UPDATED_POINTS = 2;

    @Inject
    private TypeOfChoreRepository typeOfChoreRepository;

    @Inject
    private TypeOfChoreSearchRepository typeOfChoreSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTypeOfChoreMockMvc;

    private TypeOfChore typeOfChore;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeOfChoreResource typeOfChoreResource = new TypeOfChoreResource();
        ReflectionTestUtils.setField(typeOfChoreResource, "typeOfChoreSearchRepository", typeOfChoreSearchRepository);
        ReflectionTestUtils.setField(typeOfChoreResource, "typeOfChoreRepository", typeOfChoreRepository);
        this.restTypeOfChoreMockMvc = MockMvcBuilders.standaloneSetup(typeOfChoreResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        typeOfChoreSearchRepository.deleteAll();
        typeOfChore = new TypeOfChore();
        typeOfChore.setName(DEFAULT_NAME);
        typeOfChore.setDescription(DEFAULT_DESCRIPTION);
        typeOfChore.setRepeatable(DEFAULT_REPEATABLE);
        typeOfChore.setInterval(DEFAULT_INTERVAL);
        typeOfChore.setPoints(DEFAULT_POINTS);
    }

    @Test
    @Transactional
    public void createTypeOfChore() throws Exception {
        int databaseSizeBeforeCreate = typeOfChoreRepository.findAll().size();

        // Create the TypeOfChore

        restTypeOfChoreMockMvc.perform(post("/api/type-of-chores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeOfChore)))
                .andExpect(status().isCreated());

        // Validate the TypeOfChore in the database
        List<TypeOfChore> typeOfChores = typeOfChoreRepository.findAll();
        assertThat(typeOfChores).hasSize(databaseSizeBeforeCreate + 1);
        TypeOfChore testTypeOfChore = typeOfChores.get(typeOfChores.size() - 1);
        assertThat(testTypeOfChore.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypeOfChore.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTypeOfChore.isRepeatable()).isEqualTo(DEFAULT_REPEATABLE);
        assertThat(testTypeOfChore.getInterval()).isEqualTo(DEFAULT_INTERVAL);
        assertThat(testTypeOfChore.getPoints()).isEqualTo(DEFAULT_POINTS);

        // Validate the TypeOfChore in ElasticSearch
        TypeOfChore typeOfChoreEs = typeOfChoreSearchRepository.findOne(testTypeOfChore.getId());
        assertThat(typeOfChoreEs).isEqualToComparingFieldByField(testTypeOfChore);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeOfChoreRepository.findAll().size();
        // set the field null
        typeOfChore.setName(null);

        // Create the TypeOfChore, which fails.

        restTypeOfChoreMockMvc.perform(post("/api/type-of-chores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeOfChore)))
                .andExpect(status().isBadRequest());

        List<TypeOfChore> typeOfChores = typeOfChoreRepository.findAll();
        assertThat(typeOfChores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRepeatableIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeOfChoreRepository.findAll().size();
        // set the field null
        typeOfChore.setRepeatable(null);

        // Create the TypeOfChore, which fails.

        restTypeOfChoreMockMvc.perform(post("/api/type-of-chores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeOfChore)))
                .andExpect(status().isBadRequest());

        List<TypeOfChore> typeOfChores = typeOfChoreRepository.findAll();
        assertThat(typeOfChores).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTypeOfChores() throws Exception {
        // Initialize the database
        typeOfChoreRepository.saveAndFlush(typeOfChore);

        // Get all the typeOfChores
        restTypeOfChoreMockMvc.perform(get("/api/type-of-chores?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(typeOfChore.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].repeatable").value(hasItem(DEFAULT_REPEATABLE.booleanValue())))
                .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
                .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }

    @Test
    @Transactional
    public void getTypeOfChore() throws Exception {
        // Initialize the database
        typeOfChoreRepository.saveAndFlush(typeOfChore);

        // Get the typeOfChore
        restTypeOfChoreMockMvc.perform(get("/api/type-of-chores/{id}", typeOfChore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(typeOfChore.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.repeatable").value(DEFAULT_REPEATABLE.booleanValue()))
            .andExpect(jsonPath("$.interval").value(DEFAULT_INTERVAL))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS));
    }

    @Test
    @Transactional
    public void getNonExistingTypeOfChore() throws Exception {
        // Get the typeOfChore
        restTypeOfChoreMockMvc.perform(get("/api/type-of-chores/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTypeOfChore() throws Exception {
        // Initialize the database
        typeOfChoreRepository.saveAndFlush(typeOfChore);
        typeOfChoreSearchRepository.save(typeOfChore);
        int databaseSizeBeforeUpdate = typeOfChoreRepository.findAll().size();

        // Update the typeOfChore
        TypeOfChore updatedTypeOfChore = new TypeOfChore();
        updatedTypeOfChore.setId(typeOfChore.getId());
        updatedTypeOfChore.setName(UPDATED_NAME);
        updatedTypeOfChore.setDescription(UPDATED_DESCRIPTION);
        updatedTypeOfChore.setRepeatable(UPDATED_REPEATABLE);
        updatedTypeOfChore.setInterval(UPDATED_INTERVAL);
        updatedTypeOfChore.setPoints(UPDATED_POINTS);

        restTypeOfChoreMockMvc.perform(put("/api/type-of-chores")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTypeOfChore)))
                .andExpect(status().isOk());

        // Validate the TypeOfChore in the database
        List<TypeOfChore> typeOfChores = typeOfChoreRepository.findAll();
        assertThat(typeOfChores).hasSize(databaseSizeBeforeUpdate);
        TypeOfChore testTypeOfChore = typeOfChores.get(typeOfChores.size() - 1);
        assertThat(testTypeOfChore.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypeOfChore.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTypeOfChore.isRepeatable()).isEqualTo(UPDATED_REPEATABLE);
        assertThat(testTypeOfChore.getInterval()).isEqualTo(UPDATED_INTERVAL);
        assertThat(testTypeOfChore.getPoints()).isEqualTo(UPDATED_POINTS);

        // Validate the TypeOfChore in ElasticSearch
        TypeOfChore typeOfChoreEs = typeOfChoreSearchRepository.findOne(testTypeOfChore.getId());
        assertThat(typeOfChoreEs).isEqualToComparingFieldByField(testTypeOfChore);
    }

    @Test
    @Transactional
    public void deleteTypeOfChore() throws Exception {
        // Initialize the database
        typeOfChoreRepository.saveAndFlush(typeOfChore);
        typeOfChoreSearchRepository.save(typeOfChore);
        int databaseSizeBeforeDelete = typeOfChoreRepository.findAll().size();

        // Get the typeOfChore
        restTypeOfChoreMockMvc.perform(delete("/api/type-of-chores/{id}", typeOfChore.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean typeOfChoreExistsInEs = typeOfChoreSearchRepository.exists(typeOfChore.getId());
        assertThat(typeOfChoreExistsInEs).isFalse();

        // Validate the database is empty
        List<TypeOfChore> typeOfChores = typeOfChoreRepository.findAll();
        assertThat(typeOfChores).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTypeOfChore() throws Exception {
        // Initialize the database
        typeOfChoreRepository.saveAndFlush(typeOfChore);
        typeOfChoreSearchRepository.save(typeOfChore);

        // Search the typeOfChore
        restTypeOfChoreMockMvc.perform(get("/api/_search/type-of-chores?query=id:" + typeOfChore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeOfChore.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].repeatable").value(hasItem(DEFAULT_REPEATABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }
}

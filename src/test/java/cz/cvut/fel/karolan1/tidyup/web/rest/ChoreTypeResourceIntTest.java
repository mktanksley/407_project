package cz.cvut.fel.karolan1.tidyup.web.rest;

import cz.cvut.fel.karolan1.tidyup.TidyUpApp;
import cz.cvut.fel.karolan1.tidyup.domain.ChoreType;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreTypeRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreTypeSearchRepository;

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
 * Test class for the ChoreTypeResource REST controller.
 *
 * @see ChoreTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TidyUpApp.class)
@WebAppConfiguration
@IntegrationTest
public class ChoreTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final Boolean DEFAULT_REPEATABLE = false;
    private static final Boolean UPDATED_REPEATABLE = true;

    private static final Integer DEFAULT_INTERVAL = 0;
    private static final Integer UPDATED_INTERVAL = 1;

    private static final Integer DEFAULT_POINTS = 0;
    private static final Integer UPDATED_POINTS = 1;

    @Inject
    private ChoreTypeRepository choreTypeRepository;

    @Inject
    private ChoreTypeSearchRepository choreTypeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restChoreTypeMockMvc;

    private ChoreType choreType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ChoreTypeResource choreTypeResource = new ChoreTypeResource();
        ReflectionTestUtils.setField(choreTypeResource, "choreTypeSearchRepository", choreTypeSearchRepository);
        ReflectionTestUtils.setField(choreTypeResource, "choreTypeRepository", choreTypeRepository);
        this.restChoreTypeMockMvc = MockMvcBuilders.standaloneSetup(choreTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        choreTypeSearchRepository.deleteAll();
        choreType = new ChoreType();
        choreType.setName(DEFAULT_NAME);
        choreType.setDescription(DEFAULT_DESCRIPTION);
        choreType.setRepeatable(DEFAULT_REPEATABLE);
        choreType.setInterval(DEFAULT_INTERVAL);
        choreType.setPoints(DEFAULT_POINTS);
    }

    @Test
    @Transactional
    public void createChoreType() throws Exception {
        int databaseSizeBeforeCreate = choreTypeRepository.findAll().size();

        // Create the ChoreType

        restChoreTypeMockMvc.perform(post("/api/chore-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(choreType)))
                .andExpect(status().isCreated());

        // Validate the ChoreType in the database
        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        assertThat(choreTypes).hasSize(databaseSizeBeforeCreate + 1);
        ChoreType testChoreType = choreTypes.get(choreTypes.size() - 1);
        assertThat(testChoreType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testChoreType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testChoreType.isRepeatable()).isEqualTo(DEFAULT_REPEATABLE);
        assertThat(testChoreType.getInterval()).isEqualTo(DEFAULT_INTERVAL);
        assertThat(testChoreType.getPoints()).isEqualTo(DEFAULT_POINTS);

        // Validate the ChoreType in ElasticSearch
        ChoreType choreTypeEs = choreTypeSearchRepository.findOne(testChoreType.getId());
        assertThat(choreTypeEs).isEqualToComparingFieldByField(testChoreType);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = choreTypeRepository.findAll().size();
        // set the field null
        choreType.setName(null);

        // Create the ChoreType, which fails.

        restChoreTypeMockMvc.perform(post("/api/chore-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(choreType)))
                .andExpect(status().isBadRequest());

        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        assertThat(choreTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRepeatableIsRequired() throws Exception {
        int databaseSizeBeforeTest = choreTypeRepository.findAll().size();
        // set the field null
        choreType.setRepeatable(null);

        // Create the ChoreType, which fails.

        restChoreTypeMockMvc.perform(post("/api/chore-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(choreType)))
                .andExpect(status().isBadRequest());

        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        assertThat(choreTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPointsIsRequired() throws Exception {
        int databaseSizeBeforeTest = choreTypeRepository.findAll().size();
        // set the field null
        choreType.setPoints(null);

        // Create the ChoreType, which fails.

        restChoreTypeMockMvc.perform(post("/api/chore-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(choreType)))
                .andExpect(status().isBadRequest());

        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        assertThat(choreTypes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllChoreTypes() throws Exception {
        // Initialize the database
        choreTypeRepository.saveAndFlush(choreType);

        // Get all the choreTypes
        restChoreTypeMockMvc.perform(get("/api/chore-types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(choreType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].repeatable").value(hasItem(DEFAULT_REPEATABLE.booleanValue())))
                .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
                .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }

    @Test
    @Transactional
    public void getChoreType() throws Exception {
        // Initialize the database
        choreTypeRepository.saveAndFlush(choreType);

        // Get the choreType
        restChoreTypeMockMvc.perform(get("/api/chore-types/{id}", choreType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(choreType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.repeatable").value(DEFAULT_REPEATABLE.booleanValue()))
            .andExpect(jsonPath("$.interval").value(DEFAULT_INTERVAL))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS));
    }

    @Test
    @Transactional
    public void getNonExistingChoreType() throws Exception {
        // Get the choreType
        restChoreTypeMockMvc.perform(get("/api/chore-types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChoreType() throws Exception {
        // Initialize the database
        choreTypeRepository.saveAndFlush(choreType);
        choreTypeSearchRepository.save(choreType);
        int databaseSizeBeforeUpdate = choreTypeRepository.findAll().size();

        // Update the choreType
        ChoreType updatedChoreType = new ChoreType();
        updatedChoreType.setId(choreType.getId());
        updatedChoreType.setName(UPDATED_NAME);
        updatedChoreType.setDescription(UPDATED_DESCRIPTION);
        updatedChoreType.setRepeatable(UPDATED_REPEATABLE);
        updatedChoreType.setInterval(UPDATED_INTERVAL);
        updatedChoreType.setPoints(UPDATED_POINTS);

        restChoreTypeMockMvc.perform(put("/api/chore-types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedChoreType)))
                .andExpect(status().isOk());

        // Validate the ChoreType in the database
        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        assertThat(choreTypes).hasSize(databaseSizeBeforeUpdate);
        ChoreType testChoreType = choreTypes.get(choreTypes.size() - 1);
        assertThat(testChoreType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testChoreType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testChoreType.isRepeatable()).isEqualTo(UPDATED_REPEATABLE);
        assertThat(testChoreType.getInterval()).isEqualTo(UPDATED_INTERVAL);
        assertThat(testChoreType.getPoints()).isEqualTo(UPDATED_POINTS);

        // Validate the ChoreType in ElasticSearch
        ChoreType choreTypeEs = choreTypeSearchRepository.findOne(testChoreType.getId());
        assertThat(choreTypeEs).isEqualToComparingFieldByField(testChoreType);
    }

    @Test
    @Transactional
    public void deleteChoreType() throws Exception {
        // Initialize the database
        choreTypeRepository.saveAndFlush(choreType);
        choreTypeSearchRepository.save(choreType);
        int databaseSizeBeforeDelete = choreTypeRepository.findAll().size();

        // Get the choreType
        restChoreTypeMockMvc.perform(delete("/api/chore-types/{id}", choreType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean choreTypeExistsInEs = choreTypeSearchRepository.exists(choreType.getId());
        assertThat(choreTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<ChoreType> choreTypes = choreTypeRepository.findAll();
        assertThat(choreTypes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchChoreType() throws Exception {
        // Initialize the database
        choreTypeRepository.saveAndFlush(choreType);
        choreTypeSearchRepository.save(choreType);

        // Search the choreType
        restChoreTypeMockMvc.perform(get("/api/_search/chore-types?query=id:" + choreType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(choreType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].repeatable").value(hasItem(DEFAULT_REPEATABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].interval").value(hasItem(DEFAULT_INTERVAL)))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS)));
    }
}

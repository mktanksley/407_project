package cz.cvut.fel.karolan1.tidyup.web.rest;

import cz.cvut.fel.karolan1.tidyup.TidyUpApp;
import cz.cvut.fel.karolan1.tidyup.domain.TypeOfBadge;
import cz.cvut.fel.karolan1.tidyup.repository.TypeOfBadgeRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.TypeOfBadgeSearchRepository;

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
 * Test class for the TypeOfBadgeResource REST controller.
 *
 * @see TypeOfBadgeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TidyUpApp.class)
@WebAppConfiguration
@IntegrationTest
public class TypeOfBadgeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private TypeOfBadgeRepository typeOfBadgeRepository;

    @Inject
    private TypeOfBadgeSearchRepository typeOfBadgeSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTypeOfBadgeMockMvc;

    private TypeOfBadge typeOfBadge;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeOfBadgeResource typeOfBadgeResource = new TypeOfBadgeResource();
        ReflectionTestUtils.setField(typeOfBadgeResource, "typeOfBadgeSearchRepository", typeOfBadgeSearchRepository);
        ReflectionTestUtils.setField(typeOfBadgeResource, "typeOfBadgeRepository", typeOfBadgeRepository);
        this.restTypeOfBadgeMockMvc = MockMvcBuilders.standaloneSetup(typeOfBadgeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        typeOfBadgeSearchRepository.deleteAll();
        typeOfBadge = new TypeOfBadge();
        typeOfBadge.setName(DEFAULT_NAME);
        typeOfBadge.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createTypeOfBadge() throws Exception {
        int databaseSizeBeforeCreate = typeOfBadgeRepository.findAll().size();

        // Create the TypeOfBadge

        restTypeOfBadgeMockMvc.perform(post("/api/type-of-badges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeOfBadge)))
                .andExpect(status().isCreated());

        // Validate the TypeOfBadge in the database
        List<TypeOfBadge> typeOfBadges = typeOfBadgeRepository.findAll();
        assertThat(typeOfBadges).hasSize(databaseSizeBeforeCreate + 1);
        TypeOfBadge testTypeOfBadge = typeOfBadges.get(typeOfBadges.size() - 1);
        assertThat(testTypeOfBadge.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTypeOfBadge.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the TypeOfBadge in ElasticSearch
        TypeOfBadge typeOfBadgeEs = typeOfBadgeSearchRepository.findOne(testTypeOfBadge.getId());
        assertThat(typeOfBadgeEs).isEqualToComparingFieldByField(testTypeOfBadge);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeOfBadgeRepository.findAll().size();
        // set the field null
        typeOfBadge.setName(null);

        // Create the TypeOfBadge, which fails.

        restTypeOfBadgeMockMvc.perform(post("/api/type-of-badges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(typeOfBadge)))
                .andExpect(status().isBadRequest());

        List<TypeOfBadge> typeOfBadges = typeOfBadgeRepository.findAll();
        assertThat(typeOfBadges).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTypeOfBadges() throws Exception {
        // Initialize the database
        typeOfBadgeRepository.saveAndFlush(typeOfBadge);

        // Get all the typeOfBadges
        restTypeOfBadgeMockMvc.perform(get("/api/type-of-badges?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(typeOfBadge.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getTypeOfBadge() throws Exception {
        // Initialize the database
        typeOfBadgeRepository.saveAndFlush(typeOfBadge);

        // Get the typeOfBadge
        restTypeOfBadgeMockMvc.perform(get("/api/type-of-badges/{id}", typeOfBadge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(typeOfBadge.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTypeOfBadge() throws Exception {
        // Get the typeOfBadge
        restTypeOfBadgeMockMvc.perform(get("/api/type-of-badges/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTypeOfBadge() throws Exception {
        // Initialize the database
        typeOfBadgeRepository.saveAndFlush(typeOfBadge);
        typeOfBadgeSearchRepository.save(typeOfBadge);
        int databaseSizeBeforeUpdate = typeOfBadgeRepository.findAll().size();

        // Update the typeOfBadge
        TypeOfBadge updatedTypeOfBadge = new TypeOfBadge();
        updatedTypeOfBadge.setId(typeOfBadge.getId());
        updatedTypeOfBadge.setName(UPDATED_NAME);
        updatedTypeOfBadge.setDescription(UPDATED_DESCRIPTION);

        restTypeOfBadgeMockMvc.perform(put("/api/type-of-badges")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTypeOfBadge)))
                .andExpect(status().isOk());

        // Validate the TypeOfBadge in the database
        List<TypeOfBadge> typeOfBadges = typeOfBadgeRepository.findAll();
        assertThat(typeOfBadges).hasSize(databaseSizeBeforeUpdate);
        TypeOfBadge testTypeOfBadge = typeOfBadges.get(typeOfBadges.size() - 1);
        assertThat(testTypeOfBadge.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTypeOfBadge.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the TypeOfBadge in ElasticSearch
        TypeOfBadge typeOfBadgeEs = typeOfBadgeSearchRepository.findOne(testTypeOfBadge.getId());
        assertThat(typeOfBadgeEs).isEqualToComparingFieldByField(testTypeOfBadge);
    }

    @Test
    @Transactional
    public void deleteTypeOfBadge() throws Exception {
        // Initialize the database
        typeOfBadgeRepository.saveAndFlush(typeOfBadge);
        typeOfBadgeSearchRepository.save(typeOfBadge);
        int databaseSizeBeforeDelete = typeOfBadgeRepository.findAll().size();

        // Get the typeOfBadge
        restTypeOfBadgeMockMvc.perform(delete("/api/type-of-badges/{id}", typeOfBadge.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean typeOfBadgeExistsInEs = typeOfBadgeSearchRepository.exists(typeOfBadge.getId());
        assertThat(typeOfBadgeExistsInEs).isFalse();

        // Validate the database is empty
        List<TypeOfBadge> typeOfBadges = typeOfBadgeRepository.findAll();
        assertThat(typeOfBadges).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTypeOfBadge() throws Exception {
        // Initialize the database
        typeOfBadgeRepository.saveAndFlush(typeOfBadge);
        typeOfBadgeSearchRepository.save(typeOfBadge);

        // Search the typeOfBadge
        restTypeOfBadgeMockMvc.perform(get("/api/_search/type-of-badges?query=id:" + typeOfBadge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeOfBadge.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}

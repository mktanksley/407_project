package cz.cvut.fel.karolan1.tidyup.web.rest;

import cz.cvut.fel.karolan1.tidyup.TidyUpApp;
import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import cz.cvut.fel.karolan1.tidyup.repository.ChoreEventRepository;
import cz.cvut.fel.karolan1.tidyup.repository.search.ChoreEventSearchRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ChoreEventResource REST controller.
 *
 * @see ChoreEventResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TidyUpApp.class)
@WebAppConfiguration
@IntegrationTest
public class ChoreEventResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ZonedDateTime DEFAULT_DATE_TO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_TO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_TO_STR = dateTimeFormatter.format(DEFAULT_DATE_TO);

    private static final ZonedDateTime DEFAULT_DATE_DONE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE_DONE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_DONE_STR = dateTimeFormatter.format(DEFAULT_DATE_DONE);

    @Inject
    private ChoreEventRepository choreEventRepository;

    @Inject
    private ChoreEventSearchRepository choreEventSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restChoreEventMockMvc;

    private ChoreEvent choreEvent;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ChoreEventResource choreEventResource = new ChoreEventResource();
        ReflectionTestUtils.setField(choreEventResource, "choreEventSearchRepository", choreEventSearchRepository);
        ReflectionTestUtils.setField(choreEventResource, "choreEventRepository", choreEventRepository);
        this.restChoreEventMockMvc = MockMvcBuilders.standaloneSetup(choreEventResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        choreEventSearchRepository.deleteAll();
        choreEvent = new ChoreEvent();
        choreEvent.setDateTo(DEFAULT_DATE_TO);
        choreEvent.setDateDone(DEFAULT_DATE_DONE);
    }

    @Test
    @Transactional
    public void createChoreEvent() throws Exception {
        int databaseSizeBeforeCreate = choreEventRepository.findAll().size();

        // Create the ChoreEvent

        restChoreEventMockMvc.perform(post("/api/chore-events")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(choreEvent)))
                .andExpect(status().isCreated());

        // Validate the ChoreEvent in the database
        List<ChoreEvent> choreEvents = choreEventRepository.findAll();
        assertThat(choreEvents).hasSize(databaseSizeBeforeCreate + 1);
        ChoreEvent testChoreEvent = choreEvents.get(choreEvents.size() - 1);
        assertThat(testChoreEvent.getDateTo()).isEqualTo(DEFAULT_DATE_TO);
        assertThat(testChoreEvent.getDateDone()).isEqualTo(DEFAULT_DATE_DONE);

        // Validate the ChoreEvent in ElasticSearch
        ChoreEvent choreEventEs = choreEventSearchRepository.findOne(testChoreEvent.getId());
        assertThat(choreEventEs).isEqualToComparingFieldByField(testChoreEvent);
    }

    @Test
    @Transactional
    public void getChoreEvent() throws Exception {
        // Initialize the database
        choreEventRepository.saveAndFlush(choreEvent);

        // Get the choreEvent
        restChoreEventMockMvc.perform(get("/api/chore-events/{id}", choreEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(choreEvent.getId().intValue()))
            .andExpect(jsonPath("$.dateTo").value(DEFAULT_DATE_TO_STR))
            .andExpect(jsonPath("$.dateDone").value(DEFAULT_DATE_DONE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingChoreEvent() throws Exception {
        // Get the choreEvent
        restChoreEventMockMvc.perform(get("/api/chore-events/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteChoreEvent() throws Exception {
        // Initialize the database
        choreEventRepository.saveAndFlush(choreEvent);
        choreEventSearchRepository.save(choreEvent);
        int databaseSizeBeforeDelete = choreEventRepository.findAll().size();

        // Get the choreEvent
        restChoreEventMockMvc.perform(delete("/api/chore-events/{id}", choreEvent.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean choreEventExistsInEs = choreEventSearchRepository.exists(choreEvent.getId());
        assertThat(choreEventExistsInEs).isFalse();

        // Validate the database is empty
        List<ChoreEvent> choreEvents = choreEventRepository.findAll();
        assertThat(choreEvents).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchChoreEvent() throws Exception {
        // Initialize the database
        choreEventRepository.saveAndFlush(choreEvent);
        choreEventSearchRepository.save(choreEvent);

        // Search the choreEvent
        restChoreEventMockMvc.perform(get("/api/_search/chore-events?query=id:" + choreEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(choreEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateTo").value(hasItem(DEFAULT_DATE_TO_STR)))
            .andExpect(jsonPath("$.[*].dateDone").value(hasItem(DEFAULT_DATE_DONE_STR)));
    }
}

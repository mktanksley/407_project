package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreEvent;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ChoreEvent entity.
 */
public interface ChoreEventSearchRepository extends ElasticsearchRepository<ChoreEvent, Long> {
}

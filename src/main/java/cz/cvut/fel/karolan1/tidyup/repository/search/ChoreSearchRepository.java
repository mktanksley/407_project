package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.Chore;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Chore entity.
 */
public interface ChoreSearchRepository extends ElasticsearchRepository<Chore, Long> {
}

package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.ChoreType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ChoreType entity.
 */
public interface ChoreTypeSearchRepository extends ElasticsearchRepository<ChoreType, Long> {
}

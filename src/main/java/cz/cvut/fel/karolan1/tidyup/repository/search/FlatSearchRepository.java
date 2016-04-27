package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.Flat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Flat entity.
 */
public interface FlatSearchRepository extends ElasticsearchRepository<Flat, Long> {
}

package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.TypeOfBadge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TypeOfBadge entity.
 */
public interface TypeOfBadgeSearchRepository extends ElasticsearchRepository<TypeOfBadge, Long> {
}

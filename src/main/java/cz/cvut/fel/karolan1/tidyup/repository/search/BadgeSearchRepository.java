package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.Badge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Badge entity.
 */
public interface BadgeSearchRepository extends ElasticsearchRepository<Badge, Long> {
}

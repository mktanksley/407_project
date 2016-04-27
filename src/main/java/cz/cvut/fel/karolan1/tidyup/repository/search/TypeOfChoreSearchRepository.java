package cz.cvut.fel.karolan1.tidyup.repository.search;

import cz.cvut.fel.karolan1.tidyup.domain.TypeOfChore;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TypeOfChore entity.
 */
public interface TypeOfChoreSearchRepository extends ElasticsearchRepository<TypeOfChore, Long> {
}

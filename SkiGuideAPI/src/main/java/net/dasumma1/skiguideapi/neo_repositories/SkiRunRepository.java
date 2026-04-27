package net.dasumma1.skiguideapi.neo_repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiRun;

/**
 * Repository interface for Neo4j operations related to Ski Runs.
 * <p>
 * This interface provides the standard persistence layer for {@link NeoSkiRun} 
 * nodes, enabling easy retrieval, saving, and deletion of ski run data within 
 * the Neo4j graph database.
 */
public interface SkiRunRepository extends Neo4jRepository<NeoSkiRun, String> {
    // Inherits standard CRUD functionality from Neo4jRepository.
}
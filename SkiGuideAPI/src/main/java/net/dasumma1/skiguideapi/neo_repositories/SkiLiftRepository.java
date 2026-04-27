package net.dasumma1.skiguideapi.neo_repositories;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiLift;

/**
 * Repository interface for Neo4j operations related to Ski Lifts.
 * <p>
 * This interface extends {@link Neo4jRepository} to provide standard 
 * CRUD (Create, Read, Update, Delete) operations and custom query 
 * execution for {@link NeoSkiLift} nodes within the graph database.
 */
public interface SkiLiftRepository extends Neo4jRepository<NeoSkiLift, String> {
    // No custom methods required; inherits standard Spring Data Neo4j functionality.
}
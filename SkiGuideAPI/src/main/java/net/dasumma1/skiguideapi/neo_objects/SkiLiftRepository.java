package net.dasumma1.skiguideapi.neo_objects;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SkiLiftRepository extends Neo4jRepository<NeoSkiLift, String> {
}
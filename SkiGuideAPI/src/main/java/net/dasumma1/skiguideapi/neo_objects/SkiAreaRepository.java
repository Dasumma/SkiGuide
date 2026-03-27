package net.dasumma1.skiguideapi.neo_objects;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface SkiAreaRepository extends Neo4jRepository<NeoSkiArea, String> {
    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(r:SkiRun) WHERE r.id = $runId RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiRunId(@Param("runId") String runId);
}
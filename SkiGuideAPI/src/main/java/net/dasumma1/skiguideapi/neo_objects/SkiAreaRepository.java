package net.dasumma1.skiguideapi.neo_objects;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import net.dasumma1.skiguideapi.area_objects.RouteResult;

public interface SkiAreaRepository extends Neo4jRepository<NeoSkiArea, String> {
    String delete_gds_graph = "CALL gds.graph.drop('skiMap', false) YIELD graphName RETURN null";
    
    String create_gds_graph = "CALL gds.graph.project.cypher(\n" + //
                "  'skiMap',\n" + //
                "  'MATCH (s:SkiRun)-[:HAS_POINT]->(p:Point) \n" + //
                "      WHERE s.id IN ' + $skiAreas + '\n" + //
                "      RETURN id(p) AS id\n" + //
                "   UNION\n" + //
                "   MATCH (:SkiLift)-[:HAS_POINT]->(p:Point) RETURN id(p) as id',\n" + //
                "  'MATCH (a:Point)-[s:SEGMENT|CONNECTION]->(b:Point) RETURN id(a) as source, id(b) as target, s.distance as distance',\n" + //
                "  {validateRelationships: false}\n" + //
                ") YIELD graphName RETURN null;";

    String get_route = "// Find a path from a Lift's top to a Run's end\r\n" + //
                "MATCH (l:SkiRun {name: \"Thunderbolt\"})-[:HAS_ENTRY]->(start:Point)\r\n" + //
                "MATCH (r:SkiRun {name: \"Turkey Turn\"})-[:HAS_EXIT]->(end:Point)\r\n" + //
                "\r\n" + //
                "CALL gds.shortestPath.dijkstra.stream('skiMap', {\r\n" + //
                "    sourceNode: start,\r\n" + //
                "    targetNode: end,\r\n" + //
                "    relationshipWeightProperty: 'distance'\r\n" + //
                "})\r\n" + //
                "YIELD nodeIds, totalCost\r\n" + //
                "RETURN [nodeId in nodeIds | [gds.util.asNode(nodeId).lat,  gds.util.asNode(nodeId).lon]] AS path,\r\n" + //
                "       totalCost AS distance";

    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(r:SkiRun) WHERE r.id = $runId RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiRunId(@Param("runId") String runId);

    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(l:SkiLift) WHERE l.id = $liftId RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiLiftId(@Param("liftId") String liftId);

    @Query(delete_gds_graph)
    void deleteGdsGraph();

    @Query(create_gds_graph)
    void createGdsGraph(@Param("skiAreas") String skiAreas);

    @Query(get_route)
    RouteResult customQuery();

}
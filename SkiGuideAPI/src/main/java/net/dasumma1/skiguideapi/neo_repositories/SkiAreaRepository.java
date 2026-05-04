package net.dasumma1.skiguideapi.neo_repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import net.dasumma1.skiguideapi.area_objects.RouteResult;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiPoint;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiLift;

/**
 * Repository interface for Neo4j operations related to Ski Areas.
 * <p>
 * This repository handles complex graph operations, including the projection of 
 * Graph Data Science (GDS) graphs, shortest path calculations via Dijkstra, 
 * and bulk data ingestion from JSON structures.
 */
public interface SkiAreaRepository extends Neo4jRepository<NeoSkiArea, String> {
    /**
     * Finds the ski area associated with a specific feature.
     * @param featureId the unique identifier of the feature
     * @return a list of {@link NeoSkiArea} entities containing the feature
     */
    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(f) WHERE f.id = $featureId RETURN a")
    List<NeoSkiArea> findSkiAreaByFeatureId(@Param("featureId") String featureId);

    /**
     * Finds the ski area associated with a specific ski lift.
     * @param liftId the unique identifier of the ski lift
     * @return a list of {@link NeoSkiArea} entities containing the lift
     */
    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(l:SkiLift) WHERE l.id = $liftId RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiLiftId(@Param("liftId") String liftId);

    /**
     * Drops the 'skiMap' GDS graph from memory to free up resources.
     */
    @Query("CALL gds.graph.drop('skiMap', false) YIELD graphName RETURN null;")
    void deleteGdsGraph();

    /**
     * Creates a new GDS graph projection named 'skiMap' using a list of prioritized ski runs.
     * @param skiRuns a list of run IDs to include in the graph projection
     */
    @Query( 
        "CALL gds.graph.project.cypher(" +
        "  'skiMap'," +
        "  'MATCH (p:Point) " +
        "   WHERE EXISTS { (p)<-[:HAS_POINT]-(r:SkiRun) WHERE r.id IN $skiruns } " +
        "      OR EXISTS { (p)<-[:HAS_POINT]-(:SkiLift) } " +
        "   RETURN id(p) AS id'," +
        "  'MATCH (a:Point)-[s:SEGMENT|CONNECTION]->(b:Point) " +
        "   RETURN id(a) as source, id(b) as target, s.distance as distance'," +
        "  { validateRelationships: false, parameters: { skiruns: $skiruns } }" +
        ") YIELD graphName RETURN null;"
        )
    void createGdsGraph(@Param("skiruns") List<String> skiRuns);

    /**
     * Executes a Dijkstra shortest path search on the 'skiMap' projection.
     * @param startId the ID of the starting Point
     * @param endId   the ID of the destination Point
     * @return a {@link RouteResult} containing the path coordinates and total distance
     */
    @Query(
        "MATCH (start:Point {id: $startId}) " + 
        "MATCH (end:Point {id: $endId}) " + 
        "CALL gds.shortestPath.dijkstra.stream('skiMap', { " + 
        "    sourceNode: start, " + 
        "    targetNode: end, " + 
        "    relationshipWeightProperty: 'distance' " + 
        "}) " + 
        "YIELD nodeIds, totalCost " + 
        "RETURN [nodeId in nodeIds | [gds.util.asNode(nodeId).lon, gds.util.asNode(nodeId).lat]] AS path, " + 
        "       totalCost AS distance"
    )
    RouteResult getRoute(@Param("startId") String startId, @Param("endId") String endId);

    /**
     * Finds the single closest ski area to a given coordinate.
     * @param point the {@link Point} to search from
     * @return the nearest {@link NeoSkiArea} node
     */
    @Query(
        "WITH point({longitude: $lon, latitude: $lat}) AS searchPoint \n" +
        "MATCH (a:Point) \n" +
        "WITH a, point.distance(searchPoint, point({longitude: a.lon, latitude: a.lat})) AS dist \n" +
        "ORDER BY dist ASC \n" +
        "LIMIT 1 \n" +
        "WITH a, dist \n" +
        "MATCH (a)<-[:HAS_POINT]-(s:SkiRun)<-[:HAS_FEATURE]-(area:SkiArea) \n" +
        "RETURN area"
    )
    NeoSkiArea findClosestSkiArea(@Param("lon") double lon, @Param("lat") double lat);

    /**
     * Finds the closest point in the graph to the given longitude and latitude.
     * @param lon The longitude used to locate the closest point.
     * @param lat The latitude used to locate the closest point.
     * @return The closets {@link NeoSkiPoint} in the Neo4J graph.
     */
    @Query(
        "WITH point({longitude: $lon, latitude: $lat}) AS searchPoint \n" +
        "MATCH (a:Point) \n" +
        "WITH a, point.distance(searchPoint, point({longitude: a.lon, latitude: a.lat})) AS dist \n" +
        "ORDER BY dist ASC \n" +
        "LIMIT 1 \n" +
        "RETURN a.lon AS lon, a.lat AS lat, a.alt AS alt"
    )
    NeoSkiPoint findClosestPoint(@Param("lon") double lon, @Param("lat") double lat);

    /**
     * Finds all ski runs associated with a specific ski area.
     * @param skiAreaId the unique identifier of the ski area
     * @return the {@link NeoSkiRun} objects linked to this area
     */
    @Query(
        "MATCH (a:SkiArea {id: $skiAreaId})-[:HAS_FEATURE]->(r:SkiRun) " +
        "RETURN r.id as id, r.name as name"
    )
    List<NeoSkiRun> getSkiRunsByAreaId(@Param("skiAreaId") String skiAreaId);

    /**
     * Finds all ski lifts associated with a specific ski area.
     * @param skiAreaId the unique identifier of the ski area
     * @return the {@link NeoSkiLift} objects linked to this area
     */
    @Query(
        "MATCH (a:SkiArea {id: $skiAreaId})-[:HAS_FEATURE]->(l:SkiLift) " +
        "RETURN l.id as id, l.name as name"
    )
    List<NeoSkiLift> getSkiLiftsByAreaId(@Param("skiAreaId") String skiAreaId);

    /**
     * Finds all points associated with a specific ski run or ski lift, ordered by their sequence in the run/lift.
     * @param id The unique identifier of the ski run or ski lift
     * @return The list of {@link NeoSkiPoint} objects associated with the ski feature
     */
    @Query(
        "MATCH (a:SkiRun|SkiLift)-[rel:HAS_POINT]->(p:Point)\n" + //
            "WHERE a.id = $id\n" + //
            "RETURN p.lon AS lon, p.lat AS lat, p.alt AS alt\n" + //
            "ORDER BY rel.sequence"
    )
    List<NeoSkiPoint> getPointsByFeatureId(@Param("id") String id);
    
    /**
     * Finds a ski area by its name field.
     * @param name Ski area name to search for.
     * @return The {@link NeoSkiArea} object matching the provided name.
     */
    @Query(
        "MATCH (a:SkiArea {name: $name}) RETURN a"
    )
    NeoSkiArea getSkiAreaByName(@Param("name") String name);
}
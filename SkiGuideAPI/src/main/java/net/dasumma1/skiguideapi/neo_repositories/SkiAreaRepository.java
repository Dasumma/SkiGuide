package net.dasumma1.skiguideapi.neo_repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import net.dasumma1.skiguideapi.area_objects.Point;
import net.dasumma1.skiguideapi.area_objects.RouteResult;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiPoint;

/**
 * Repository interface for Neo4j operations related to Ski Areas.
 * <p>
 * This repository handles complex graph operations, including the projection of 
 * Graph Data Science (GDS) graphs, shortest path calculations via Dijkstra, 
 * and bulk data ingestion from JSON structures.
 */
public interface SkiAreaRepository extends Neo4jRepository<NeoSkiArea, String> {

    /** Cypher command to drop an existing GDS graph from memory. */
    String delete_gds_graph = "CALL gds.graph.drop('skiMap', false) YIELD graphName RETURN null;";
    
    /** * Cypher command to project a subset of the graph into GDS memory.
     * It filters points associated with specific ski runs and all ski lifts.
     */
    String create_gds_graph = "CALL gds.graph.project.cypher(" +
                "  'skiMap'," +
                "  'MATCH (s:SkiRun)-[:HAS_POINT]->(p:Point) " +
                "      " +
                "      RETURN id(p) AS id  " +
                "   UNION " +
                "   MATCH (:SkiLift)-[:HAS_POINT]->(p:Point) RETURN id(p) as id'," +
                "  'MATCH (a:Point)-[s:SEGMENT|CONNECTION]->(b:Point) RETURN id(a) as source, id(b) as target, s.distance as distance'," +
                "  { validateRelationships: false, parameters: { skiruns: $skiruns } }" +
                ") YIELD graphName RETURN null;";

    /** * Cypher query using Dijkstra's algorithm to find the shortest path between two points.
     * Returns a {@link RouteResult} containing the coordinate path and total distance.
     */
    String get_route = "// Find a path from a Lift's top to a Run's end\r\n" + 
                "MATCH (start:Point {id: $startId })\r\n" + 
                "MATCH (end:Point {id: $endId })\r\n" + 
                "\r\n" + 
                "CALL gds.shortestPath.dijkstra.stream('skiMap', {\r\n" + 
                "    sourceNode: start,\r\n" + 
                "    targetNode: end,\r\n" + 
                "    relationshipWeightProperty: 'distance'\r\n" + 
                "})\r\n" + 
                "YIELD nodeIds, totalCost\r\n" + 
                "RETURN [nodeId in nodeIds | [gds.util.asNode(nodeId).lon,  gds.util.asNode(nodeId).lat]] AS path,\r\n" + 
                "       totalCost AS distance";

    /** Ingests SkiArea metadata and calculates an average geographical center point. */
    String create_ski_areas = "// 1. Load the data\n" + 
                "CALL apoc.load.json(\"$skiarea$\") YIELD value\n" + 
                "UNWIND value AS feature\n" + 
                "\n" + 
                "// 2. Extract and average the coordinates first\n" + 
                "WITH feature, feature.geometry.coordinates[0] AS points\n" + 
                "UNWIND points AS point\n" + 
                "WITH feature, avg(point[0]) AS avgLon, avg(point[1]) AS avgLat\n" + 
                "\n" + 
                "// 3. Now that we have the averages, create the node\n" + 
                "MERGE (s:SkiArea {id: feature.properties.id})\n" + 
                "SET s.name = feature.properties.name,\n" + 
                "    s.maxElevation = feature.statistics.maxElevation,\n" + 
                "    s.minElevation = feature.statistics.minElevation,\n" + 
                "    s.avgLocation = point({longitude: avgLon, latitude: avgLat})";

    /** Ingests SkiRun metadata and links them to their respective SkiAreas. */
    String create_ski_runs_metadata = "// 2. CREATE SKI RUNS (METADATA)\r\n" + 
                "CALL apoc.load.json(\"$skiruns$\") YIELD value\r\n" + 
                "UNWIND value.features as feature\r\n" + 
                "MERGE (r:SkiRun {id: feature.properties.id})\r\n" + 
                "SET r.name = feature.properties.name,\r\n" + 
                "    r.difficulty = feature.properties.difficulty,\r\n" + 
                "    r.status = feature.properties.status,\r\n" + 
                "    r.geoType = feature.geometry.type\r\n" + 
                "WITH r, feature\r\n" + 
                "UNWIND feature.properties.skiAreas as SkiArea\r\n" + 
                "MATCH (s:SkiArea {id: SkiArea.properties.id})\r\n" + 
                "MERGE (s)-[:HAS_FEATURE]->(r);";

    /** Processes LineString geometry to create a sequence of Point nodes linked by SEGMENT relationships. */
    String create_run_points = "// 3. Create LineString Runs\r\n" + 
                "CALL apoc.load.json(\"$skiruns$\") YIELD value\r\n" + 
                "UNWIND value.features as feature\r\n" + 
                "MATCH (r:SkiRun {id: feature.properties.id})\r\n" + 
                "WHERE feature.geometry.type = 'LineString'\r\n" + 
                "UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i\r\n" + 
                "WITH r, i, feature.geometry.coordinates[i] AS coord\r\n" + 
                "WITH r, i, coord, toString(round(toFloat(coord[0]), 6)) + \";\" + toString(round(toFloat(coord[1]), 6)) AS coordKey\r\n" + 
                "MERGE (p:Point {id: coordKey})\r\n" + 
                "  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),\r\n" + 
                "      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})\r\n" + 
                "MERGE (r)-[:HAS_POINT]->(p)\r\n" + 
                "WITH r, p ORDER BY i\r\n" + 
                "WITH r, collect(p) AS pointList\r\n" + 
                "CALL apoc.nodes.link(pointList, 'SEGMENT', {avoidDuplicates: true});";

    /** Identifies the first and last points of a run and marks them as entries/exits. */
    String create_run_entries_exits = "CALL apoc.load.json(\"$skiruns$\") YIELD value\r\n" + 
                "UNWIND value.features as feature\r\n" + 
                "MATCH (r:SkiRun {id: feature.properties.id})\r\n" + 
                "WHERE feature.geometry.type = 'LineString'\r\n" + 
                "WITH r, feature.geometry.coordinates as coords\r\n" + 
                "WITH r, coords, coords[0] AS coord\r\n" + 
                "WITH r, coords, coord, toString(round(toFloat(coord[0]), 6)) + \";\" + toString(round(toFloat(coord[1]), 6)) AS coordKey\r\n" + 
                "MATCH (p:Point {id: coordKey})\r\n" + 
                "MERGE (r)-[:HAS_ENTRY]->(p)\r\n" + 
                "WITH r, coords, coords[-1] AS coord\r\n" + 
                "WITH r, coords, coord, toString(round(toFloat(coord[0]), 6)) + \";\" + toString(round(toFloat(coord[1]), 6)) AS coordKey\r\n" + 
                "MATCH (p:Point {id: coordKey})\r\n" + 
                "MERGE (r)-[:HAS_EXIT]->(p);";

    /** Ingests SkiLift data and creates entry/exit points to represent the lift's path. */
    String create_ski_lifts = "CALL apoc.load.json(\"$skilifts$\") YIELD value\r\n" + 
                "UNWIND value.features as feature\r\n" + 
                "MATCH (s:SkiArea)\r\n" + 
                "MERGE (l:SkiLift {id: feature.properties.id})\r\n" + 
                "SET l.name = feature.properties.name,\r\n" + 
                "    l.status = feature.properties.status\r\n" + 
                "MERGE (s)-[:HAS_FEATURE]->(l)\r\n" + 
                "WITH feature, l, feature.geometry.coordinates AS coords\r\n" + 
                "WITH feature, l, coords[0] AS EntryCoord, coords[-1] AS ExitCoord,\r\n" + 
                "    toString(round(toFloat(coords[0][0]), 6)) + \";\" + toString(round(toFloat(coords[0][1]), 6)) AS entryCoordKey,\r\n" + 
                "    toString(round(toFloat(coords[-1][0]), 6)) + \";\" + toString(round(toFloat(coords[-1][1]), 6)) AS exitCoordKey\r\n" + 
                "MERGE (entryPoint:Point {id: entryCoordKey})\r\n" + 
                "MERGE (exitPoint:Point {id: exitCoordKey})\r\n" + 
                "MERGE (l)-[:HAS_ENTRY]->(entryPoint)\r\n" + 
                "MERGE (l)-[:HAS_EXIT]->(exitPoint)\r\n" + 
                "MERGE (entryPoint)-[:SEGMENT]->(exitPoint);";

    /** * Spatial logic to link infrastructure. Connects lift exits to run entries and 
     * run exits to lift entries if they are within 50 meters.
     */
    String connect_runs_lifts = "MATCH (l:SkiLift)-[:HAS_EXIT]->(liftTop:Point)\r\n" + 
                "MATCH (r:SkiRun)-[:HAS_ENTRY]->(trailStart:Point)\r\n" + 
                "WITH liftTop, trailStart, r, l, \r\n" + 
                "     point.distance(liftTop.location, trailStart.location) AS dist\r\n" + 
                "WHERE dist < 50 AND liftTop <> trailStart\r\n" + 
                "MERGE (liftTop)-[c:CONNECTION]->(trailStart)\r\n" + 
                "SET c.distance = dist, c.type = \"Lift-to-Run\";";

    /** Iterates over all edges to calculate real-world distance and percentage slope. */
    String calculate_distances = "MATCH (p1:Point)-[rel:(SEGMENT|CONNECTION)]->(p2:Point)\r\n" + 
                "WITH rel, p1, p2, point.distance(p1.location, p2.location) AS dist\r\n" + 
                "SET rel.distance = dist,\r\n" + 
                "    rel.slope = CASE WHEN dist > 0 THEN (abs(p1.alt - p2.alt) / dist) * 100 ELSE 0 END;";

    /**
     * Finds the ski area associated with a specific ski run.
     * * @param runId the unique identifier of the ski run
     * @return a list of {@link NeoSkiArea} entities containing the run
     */
    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(r:SkiRun) WHERE r.id = $runId RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiRunId(@Param("runId") String runId);

    /**
     * Finds the ski area associated with a specific ski lift.
     * * @param liftId the unique identifier of the ski lift
     * @return a list of {@link NeoSkiArea} entities containing the lift
     */
    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(l:SkiLift) WHERE l.id = $liftId RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiLiftId(@Param("liftId") String liftId);

    /**
     * Drops the 'skiMap' GDS graph from memory to free up resources.
     */
    @Query(delete_gds_graph)
    void deleteGdsGraph();

    /**
     * Creates a new GDS graph projection named 'skiMap' using a list of prioritized ski runs.
     * * @param skiRuns a list of run IDs to include in the graph projection
     */
    @Query(create_gds_graph)
    void createGdsGraph(@Param("skiruns") List<String> skiRuns);

    /**
     * Executes a Dijkstra shortest path search on the 'skiMap' projection.
     * * @param startId the ID of the starting Point
     * @param endId   the ID of the destination Point
     * @return a {@link RouteResult} containing the path coordinates and total distance
     */
    @Query(get_route)
    RouteResult getRoute(@Param("startId") String startId, @Param("endId") String endId);

    /**
     * Ingests ski area data from a JSON string.
     * * @param skiAreaJson raw JSON string representing ski areas
     */
    @Query(create_ski_areas)
    void createSkiAreas(@Param("$skiarea$") String skiAreaJson);

    /**
     * Ingests ski run metadata from a JSON string and links them to areas.
     * * @param skiRunsJson raw JSON string representing ski runs
     */
    @Query(create_ski_runs_metadata)
    void createSkiRunsMetadata(@Param("$skiruns$") String skiRunsJson);

    /**
     * Processes run geometry to create a network of Point nodes and SEGMENT relationships.
     * * @param skiRunsJson raw JSON string containing run geometry
     */
    @Query(create_run_points)
    void createRunPoints(@Param("$skiruns$") String skiRunsJson);

    /**
     * Marks the beginning and end of ski runs as specific Entry/Exit nodes.
     * * @param skiRunsJson raw JSON string containing run geometry
     */
    @Query(create_run_entries_exits)
    void createRunEntriesExits(@Param("$skiruns$") String skiRunsJson);

    /**
     * Ingests ski lift data and creates topological connections between lift start/end points.
     * * @param skiLiftsJson raw JSON string representing ski lifts
     */
    @Query(create_ski_lifts)
    void createSkiLifts(@Param("$skilifts$") String skiLiftsJson);

    /**
     * Automatically creates CONNECTION relationships between lift exits and run entries 
     * (and vice versa) based on spatial proximity.
     */
    @Query(connect_runs_lifts)
    void connectRunsLifts();

    /**
     * Updates all SEGMENT and CONNECTION relationships with calculated distance and slope.
     */
    @Query(calculate_distances)
    void calculateDistances();
    
    /**
     * Finds the single closest ski area to a given coordinate.
     * * @param point the {@link Point} to search from
     * @return the nearest {@link NeoSkiArea} node
     */
    @Query("MATCH (a:SkiArea) RETURN a ORDER BY point.distance(a.avgLocation, $point) LIMIT 1")
    NeoSkiArea getClosestSkiArea(@Param("point") Point point);

    @Query(
        "WITH point({longitude: $longitude, latitude: $latitude}) AS searchPoint \n" +
        "MATCH (a:Point) \n" +
        "WITH a, point.distance(searchPoint, point({longitude: a.lon, latitude: a.lat})) AS dist \n" +
        "ORDER BY dist ASC \n" +
        "LIMIT 1 \n" +
        "RETURN a.lon AS longitude, a.lat AS latitude"
    )
    NeoSkiPoint findClosestPoint(@Param("longitude") double longitude, @Param("latitude") double latitude);
}
package net.dasumma1.skiguideapi.neo_objects;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import net.dasumma1.skiguideapi.area_objects.RouteResult;

public interface SkiAreaRepository extends Neo4jRepository<NeoSkiArea, String> {
    String delete_gds_graph = "CALL gds.graph.drop('skiMap', false) YIELD graphName RETURN null";
    
    String create_gds_graph = "CALL gds.graph.project.cypher(\n" + //
                "  'skiMap',\n" + //
                "  'MATCH (s:SkiRun)-[:HAS_POINT]->(p:Point) \n" + //
                "      WHERE s.id IN ' +  + '\n" + //
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

    String create_ski_area = "MATCH (N) DETACH DELETE N;\r\n" + //
                "// 1. CREATE SKI AREA\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "CALL apoc.load.json(\"$skiarea$\") YIELD value\r\n" + //
                "UNWIND value as feature\r\n" + //
                "MERGE (s:SkiArea {id: feature.properties.id})\r\n" + //
                "SET s.name = feature.properties.name,\r\n" + //
                "    s.maxElevation = feature.statistics.maxElevation,\r\n" + //
                "    s.minElevation = feature.statistics.minElevation,\r\n" + //
                "    s.avgLocation = {\r\n" + //
                "        WITH feature.geometry.coordinates[0] AS points\r\n" + //
                "        UNWIND points AS point\r\n" + //
                "        WITH avg(point[0]) AS avgLon, avg(point[1]) AS avgLat\r\n" + //
                "        RETURN point({longitude: avgLon, latitude: avgLat})\r\n" + //
                "    }\r\n" + //
                "\r\n" + //
                "// 2. CREATE SKI RUNS (METADATA)\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "CALL apoc.load.json(\"$skiruns$\") YIELD value\r\n" + //
                "UNWIND value.features as feature\r\n" + //
                "MERGE (r:SkiRun {id: feature.properties.id})\r\n" + //
                "SET r.name = feature.properties.name,\r\n" + //
                "    r.difficulty = feature.properties.difficulty,\r\n" + //
                "    r.status = feature.properties.status,\r\n" + //
                "    r.geoType = feature.geometry.type\r\n" + //
                "WITH r, feature\r\n" + //
                "UNWIND feature.properties.skiAreas as SkiArea\r\n" + //
                "MATCH (s:SkiArea {id: SkiArea.properties.id})\r\n" + //
                "MERGE (s)-[:HAS_FEATURE]->(r);\r\n" + //
                "\r\n" + //
                "// 3. Create LineString Runs\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "CALL apoc.load.json(\"$skiruns$\") YIELD value\r\n" + //
                "UNWIND value.features as feature\r\n" + //
                "MATCH (r:SkiRun {id: feature.properties.id})\r\n" + //
                "WHERE feature.geometry.type = 'LineString'\r\n" + //
                "UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i\r\n" + //
                "WITH r, i, feature.geometry.coordinates[i] AS coord\r\n" + //
                "WITH r, i, coord, toString(round(toFloat(coord[0]), 6)) + \";\" + toString(round(toFloat(coord[1]), 6)) AS coordKey\r\n" + //
                "MERGE (p:Point {id: coordKey})\r\n" + //
                "  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),\r\n" + //
                "      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})\r\n" + //
                "MERGE (r)-[:HAS_POINT]->(p)\r\n" + //
                "WITH r, p ORDER BY i\r\n" + //
                "WITH r, collect(p) AS pointList\r\n" + //
                "CALL apoc.nodes.link(pointList, 'SEGMENT', {avoidDuplicates: true});\r\n" + //
                "\r\n" + //
                "// 3.1. CREATE ENTRY AND EXITS\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "CALL apoc.load.json(\"$skiruns$\") YIELD value\r\n" + //
                "UNWIND value.features as feature\r\n" + //
                "MATCH (r:SkiRun {id: feature.properties.id})\r\n" + //
                "WHERE feature.geometry.type = 'LineString'\r\n" + //
                "WITH r, feature.geometry.coordinates as coords\r\n" + //
                "WITH r, coords, coords[0] AS coord\r\n" + //
                "WITH r, coords, coord, toString(round(toFloat(coord[0]), 6)) + \";\" + toString(round(toFloat(coord[1]), 6)) AS coordKey\r\n" + //
                "MATCH (p:Point {id: coordKey})\r\n" + //
                "MERGE (r)-[:HAS_ENTRY]->(p)\r\n" + //
                "  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),\r\n" + //
                "      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})\r\n" + //
                "WITH r, coords, coords[-1] AS coord\r\n" + //
                "WITH r, coords, coord, toString(round(toFloat(coord[0]), 6)) + \";\" + toString(round(toFloat(coord[1]), 6)) AS coordKey\r\n" + //
                "MATCH (p:Point {id: coordKey})\r\n" + //
                "MERGE (r)-[:HAS_EXIT]->(p)\r\n" + //
                "  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),\r\n" + //
                "      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])});\r\n" + //
                "\r\n" + //
                "// 4. CREATE SKI LIFTS\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "CALL apoc.load.json(\"$skilifts$\") YIELD value\r\n" + //
                "UNWIND value.features as feature\r\n" + //
                "MATCH (s:SkiArea)\r\n" + //
                "MERGE (l:SkiLift {id: feature.properties.id})\r\n" + //
                "SET l.name = feature.properties.name,\r\n" + //
                "    l.status = feature.properties.status\r\n" + //
                "MERGE (s)-[:HAS_FEATURE]->(l)\r\n" + //
                "\r\n" + //
                "WITH feature, l, feature.geometry.coordinates AS coords\r\n" + //
                "WITH feature, l, coords[0] AS EntryCoord, coords[-1] AS ExitCoord,\r\n" + //
                "    toString(round(toFloat(coords[0][0]), 6)) + \";\" + toString(round(toFloat(coords[0][1]), 6)) AS entryCoordKey,\r\n" + //
                "    toString(round(toFloat(coords[-1][0]), 6)) + \";\" + toString(round(toFloat(coords[-1][1]), 6)) AS exitCoordKey\r\n" + //
                "MERGE (entryPoint:Point {id: entryCoordKey})\r\n" + //
                "  SET entryPoint.lon = toFloat(EntryCoord[0]), entryPoint.lat = toFloat(EntryCoord[1]), entryPoint.alt = toFloat(EntryCoord[2]),\r\n" + //
                "      entryPoint.location = point({longitude: toFloat(EntryCoord[0]), latitude: toFloat(EntryCoord[1]), height: toFloat(EntryCoord[2])})\r\n" + //
                "MERGE (exitPoint:Point {id: exitCoordKey})\r\n" + //
                "  SET exitPoint.lon = toFloat(ExitCoord[0]), exitPoint.lat = toFloat(ExitCoord[1]), exitPoint.alt = toFloat(ExitCoord[2]),\r\n" + //
                "      exitPoint.location = point({longitude: toFloat(ExitCoord[0]), latitude: toFloat(ExitCoord[1]), height: toFloat(ExitCoord[2])})\r\n" + //
                "MERGE (l)-[:HAS_ENTRY]->(entryPoint)\r\n" + //
                "MERGE (l)-[:HAS_POINT]->(entryPoint)\r\n" + //
                "MERGE (l)-[:HAS_EXIT]->(exitPoint)\r\n" + //
                "MERGE (l)-[:HAS_POINT]->(exitPoint)\r\n" + //
                "MERGE (entryPoint)-[:SEGMENT]->(exitPoint);\r\n" + //
                "\r\n" + //
                "/*WITH feature, l\r\n" + //
                "UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i\r\n" + //
                "WITH l, i, feature.geometry.coordinates[i] AS coord\r\n" + //
                "MERGE (p:Point {id: apoc.text.join(toStringList(coord), \";\")})\r\n" + //
                "  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),\r\n" + //
                "      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})\r\n" + //
                "MERGE (l)-[:HAS_POINT]->(p)\r\n" + //
                "WITH l, p ORDER BY i\r\n" + //
                "WITH l, collect(p) AS pointList\r\n" + //
                "CALL apoc.nodes.link(pointList, 'SEGMENT',  {avoidDuplicates: true});\r\n" + //
                "\r\n" + //
                "// 4.1. CREATE ENTRY AND EXITS\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "CALL apoc.load.json(\"https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/lifts_Mount%20Southington%20Ski%20Area.geojson\") YIELD value\r\n" + //
                "UNWIND value.features as feature\r\n" + //
                "MATCH (r:SkiLift {id: feature.properties.id})\r\n" + //
                "WITH r, feature.geometry.coordinates as coords\r\n" + //
                "WITH r, coords, apoc.text.join(toStringList(coords[0]), \";\") as EntryPoint\r\n" + //
                "MERGE (r)-[:HAS_ENTRY]->(p1:Point {id: EntryPoint})\r\n" + //
                "  SET p1.lon = toFloat(coords[0][0]), p1.lat = toFloat(coords[0][1]), p1.alt = toFloat(coords[0][2]),\r\n" + //
                "      p1.location = point({longitude: toFloat(coords[0][0]), latitude: toFloat(coords[0][1]), height: toFloat(coords[0][2])})\r\n" + //
                "WITH r, coords, EntryPoint, apoc.text.join(toStringList(coords[-1]), \";\") as ExitPoint\r\n" + //
                "MERGE (r)-[:HAS_EXIT]->(p2:Point {id: ExitPoint})\r\n" + //
                "  SET p2.lon = toFloat(coords[-1][0]), p2.lat = toFloat(coords[-1][1]), p2.alt = toFloat(coords[-1][2]),\r\n" + //
                "      p2.location = point({longitude: toFloat(coords[-1][0]), latitude: toFloat(coords[-1][1]), height: toFloat(coords[-1][2])})\r\n" + //
                "MERGE (p1)-[:SEGMENT]->(p2);*/\r\n" + //
                "\r\n" + //
                "// 5. CONNECT SKI RUNS TO SKI LIFTS\r\n" + //
                "\r\n" + //
                "// 5.1. Connect Lift Tops to Trail Entries (Starting a run)\r\n" + //
                "MATCH (l:SkiLift)-[:HAS_EXIT]->(liftTop:Point)\r\n" + //
                "MATCH (r:SkiRun)-[:HAS_ENTRY]->(trailStart:Point)\r\n" + //
                "WITH liftTop, trailStart, r, l, \r\n" + //
                "     point.distance(liftTop.location, trailStart.location) AS dist\r\n" + //
                "WHERE dist < 50\r\n" + //
                "AND liftTop <> trailStart\r\n" + //
                "MERGE (liftTop)-[c:CONNECTION]->(trailStart)\r\n" + //
                "SET c.distance = dist, \r\n" + //
                "    c.type = \"Lift-to-Run\",\r\n" + //
                "    c.slope = 0;\r\n" + //
                "\r\n" + //
                "// 5.2. Connect Trail Exits to Lift Entries (Getting back on the lift)\r\n" + //
                "MATCH (r:SkiRun)-[:HAS_EXIT]->(trailEnd:Point)\r\n" + //
                "MATCH (l:SkiLift)-[:HAS_ENTRY]->(liftBottom:Point)\r\n" + //
                "WITH trailEnd, liftBottom, r, l, \r\n" + //
                "     point.distance(trailEnd.location, liftBottom.location) AS dist\r\n" + //
                "WHERE dist < 50\r\n" + //
                "    AND trailEnd <> liftBottom\r\n" + //
                "MERGE (trailEnd)-[c:CONNECTION]->(liftBottom)\r\n" + //
                "SET c.distance = dist, \r\n" + //
                "    c.type = \"Run-to-Lift\",\r\n" + //
                "    c.slope = 0;\r\n" + //
                "\r\n" + //
                "// 5.3 Manually Connect Thunderbolt to Avalanche Double\r\n" + //
                "MATCH (r:SkiRun {name:\"Thunderbolt\"})-[:HAS_EXIT]->(trailEnd:Point)\r\n" + //
                "MATCH (l:SkiLift {name: \"Avalanche Double\"})-[:HAS_ENTRY]->(liftBottom:Point)\r\n" + //
                "WITH trailEnd, liftBottom, r, l, \r\n" + //
                "     point.distance(trailEnd.location, liftBottom.location) AS dist\r\n" + //
                "MERGE (trailEnd)-[c:CONNECTION]->(liftBottom)\r\n" + //
                "SET c.distance = dist,\r\n" + //
                "    c.type = \"Run-to-Lift\",\r\n" + //
                "    c.slope = 0;\r\n" + //
                "\r\n" + //
                "// 6. CALCULATE DISTANCE AND SLOPE FOR ALL EDGES\r\n" + //
                "// -------------------------------------------------------------------------6\r\n" + //
                "MATCH (p1:Point)-[rel:(SEGMENT|CONNECTION)]->(p2:Point)\r\n" + //
                "WITH rel, p1, p2, point.distance(p1.location, p2.location) AS dist\r\n" + //
                "SET rel.distance = dist,\r\n" + //
                "    rel.slope = CASE \r\n" + //
                "        WHEN dist > 0 THEN (abs(p1.alt - p2.alt) / dist) * 100 \r\n" + //
                "        ELSE 0 \r\n" + //
                "    END;\r\n" + //
                "\r\n" + //
                "// 7. CREATE CALLABLE FUNCTIONS\r\n" + //
                "// -------------------------------------------------------------------------\r\n" + //
                "\",2,0,false\r\n" + //
                "Create and Run Dijkstra,,\"// 1. Clear any existing projection\r\n" + //
                "CALL gds.graph.drop('skiMap', false)\r\n" + //
                "YIELD nodeCount\r\n" + //
                "WITH nodeCount as droppedNodeCount\r\n" + //
                "\r\n" + //
                "// 2. Project both SEGMENT and CONNECTION\r\n" + //
                "CALL gds.graph.project(\r\n" + //
                "  'skiMap',\r\n" + //
                "  'Point',\r\n" + //
                "  {\r\n" + //
                "    SEGMENT: { orientation: 'NATURAL' },\r\n" + //
                "    CONNECTION: { orientation: 'NATURAL' }\r\n" + //
                "  },\r\n" + //
                "  {\r\n" + //
                "    relationshipProperties: 'distance'\r\n" + //
                "  }\r\n" + //
                ") \r\n" + //
                "YIELD nodeCount\r\n" + //
                "WITH nodeCount\r\n" + //
                "\r\n" + //
                "// Find a path from a Lift's top to a Run's end\r\n" + //
                "MATCH (l:SkiRun {name: \"Thunderbolt\"})-[:HAS_ENTRY]->(start:Point)\r\n" + //
                "MATCH (r:SkiRun {name: \"Turkey Turn\"})-[:HAS_EXIT]->(end:Point)\r\n" + //
                "\r\n" + //
                "CALL gds.shortestPath.dijkstra.stream('skiMap', {\r\n" + //
                "    sourceNode: start,\r\n" + //
                "    targetNode: end,\r\n" + //
                "    relationshipWeightProperty: 'distance'\r\n" + //
                "})\r\n" + //
                "YIELD nodeIds, totalCost\r\n" + //
                "RETURN [nodeId in nodeIds | [gds.util.asNode(nodeId).lat,  gds.util.asNode(nodeId).lon]] AS coords,\r\n" + //
                "       totalCost AS distanceMeters;\",3,0,false";

    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(r:SkiRun) WHERE r.id =  RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiRunId(@Param("runId") String runId);

    @Query("MATCH (a:SkiArea)-[:HAS_FEATURE]->(l:SkiLift) WHERE l.id =  RETURN a")
    List<NeoSkiArea> findSkiAreaBySkiLiftId(@Param("liftId") String liftId);

    @Query(delete_gds_graph)
    void deleteGdsGraph();

    @Query(create_gds_graph)
    void createGdsGraph(@Param("skiAreas") String skiAreas);

    @Query(get_route)
    RouteResult customQuery();

    @Query(create_ski_area)
    void createSkiAreaUsingJson(@Param("$skiarea$") String skiAreaJson, @Param("$skiruns$") String skiRunsJson, @Param("$skilifts$") String skiLiftsJson);

    @Query("MATCH (a:SkiArea)")
    NeoSkiArea getClosestSkiArea(Point point);

}
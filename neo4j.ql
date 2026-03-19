MATCH (N) DETACH DELETE N;
// 1. CREATE SKI AREA
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/ski_areas_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value as feature
MERGE (s:SkiArea {id: feature.properties.id})
SET s.name = feature.properties.name,
    s.maxElevation = feature.statistics.maxElevation,
    s.minElevation = feature.statistics.minElevation;

// 2. CREATE SKI RUNS (METADATA)
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/runs_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MERGE (r:SkiRun {id: feature.properties.id})
SET r.name = feature.properties.name,
    r.difficulty = feature.properties.difficulty,
    r.status = feature.properties.status,
    r.geoType = feature.geometry.type
WITH r, feature
UNWIND feature.properties.skiAreas as SkiArea
MATCH (s:SkiArea {id: SkiArea.properties.id})
MERGE (s)-[:HAS_FEATURE]->(r);

// 3. Create LineString Runs
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/runs_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (r:SkiRun {id: feature.properties.id})
WHERE feature.geometry.type = 'LineString'
UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i
WITH r, i, feature.geometry.coordinates[i] AS coord
WITH r, i, coord, toString(round(toFloat(coord[0]), 6)) + ";" + toString(round(toFloat(coord[1]), 6)) AS coordKey
MERGE (p:Point {id: coordKey})
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})
MERGE (r)-[:HAS_POINT]->(p)
WITH r, p ORDER BY i
WITH r, collect(p) AS pointList
CALL apoc.nodes.link(pointList, 'SEGMENT', {avoidDuplicates: true});

// 3.1. CREATE ENTRY AND EXITS
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/runs_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (r:SkiRun {id: feature.properties.id})
WHERE feature.geometry.type = 'LineString'
WITH r, feature.geometry.coordinates as coords
WITH r, coords, coords[0] AS coord
WITH r, coords, coord, toString(round(toFloat(coord[0]), 6)) + ";" + toString(round(toFloat(coord[1]), 6)) AS coordKey
MATCH (p:Point {id: coordKey})
MERGE (r)-[:HAS_ENTRY]->(p)
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})
WITH r, coords, coords[-1] AS coord
WITH r, coords, coord, toString(round(toFloat(coord[0]), 6)) + ";" + toString(round(toFloat(coord[1]), 6)) AS coordKey
MATCH (p:Point {id: coordKey})
MERGE (r)-[:HAS_EXIT]->(p)
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])});

// 4. CREATE SKI LIFTS
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/lifts_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (s:SkiArea)
MERGE (l:SkiLift {id: feature.properties.id})
SET l.name = feature.properties.name,
    l.status = feature.properties.status
MERGE (s)-[:HAS_FEATURE]->(l)

WITH feature, l, feature.geometry.coordinates AS coords
WITH feature, l, coords[0] AS EntryCoord, coords[-1] AS ExitCoord,
    toString(round(toFloat(coords[0][0]), 6)) + ";" + toString(round(toFloat(coords[0][1]), 6)) AS entryCoordKey,
    toString(round(toFloat(coords[-1][0]), 6)) + ";" + toString(round(toFloat(coords[-1][1]), 6)) AS exitCoordKey
MERGE (entryPoint:Point {id: entryCoordKey})
  SET entryPoint.lon = toFloat(EntryCoord[0]), entryPoint.lat = toFloat(EntryCoord[1]), entryPoint.alt = toFloat(EntryCoord[2]),
      entryPoint.location = point({longitude: toFloat(EntryCoord[0]), latitude: toFloat(EntryCoord[1]), height: toFloat(EntryCoord[2])})
MERGE (exitPoint:Point {id: exitCoordKey})
  SET exitPoint.lon = toFloat(ExitCoord[0]), exitPoint.lat = toFloat(ExitCoord[1]), exitPoint.alt = toFloat(ExitCoord[2]),
      exitPoint.location = point({longitude: toFloat(ExitCoord[0]), latitude: toFloat(ExitCoord[1]), height: toFloat(ExitCoord[2])})
MERGE (l)-[:HAS_ENTRY]->(entryPoint)
MERGE (l)-[:HAS_POINT]->(entryPoint)
MERGE (l)-[:HAS_EXIT]->(exitPoint)
MERGE (l)-[:HAS_POINT]->(exitPoint)
MERGE (entryPoint)-[:SEGMENT]->(exitPoint);

/*WITH feature, l
UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i
WITH l, i, feature.geometry.coordinates[i] AS coord
MERGE (p:Point {id: apoc.text.join(toStringList(coord), ";")})
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})
MERGE (l)-[:HAS_POINT]->(p)
WITH l, p ORDER BY i
WITH l, collect(p) AS pointList
CALL apoc.nodes.link(pointList, 'SEGMENT',  {avoidDuplicates: true});

// 4.1. CREATE ENTRY AND EXITS
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/lifts_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (r:SkiLift {id: feature.properties.id})
WITH r, feature.geometry.coordinates as coords
WITH r, coords, apoc.text.join(toStringList(coords[0]), ";") as EntryPoint
MERGE (r)-[:HAS_ENTRY]->(p1:Point {id: EntryPoint})
  SET p1.lon = toFloat(coords[0][0]), p1.lat = toFloat(coords[0][1]), p1.alt = toFloat(coords[0][2]),
      p1.location = point({longitude: toFloat(coords[0][0]), latitude: toFloat(coords[0][1]), height: toFloat(coords[0][2])})
WITH r, coords, EntryPoint, apoc.text.join(toStringList(coords[-1]), ";") as ExitPoint
MERGE (r)-[:HAS_EXIT]->(p2:Point {id: ExitPoint})
  SET p2.lon = toFloat(coords[-1][0]), p2.lat = toFloat(coords[-1][1]), p2.alt = toFloat(coords[-1][2]),
      p2.location = point({longitude: toFloat(coords[-1][0]), latitude: toFloat(coords[-1][1]), height: toFloat(coords[-1][2])})
MERGE (p1)-[:SEGMENT]->(p2);*/

// 5. CONNECT SKI RUNS TO SKI LIFTS

// 5.1. Connect Lift Tops to Trail Entries (Starting a run)
MATCH (l:SkiLift)-[:HAS_EXIT]->(liftTop:Point)
MATCH (r:SkiRun)-[:HAS_ENTRY]->(trailStart:Point)
WITH liftTop, trailStart, r, l, 
     point.distance(liftTop.location, trailStart.location) AS dist
WHERE dist < 40
AND liftTop <> trailStart
MERGE (liftTop)-[c:CONNECTION]->(trailStart)
SET c.distance = dist, 
    c.type = "Lift-to-Run",
    c.slope = 0;

// 5.2. Connect Trail Exits to Lift Entries (Getting back on the lift)
MATCH (r:SkiRun)-[:HAS_EXIT]->(trailEnd:Point)
MATCH (l:SkiLift)-[:HAS_ENTRY]->(liftBottom:Point)
WITH trailEnd, liftBottom, r, l, 
     point.distance(trailEnd.location, liftBottom.location) AS dist
WHERE dist < 40
    AND trailEnd <> liftBottom
MERGE (trailEnd)-[c:CONNECTION]->(liftBottom)
SET c.distance = dist, 
    c.type = "Run-to-Lift",
    c.slope = 0;

// 6. CALCULATE DISTANCE AND SLOPE FOR ALL EDGES
// -------------------------------------------------------------------------
MATCH (p1:Point)-[rel:(SEGMENT|CONNECTION)]->(p2:Point)
WITH rel, p1, p2, point.distance(p1.location, p2.location) AS dist
SET rel.distance = dist,
    rel.slope = CASE 
        WHEN dist > 0 THEN (abs(p1.alt - p2.alt) / dist) * 100 
        ELSE 0 
    END;
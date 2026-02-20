// -------------------------------------------------------------------------
// 1. CREATE SKI AREA
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/ski_areas_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value as feature
MERGE (s:SkiArea {id: feature.properties.id})
SET s.name = feature.properties.name,
    s.maxElevation = feature.statistics.maxElevation,
    s.minElevation = feature.statistics.minElevation;

// -------------------------------------------------------------------------
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

// -------------------------------------------------------------------------
// 3. HANDLE LINESTRINGS (TRAILS)
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/runs_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (r:SkiRun {id: feature.properties.id})
WHERE feature.geometry.type = 'LineString'
UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i
WITH r, i, feature.geometry.coordinates[i] AS coord
MERGE (p:Point {id: apoc.text.join(toStringList(coord), ";")})
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})
MERGE (r)-[:HAS_POINT]->(p)
WITH r, p ORDER BY i
WITH r, collect(p) AS pointList
CALL apoc.nodes.link(pointList, 'SEGMENT', {avoidDuplicates: true});

// -------------------------------------------------------------------------
// 4. HANDLE POLYGONS (GLADES WITH "FISHBONE" SKELETON)
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/runs_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (r:SkiRun {id: feature.properties.id})
WHERE feature.geometry.type = 'Polygon'

// A. Create Perimeter Points
UNWIND range(0, size(feature.geometry.coordinates[0])/2 - 1) AS i
WITH r, i, feature.geometry.coordinates[0][i] AS coord, feature.geometry.coordinates[0][size(feature.geometry.coordinates[0])-i] AS coord2
MERGE (p:Point {id: apoc.text.join(toStringList(coord), ";")})
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})
MERGE (r)-[:HAS_POINT]->(p)

// B. Calculate Spine Centroids (Pre-calculate Averages)
WITH r, p, round(p.alt / 10) * 10 AS altBucket
WITH r, altBucket, 
     avg(p.lat) as avgLat, 
     avg(p.lon) as avgLon, 
     collect(p) as pointsInBucket
MERGE (spine:Point {id: r.id + "_spine_" + toString(altBucket)})
SET spine.lat = avgLat,
    spine.lon = avgLon,
    spine.alt = altBucket,
    spine.isSpine = true,
    spine.location = point({longitude: avgLon, latitude: avgLat, height: altBucket})
MERGE (r)-[:HAS_SPINE_NODE]->(spine)

// C. Connect the Spine (The Vertical Skeleton)
WITH r, spine ORDER BY spine.alt DESC
WITH r, collect(spine) AS spineList
CALL apoc.nodes.link(spineList, 'SEGMENT',  {avoidDuplicates: true})

// D. Ribs: Connect Perimeter Points to Nearest Spine Node
WITH r, spineList
MATCH (r)-[:HAS_POINT]->(p:Point)
WHERE p.isSpine IS NULL
UNWIND spineList AS sNode
WITH p, sNode, point.distance(p.location, sNode.location) AS dist
ORDER BY p, dist ASC
WITH p, collect({node: sNode, d: dist})[0] AS closest
WITH p, closest.node AS targetNode, closest.d AS d
MERGE (p)-[rel:MESH_EDGE]-(targetNode)
SET rel.distance = d;

// -------------------------------------------------------------------------
// 5. CREATE SKI LIFTS
// -------------------------------------------------------------------------
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/lifts_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value.features as feature
MATCH (s:SkiArea)
MERGE (l:SkiLift {id: feature.properties.id})
SET l.name = feature.properties.name,
    l.status = feature.properties.status
MERGE (s)-[:HAS_FEATURE]->(l)

WITH feature, l
UNWIND range(0, size(feature.geometry.coordinates) - 1) AS i
WITH l, i, feature.geometry.coordinates[i] AS coord
MERGE (p:Point {id: apoc.text.join(toStringList(coord), ";")})
  SET p.lon = toFloat(coord[0]), p.lat = toFloat(coord[1]), p.alt = toFloat(coord[2]),
      p.location = point({longitude: toFloat(coord[0]), latitude: toFloat(coord[1]), height: toFloat(coord[2])})
MERGE (l)-[:HAS_POINT]->(p)
WITH l, p ORDER BY i
WITH l, collect(p) AS pointList
CALL apoc.nodes.link(pointList, 'SEGMENT',  {avoidDuplicates: true});

// -------------------------------------------------------------------------
// 6. CALCULATE DISTANCE AND SLOPE FOR ALL EDGES
// -------------------------------------------------------------------------
MATCH (p1:Point)-[rel:SEGMENT|MESH_EDGE]->(p2:Point)
WITH rel, p1, p2, point.distance(p1.location, p2.location) AS dist
SET rel.distance = dist,
    rel.slope = CASE 
        WHEN dist > 0 THEN (abs(p1.alt - p2.alt) / dist) * 100 
        ELSE 0 
    END;
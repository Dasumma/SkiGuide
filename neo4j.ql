//Create Ski Area:
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/ski_areas_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value as feature
MERGE (s:SkiArea {id: feature.properties.id})
SET s.type = feature.properties.type,
    s.name = feature.properties.name,
    s.maxElevation = feature.statistics.maxElevation,
    s.minElevation = feature.statistics.minElevation

//Create Ski Run:
WITH s as SkiArea
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/runs_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value as feature
MERGE (r:SkiRun {id: feature.properties.id})
SET r.type = feature.properties.type,
    r.name = feature.properties.name,
    r.difficulty = feature.properties.difficulty,
    r.difficultyConvention = feature.properties.difficultyConvention,
    r.oneway = feature.properties.oneway,
    r.gladed = feature.properties.gladed,
    r.patrolled = feature.properties.patrolled,
    r.lit = feature.properties.lit,
    r.grooming = feature.properties.grooming,
    r.status = feature.properties.status
MERGE (SkiArea)-[:HAS_FEATURE]->(r)

WITH SkiArea, feature, r AS SkiRun,
CASE
  WHEN feature.geometry.coordinates IS :: LIST<LIST<LIST<ANY>>> THEN feature.geometry.coordinates[0]
  WHEN feature.geometry.coordinates IS :: LIST<LIST<ANY>> THEN feature.geometry.coordinates
END as coordinates
UNWIND range(0, size(coordinates) - 1) AS i
WITH SkiArea, feature, SkiRun, i, coordinates[i] as coord
MERGE (p:Point {id: apoc.text.join(toStringList(coord), ";")})
  SET p.lat = coord[0],
      p.lon = coord[1],
      p.ele = coord[2],
MERGE (SkiRun)-[:HAS_POINT]->(p)
WITH SkiArea, SkiRun, p ORDER BY p.sequence
WITH SkiArea, SkiRun, collect(p) AS pointList
CALL apoc.nodes.link(pointList, 'SEGMENT', {avoidDuplicates: true})

//Create Ski Lift:
WITH SkiArea
CALL apoc.load.json("https://raw.githubusercontent.com/Dasumma/SkiGuide/refs/heads/local_mountains/GeoJsons/United%20States/Connecticut/lifts_Mount%20Southington%20Ski%20Area.geojson") YIELD value
UNWIND value as feature
MERGE (l:SkiLift {id: feature.properties.id})
SET l.type = feature.properties.type,
    l.name = feature.properties.name,
    l.status = feature.properties.status,
    l.occupancy = feature.properties.occupancy,
    l.duration = feature.properties.duration,
    l.bubble = feature.properties.bubble,
    l.heating = feature.properties.heating
MERGE (SkiArea)-[:HAS_FEATURE]->(l)

WITH feature, l AS SkiLift, 
CASE
  WHEN feature.geometry.coordinates IS :: LIST<LIST<LIST<ANY>>> THEN feature.geometry.coordinates[0]
  WHEN feature.geometry.coordinates IS :: LIST<LIST<ANY>> THEN feature.geometry.coordinates
END as coordinates
UNWIND range(0, size(coordinates) - 1) AS i
WITH feature, SkiLift, i, coordinates[i] AS coord
MERGE (p:Point {id: apoc.text.join(toStringList(coord), ";")})
  SET p.lat = coord[0],
      p.lon = coord[1],
      p.ele = coord[2],
      p.lsequence = i,
      p.LiftTop = CASE i WHEN(size(feature.geometry.coordinates)-1) THEN "Top" WHEN(0) THEN "Bottom" ELSE "Middle" END
MERGE (SkiLift)-[:HAS_POINT]->(p)
WITH SkiLift, p ORDER BY p.sequence
WITH SkiLift, collect(p) AS pointList
CALL apoc.nodes.link(pointList, 'SEGMENT', {avoidDuplicates: true})
RETURN SkiLift.name, size(pointList) AS pointsLinked
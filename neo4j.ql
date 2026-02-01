//Create Ski Areas:
CALL apoc.periodic.iterate(
    // The "outer" query that reads the data
    'CALL apoc.load.json("https://tiles.openskimap.org/geojson/ski_areas.geojson", "$.features") YIELD value',

    // The "inner" query that processes each batch of data
    'UNWIND value as feature
    CREATE (s:SkiArea)
    SET s.id = feature.properties.id,
        s.type = feature.properties.type,
        s.name = feature.properties.name,
        s.maxElevation = feature.statistics.maxElevation,
        s.minElevation = feature.statistics.minElevation
    ',

    // Configuration for the iteration
    {batchSize: 100, parallel: false}
) YIELD batches, total RETURN batches, total;



//Create Ski Runs:
UNWIND range(0,9) AS chunk
CALL apoc.load.json("https://tiles.openskimap.org/geojson/runs.geojson", "$.features[?(@.id % 10 == " + chunk + ")]") YIELD value
UNWIND value as feature
CREATE (r:SkiRun)
SET r.id = feature.properties.id,
    r.type = feature.properties.type,
    r.name = feature.properties.name,
    r.difficulty = feature.properties.difficulty,
    r.difficultyConvention = feature.properties.difficultyConvention,
    r.oneway = feature.properties.oneway,
    r.gladed = feature.properties.gladed,
    r.patrolled = feature.properties.patrolled,
    r.lit = feature.properties.lit,
    r.grooming = feature.properties.grooming,
    r.status = feature.properties.status


WITH r, feature.properties.uses as uses
UNWIND uses as use
CREATE (ru:runUse)
SET ru.id = feature.properties.id,
    ru.use = use

UNWIND feature.properties.skiAreas as skiArea
CREATE (sr:SkiAreaSkiRun)
SET sr.runId = feature.properties.id,
    sr.areaId = skiArea.properties.id
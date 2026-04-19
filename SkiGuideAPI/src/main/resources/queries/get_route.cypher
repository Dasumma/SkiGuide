// 1. Clear any existing projection
CALL gds.graph.drop('skiMap', false)
YIELD nodeCount
WITH nodeCount as droppedNodeCount

// 2. Project both SEGMENT and CONNECTION
CALL gds.graph.project(
  'skiMap',
  'Point',
  {
    SEGMENT: { orientation: 'NATURAL' },
    CONNECTION: { orientation: 'NATURAL' }
  },
  {
    relationshipProperties: 'distance'
  }
) 
YIELD nodeCount
WITH nodeCount

// Find a path from a Lift's top to a Run's end
MATCH (l:SkiRun {name: "Thunderbolt"})-[:HAS_ENTRY]->(start:Point)
MATCH (r:SkiRun {name: "Turkey Turn"})-[:HAS_EXIT]->(end:Point)

CALL gds.shortestPath.dijkstra.stream('skiMap', {
    sourceNode: start,
    targetNode: end,
    relationshipWeightProperty: 'distance'
})
YIELD nodeIds, totalCost
RETURN [nodeId in nodeIds | [gds.util.asNode(nodeId).lat,  gds.util.asNode(nodeId).lon]] AS coords,
       totalCost AS distanceMeters;
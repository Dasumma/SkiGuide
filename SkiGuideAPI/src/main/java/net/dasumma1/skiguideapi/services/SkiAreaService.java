package net.dasumma1.skiguideapi.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiPoint;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunPreferencesRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunRequest;

/**
 * Service class responsible for managing ski area data and synchronizing 
 * information between the Neo4j graph database and the SPARQL RDF store.
 * It provides functionality to identify discrepancies between data sources 
 * and perform route calculations based on user preferences.
 */
@Service
public class SkiAreaService {

    /** Service for interacting with the Neo4j database. */
    private final NeoService neoService;
    
    /** Service for interacting with the SPARQL endpoint. */
    private final SparqlService sparqlService;

    /**
     * Constructs a new SkiAreaService with the required data services.
     * @param neoService the service used for Neo4j operations
     * @param sparqlService the service used for SPARQL/RDF operations
     */
    public SkiAreaService(NeoService neoService, SparqlService sparqlService) {
        this.neoService = neoService;
        this.sparqlService = sparqlService;
    }

    /**
     * Identifies ski areas that exist in the Neo4j database but are missing from the SPARQL store.
     * @return a Map where the key is the area ID and the value is the area name
     */
    public Map<String, String> findAreasInNeoNotInSparql() {
        var neoSkiAreas = neoService.getAllSkiAreas();
        var sparqlSkiAreas = sparqlService.getSparqlSkiAreas();

        Map<String, String> result = new HashMap<>();
        for (var neoArea : neoSkiAreas) {
            if (!sparqlSkiAreas.containsKey(neoArea.getId())) {
                result.put(neoArea.getId(), neoArea.getName());
            }
        }
        return result;
    }

    /**
     * Identifies ski areas that exist in the SPARQL store but are missing from the Neo4j database.
     * @return a Map where the key is the area ID and the value is the area name
     */
    public Map<String, String> findAreasInSparqlNotInNeo() {
        var neoSkiAreas = neoService.getAllSkiAreas();
        var sparqlSkiAreas = sparqlService.getSparqlSkiAreas();

        Map<String, String> result = new HashMap<>();
        sparqlSkiAreas.forEach((key, value) -> {
            if (!neoSkiAreas.stream().anyMatch(neoArea -> neoArea.getId().equals(key))) {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * Synchronizes missing ski areas by fetching them from Neo4j and creating them in the SPARQL store.
     * @return a confirmation string listing the areas added to SPARQL
     */
    public String addMissingAreasToSparql() {
        Map<String, String> missingAreas = findAreasInNeoNotInSparql();
        missingAreas.forEach((areaId, areaName) -> {
            NeoSkiArea neoArea = neoService.getSkiAreaById(areaId);
            
            var request = new net.dasumma1.skiguideapi.request_objects.RdfSkiAreaRequest();
            request.setId(neoArea.getId());
            request.setName(neoArea.getName());

            sparqlService.createSparqlSkiArea(request);
        });
        return "Missing areas added to SPARQL: " + missingAreas.toString();
    }

    /**
     * Identifies ski runs that exist in the Neo4j database but are missing from the SPARQL store.
     * @return a Map where the key is the run ID and the value is the run name
     */
    public Map<String, String> findRunsInNeoNotInSparql() {
        var neoSkiRuns = neoService.getAllSkiRuns();
        var sparqlSkiRuns = sparqlService.getSparqlSkiRuns();

        Map<String, String> result = new HashMap<>();
        for (var neoRun : neoSkiRuns) {
            if (!sparqlSkiRuns.stream().anyMatch(sparqlRun -> sparqlRun.getId().equals(neoRun.getId()))) {
                result.put(neoRun.getId(), neoRun.getName());
            }
        }
        return result;
    }
    
    /**
     * Identifies ski runs that exist in the SPARQL store but are missing from the Neo4j database.
     * @return a Map where the key is the run ID and the value is the run name
     */
    public Map<String, String> findRunsInSparqlNotInNeo() {
        var neoSkiRuns = neoService.getAllSkiRuns();
        var sparqlSkiRuns = sparqlService.getSparqlSkiRuns();

        Map<String, String> result = new HashMap<>();
        sparqlSkiRuns.forEach((sparqlRun) -> {
            if (!neoSkiRuns.stream().anyMatch(neoRun -> neoRun.getId().equals(sparqlRun.getId()))) {
                result.put(sparqlRun.getId(), sparqlRun.getName());
            }
        });
        return result;
    }

    /**
     * Synchronizes missing ski runs by fetching their detailed attributes from Neo4j 
     * and creating the corresponding RDF entries in the SPARQL store.
     * @return a confirmation string listing the runs added to SPARQL
     */
    public String addMissingRunsToSparql() {
        Map<String, String> missingRuns = findRunsInNeoNotInSparql();
        missingRuns.forEach((runId, runName) -> {
            NeoSkiRun neoRun = neoService.getSkiRunById(runId);
            
            var request = new RdfSkiRunRequest(
                neoRun.getId(),
                neoRun.getName(),
                SparqlService.getDifficultyWeight(neoRun.getDifficulty()),
                "classic".equals(neoRun.getGrooming()),
                neoRun.getPatrolled(),
                neoRun.getSnowmaking(),
                neoRun.getOneway(),
                neoRun.getLit(),
                neoRun.getGladed()
            );

            List<NeoSkiArea> areas = neoService.getSkiAreaByFeatureId(runId);
            List<String> areaIds = areas.stream().map(NeoSkiArea::getId).toList();
            Logger.getLogger(SkiAreaService.class.getName()).info("Associating run " + runId + " with areas: " + areaIds.toString());

            sparqlService.createSparqlSkiRun(request, areaIds);
        });
        return "Missing runs added to SPARQL: " + missingRuns.toString();
    }
    
    /**
     * Identifies ski lifts that exist in the Neo4j database but are missing from the SPARQL store.
     * @return a Map where the key is the lift ID and the value is the lift name
     */
    public Map<String, String> findLiftsInNeoNotInSparql() {
        var neoSkiLifts = neoService.getAllSkiLifts();
        var sparqlSkiLifts = sparqlService.getSparqlSkiLifts();

        Map<String, String> result = new HashMap<>();
        for (var neoLift : neoSkiLifts) {
            if (!sparqlSkiLifts.containsKey(neoLift.getId())) {
                result.put(neoLift.getId(), neoLift.getName());
            }
        }
        return result;
    }

    /**
     * Identifies ski lifts that exist in the SPARQL store but are missing from the Neo4j database.
     * @return a Map where the key is the lift ID and the value is the lift name
     */
    public Map<String, String> findLiftsInSparqlNotInNeo() {
        var neoSkiLifts = neoService.getAllSkiLifts();
        var sparqlSkiLifts = sparqlService.getSparqlSkiLifts();

        Map<String, String> result = new HashMap<>();
        sparqlSkiLifts.forEach((key, value) -> {
            if (!neoSkiLifts.stream().anyMatch(neoLift -> neoLift.getId().equals(key))) {
                result.put(key, value);
            }
        });
        return result;
    }

    /**
     * Synchronizes missing ski lifts by fetching their metadata from Neo4j 
     * and creating the corresponding RDF entries in the SPARQL store.
     * @return a confirmation string listing the lifts added to SPARQL
     */
    public String addMissingLiftsToSparql() {
        Map<String, String> missingLifts = findLiftsInNeoNotInSparql();
        missingLifts.forEach((liftId, liftName) -> {
            NeoSkiLift neoLift = neoService.getSkiLiftById(liftId);
            
            var request = new net.dasumma1.skiguideapi.request_objects.RdfSkiLiftRequest();
            request.setId(neoLift.getId());
            request.setName(neoLift.getName());
            request.setType(neoLift.getType());
            request.setAreaId(neoLift.getAreaId());

            List<NeoSkiArea> areas = neoService.getSkiAreaByFeatureId(liftId);
            List<String> areaIds = areas.stream().map(NeoSkiArea::getId).toList();

            sparqlService.createSparqlSkiLift(request, areaIds);
        });
        return "Missing lifts added to SPARQL: " + missingLifts.toString();
    }

    /**
     * Calculates the optimal route between two points by filtering available runs 
     * through the SPARQL service based on user preferences and then performing 
     * a graph search in Neo4j.
     * @param request the user's run preferences (difficulty, grooming, etc.)
     * @param start the starting geographical Point
     * @param end the destination geographical Point
     * @return a string representation of the found route
     */
    public String getBestRouteUsingPriorityRuns(RdfSkiRunPreferencesRequest request, NeoSkiPoint start, NeoSkiPoint end) {
        var filteredRuns = sparqlService.getFilteredSkiRuns(request);
        Logger.getLogger(SkiAreaService.class.getName()).info("Filtered runs based on preferences: " + filteredRuns.toString());
        var findRoute = neoService.findRoute(filteredRuns, start, end);
        Logger.getLogger(SkiAreaService.class.getName()).info("Found route: " + findRoute.toString());

        return findRoute.toString();
    }
}
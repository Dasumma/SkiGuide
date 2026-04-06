package net.dasumma1.skiguideapi.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import net.dasumma1.skiguideapi.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunPreferencesRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunRequest;

@Service
public class SkiAreaService {

    private final NeoService neoService;
    private final SparqlService sparqlService;

    public SkiAreaService(NeoService neoService, SparqlService sparqlService) {
        this.neoService = neoService;
        this.sparqlService = sparqlService;
    }

    public Map<String, String> findAreasInNeoNotInSparql() {
        // Get ski areas from Neo4j
        var neoSkiAreas = neoService.getAllSkiAreas();

        // Get ski areas from SPARQL
        var sparqlSkiAreas = sparqlService.getSparqlSkiAreas();

        // Compare and find areas in Neo4j not in SPARQL
        Map<String, String> result = new HashMap<>();
        for (var neoArea : neoSkiAreas) {
            if (!sparqlSkiAreas.containsKey(neoArea.getId())) {
                result.put(neoArea.getId(), neoArea.getName());
            }
        }
        return result;
    }

    public Map<String, String> findAreasInSparqlNotInNeo() {
        // Get ski areas from Neo4j
        var neoSkiAreas = neoService.getAllSkiAreas();

        // Get ski areas from SPARQL
        var sparqlSkiAreas = sparqlService.getSparqlSkiAreas();

        // Compare and find areas in SPARQL not in Neo4j
        Map<String, String> result = new HashMap<>();
        sparqlSkiAreas.forEach((key, value) -> {
            if (!neoSkiAreas.stream().anyMatch(neoArea -> neoArea.getId().equals(key))) {
                result.put(key, value);
            }
        });
        return result;
    }

    public String addMissingAreasToSparql() {
        Map<String, String> missingAreas = findAreasInNeoNotInSparql();
        missingAreas.forEach((areaId, areaName) -> {
            // Get SkiArea Details from Neo4j
            NeoSkiArea neoArea = neoService.getSkiAreaById(areaId);
            
            // Map NeoSkiArea to RdfSkiAreaRequest
            var request = new net.dasumma1.skiguideapi.rdf_objects.RdfSkiAreaRequest();
            request.setId(neoArea.getId());
            request.setName(neoArea.getName());

            // Create a new ski area in SPARQL for each missing area
            sparqlService.createSparqlSkiArea(request);
        });
        return "Missing areas added to SPARQL: " + missingAreas.toString();
    }

    public Map<String, String> findRunsInNeoNotInSparql() {
        // Get ski runs from Neo4j
        var neoSkiRuns = neoService.getAllSkiRuns();

        // Get ski runs from SPARQL
        var sparqlSkiRuns = sparqlService.getSparqlSkiRuns();

        // Compare and find runs in Neo4j not in SPARQL
        Map<String, String> result = new HashMap<>();
        for (var neoRun : neoSkiRuns) {
            if (!sparqlSkiRuns.stream().anyMatch(sparqlRun -> sparqlRun.getId().equals(neoRun.getId()))) {
                result.put(neoRun.getId(), neoRun.getName());
            }
        }
        return result;
    }
    
    public Map<String, String> findRunsInSparqlNotInNeo() {
        // Get ski runs from Neo4j
        var neoSkiRuns = neoService.getAllSkiRuns();

        // Get ski runs from SPARQL
        var sparqlSkiRuns = sparqlService.getSparqlSkiRuns();

        // Compare and find runs in SPARQL not in Neo4j
        Map<String, String> result = new HashMap<>();
        sparqlSkiRuns.forEach((sparqlRun) -> {
            if (!neoSkiRuns.stream().anyMatch(neoRun -> neoRun.getId().equals(sparqlRun.getId()))) {
                result.put(sparqlRun.getId(), sparqlRun.getName());
            }
        });
        return result;
    }

    public String addMissingRunsToSparql() {
        Map<String, String> missingRuns = findRunsInNeoNotInSparql();
        missingRuns.forEach((runId, runName) -> {
            // Get SkiRun Details from Neo4j
            NeoSkiRun neoRun = neoService.getSkiRunById(runId);
            
            // Map NeoSkiRun to RdfSkiRunRequest
            var request = new RdfSkiRunRequest(
                neoRun.getId(),
                neoRun.getName(),
                SparqlService.getDifficultyWeight(neoRun.getDifficulty()),
                neoRun.getGrooming() == "classic" ? true : false,
                neoRun.getPatrolled(),
                neoRun.getSnowmaking(),
                neoRun.getOneway(),
                neoRun.getLit(),
                neoRun.getGladed()
            );

            List<NeoSkiArea> areas = neoService.getSkiAreaBySkiRunId(runId);
            List<String> areaIds = areas.stream().map(NeoSkiArea::getId).toList();

            // Create a new ski run in SPARQL for each missing run
            sparqlService.createSparqlSkiRun(request, areaIds);
        });
        return "Missing runs added to SPARQL: " + missingRuns.toString();
    }
    
    public Map<String, String> findLiftsInNeoNotInSparql() {
        // Get ski lifts from Neo4j
        var neoSkiLifts = neoService.getAllSkiLifts();

        // Get ski lifts from SPARQL
        var sparqlSkiLifts = sparqlService.getSparqlSkiLifts();

        // Compare and find lifts in Neo4j not in SPARQL
        Map<String, String> result = new HashMap<>();
        for (var neoLift : neoSkiLifts) {
            if (!sparqlSkiLifts.containsKey(neoLift.getId())) {
                result.put(neoLift.getId(), neoLift.getName());
            }
        }
        return result;
    }

    public Map<String, String> findLiftsInSparqlNotInNeo() {

        // Get ski lifts from Neo4j
        var neoSkiLifts = neoService.getAllSkiLifts();

        // Get ski lifts from SPARQL
        var sparqlSkiLifts = sparqlService.getSparqlSkiLifts();

        // Compare and find lifts in SPARQL not in Neo4j
        Map<String, String> result = new HashMap<>();
        sparqlSkiLifts.forEach((key, value) -> {
            if (!neoSkiLifts.stream().anyMatch(neoLift -> neoLift.getId().equals(key))) {
                result.put(key, value);
            }
        });
        return result;
    }

    public String addMissingLiftsToSparql() {
        Map<String, String> missingLifts = findLiftsInNeoNotInSparql();
        missingLifts.forEach((liftId, liftName) -> {
            // Get SkiLift Details from Neo4j
            NeoSkiLift neoLift = neoService.getSkiLiftById(liftId);
            
            // Map NeoSkiLift to RdfSkiLiftRequest
            var request = new net.dasumma1.skiguideapi.rdf_objects.RdfSkiLiftRequest();
            request.setId(neoLift.getId());
            request.setName(neoLift.getName());
            request.setType(neoLift.getType());
            request.setAreaId(neoLift.getAreaId());

            // Create a new ski lift in SPARQL for each missing lift
            sparqlService.createSparqlSkiLift(request);
        });
        return "Missing lifts added to SPARQL: " + missingLifts.toString();
    }

    public String getBestRouteUsingPriorityRuns(RdfSkiRunPreferencesRequest request, Point start, Point end) {
        var filteredRuns = sparqlService.getFilteredSkiRuns(request);
        var findRoute = neoService.findRoute(filteredRuns, start, end);

        return "Best route using priority runs: " + findRoute.toString();
    }
}
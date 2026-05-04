package net.dasumma1.skiguideapi.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.area_objects.GetBestRouteUsingPriorityRunsRequest;
import net.dasumma1.skiguideapi.services.SkiAreaService;

/**
 * REST Controller for managing cross-database synchronization and advanced routing.
 * <p>
 * This controller provides endpoints to identify data discrepancies between the 
 * Neo4j graph database and the SPARQL triplestore, as well as tools to synchronize 
 * them and calculate optimal ski routes based on user-defined priorities.
 */
@RestController
@RequestMapping("/api")
public class SkiAreaController {

    /** The service layer handling the business logic for ski areas. */
    private final SkiAreaService skiAreaService;

    /**
     * Constructs the controller with the required SkiAreaService.
     * @param skiAreaService the service used for cross-store data management
     */
    public SkiAreaController(SkiAreaService skiAreaService) {
        this.skiAreaService = skiAreaService;
    }

    /**
     * Identifies ski areas present in Neo4j but missing from SPARQL.
     * @return a Map of missing area IDs to names
     */
    @GetMapping("/find-areas-in-neo-not-in-sparql")
    public Map<String, String> findAreasInNeoNotInSparql(){
        return skiAreaService.findAreasInNeoNotInSparql();
    }

    /**
     * Identifies ski areas present in SPARQL but missing from Neo4j.
     * @return a Map of missing area IDs to names
     */
    @GetMapping("/find-areas-in-sparql-not-in-neo")
    public Map<String, String> findAreasInSparqlNotInNeo(){
        return skiAreaService.findAreasInSparqlNotInNeo();
    }

    /**
     * Synchronizes missing ski areas from Neo4j into the SPARQL store.
     * @return a status message confirming the additions
     */
    @GetMapping("/add-missing-areas-to-sparql")
    public String addMissingAreasToSparql(){
        return skiAreaService.addMissingAreasToSparql();
    }

    /**
     * Identifies ski runs present in Neo4j but missing from SPARQL.
     * @return a Map of missing run IDs to names
     */
    @GetMapping("/find-runs-in-neo-not-in-sparql")
    public Map<String, String> findRunsInNeoNotInSparql(){
        return skiAreaService.findRunsInNeoNotInSparql();
    }

    /**
     * Identifies ski runs present in SPARQL but missing from Neo4j.
     * @return a Map of missing run IDs to names
     */
    @GetMapping("/find-runs-in-sparql-not-in-neo")
    public Map<String, String> findRunsInSparqlNotInNeo(){
        return skiAreaService.findRunsInSparqlNotInNeo();
    }

    /**
     * Synchronizes missing ski runs from Neo4j into the SPARQL store.
     * @return a status message confirming the additions
     */
    @GetMapping("/add-missing-runs-to-sparql")
    public String addMissingRunsToSparql(){
        return skiAreaService.addMissingRunsToSparql();
    }

    /**
     * Identifies ski lifts present in Neo4j but missing from SPARQL.
     * @return a Map of missing lift IDs to names
     */
    @GetMapping("/find-lifts-in-neo-not-in-sparql")
    public Map<String, String> findLiftsInNeoNotInSparql(){ 
        return skiAreaService.findLiftsInNeoNotInSparql();
    }   

    /**
     * Identifies ski lifts present in SPARQL but missing from Neo4j.
     * @return a Map of missing lift IDs to names
     */
    @GetMapping("/find-lifts-in-sparql-not-in-neo")
    public Map<String, String> findLiftsInSparqlNotInNeo(){         
        return skiAreaService.findLiftsInSparqlNotInNeo();
    }
    
    /**
     * Synchronizes missing ski lifts from Neo4j into the SPARQL store.
     * @return a status message confirming the additions
     */
    @GetMapping("/add-missing-lifts-to-sparql")
    public String addMissingLiftsToSparql(){
        return skiAreaService.addMissingLiftsToSparql();
    }

    /**
     * Calculates the best route between two points using a weighted priority 
     * system for run characteristics (difficulty, grooming, etc.).
     * @param request the routing request containing user preferences, start point, and end point
     * @return a string representation (typically JSON) of the calculated route result
     */
    @PostMapping("/get-best-route-using-priority-runs")
    public String getBestRouteUsingPriorityRuns(@org.springframework.web.bind.annotation.RequestBody GetBestRouteUsingPriorityRunsRequest request) {
        return skiAreaService.getBestRouteUsingPriorityRuns(request.getPreferences(), request.getStart(), request.getEnd());
    }
}
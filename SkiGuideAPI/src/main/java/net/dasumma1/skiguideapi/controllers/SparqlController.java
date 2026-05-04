package net.dasumma1.skiguideapi.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.request_objects.RdfSkiAreaRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiLiftRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunPreferencesRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunRequest;
import net.dasumma1.skiguideapi.services.SparqlService;

/**
 * REST Controller for SPARQL-based operations.
 * <p>
 * This controller exposes endpoints to interact with the RDF triplestore (Apache Jena Fuseki).
 * It supports CRUD operations for ski areas, runs, and lifts within the semantic web 
 * ontology and provides preference-based filtering logic.
 */
@RestController
@RequestMapping("/api/sparql")
public class SparqlController {

    /** The service layer handling SPARQL query construction and execution. */
    private final SparqlService sparqlService;

    /**
     * Constructs the controller with the required SparqlService.
     * @param sparqlService the service used for RDF triplestore interactions
     */
    public SparqlController(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    /**
     * Retrieves all ski areas currently defined in the RDF triplestore.
     * @return a Map where the keys are area URIs/IDs and values are the area names
     */
    @GetMapping("/sparql-ski-areas")
    public Map<String, String> getSparqlSkiAreas() {
        return sparqlService.getSparqlSkiAreas();
    }

    /**
     * Creates a new ski area entry in the RDF triplestore.
     * @param request the data required to create a new semantic ski area
     * @return a confirmation message indicating the result of the insertion
     */
    @PostMapping("/sparql-ski-areas")
    public String createSparqlSkiArea(@org.springframework.web.bind.annotation.RequestBody RdfSkiAreaRequest request) {
        return sparqlService.createSparqlSkiArea(request);
    }

    /**
     * Retrieves a list of all ski runs stored in the RDF triplestore with their attributes.
     * @return a list of {@link RdfSkiRunRequest} objects representing the runs
     */
    @GetMapping("/sparql-ski-runs")
    public List<RdfSkiRunRequest> getSparqlSkiRuns() {
        return sparqlService.getSparqlSkiRuns();
    }

    /**
     * Creates a new ski run entry in the RDF triplestore.
     * @param request the data required to create a new semantic ski run
     * @return a confirmation message indicating the result of the insertion
     */
    @PostMapping("/sparql-ski-runs")
    public String createSparqlSkiRun(@org.springframework.web.bind.annotation.RequestBody RdfSkiRunRequest request) {
        return sparqlService.createSparqlSkiRun(request, null);
    }

    /**
     * Retrieves all ski lifts currently defined in the RDF triplestore.
     * @return a Map where the keys are lift URIs/IDs and values are the lift names
     */
    @GetMapping("/sparql-ski-lifts")
    public Map<String, String> getSparqlSkiLifts() {
        return sparqlService.getSparqlSkiLifts();
    }

    /**
     * Creates a new ski lift entry in the RDF triplestore.
     * @param request the data required to create a new semantic ski lift
     * @return a confirmation message indicating the result of the insertion
     */
    @PostMapping("/sparql-ski-lifts")
    public String createSparqlSkiLift(@org.springframework.web.bind.annotation.RequestBody RdfSkiLiftRequest request) {
        return sparqlService.createSparqlSkiLift(request, null);
    }

    /**
     * Filters ski runs based on user preferences using a weighted scoring system 
     * executed via a SPARQL query.
     * @param preferences the weighted criteria (difficulty, grooming, lighting, etc.)
     * @return a list of ski run IDs ordered by their suitability score
     */
    @PostMapping("/get-filtered-ski-runs")
    public List<String> getFilteredSkiRuns(@org.springframework.web.bind.annotation.RequestBody RdfSkiRunPreferencesRequest preferences) {
        return sparqlService.getFilteredSkiRuns(preferences);
    }
}
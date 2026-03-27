package net.dasumma1.skiguideapi.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.rdf_objects.RdfSkiAreaRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiLiftRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunRequest;
import net.dasumma1.skiguideapi.services.SparqlService;

@RestController
@RequestMapping("/api/sparql")
public class SparqlController {

    private final SparqlService sparqlService;

    public SparqlController(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    // SPARQL-based endpoints
    @GetMapping("/sparql-ski-areas")
    public Map<String, String> getSparqlSkiAreas() {
        return sparqlService.getSparqlSkiAreas();
    }

    @PostMapping("/sparql-ski-areas")
    public String createSparqlSkiArea(@org.springframework.web.bind.annotation.RequestBody RdfSkiAreaRequest request) {
        return sparqlService.createSparqlSkiArea(request);
    }

    @GetMapping("/sparql-ski-runs")
    public List<RdfSkiRunRequest> getSparqlSkiRuns() {
        return sparqlService.getSparqlSkiRuns();
    }

    @PostMapping("/sparql-ski-runs")
    public String createSparqlSkiRun(@org.springframework.web.bind.annotation.RequestBody RdfSkiRunRequest request) {
        return sparqlService.createSparqlSkiRun(request, null);
    }

    @GetMapping("/sparql-ski-lifts")
    public Map<String, String> getSparqlSkiLifts() {
        return sparqlService.getSparqlSkiLifts();
    }

    @PostMapping("/sparql-ski-lifts")
    public String createSparqlSkiLift(@org.springframework.web.bind.annotation.RequestBody RdfSkiLiftRequest request) {
        return sparqlService.createSparqlSkiLift(request);
    }

    @PostMapping("/get-filtered-ski-runs")
    public List<String> getFilteredSkiRuns(@org.springframework.web.bind.annotation.RequestBody RdfSkiRunRequest preferences) {
        return sparqlService.getFilteredSkiRuns(preferences);
    }
}

package net.dasumma1.skiguideapi.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiPoint;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.GeoJSONFeature.GeoJSONFeatureRequest.GeoJSONFeatureCollection;
import net.dasumma1.skiguideapi.services.NeoService;

/**
 * REST Controller for Neo4j-specific operations.
 * <p>
 * Provides endpoints to manage and query the graph representation of ski infrastructure,
 * including areas, runs, and lifts, as well as spatial analysis features.
 */
@RestController
@RequestMapping("/api/neo")
public class NeoController {

    /** The service layer for Neo4j graph operations. */
    private final NeoService neoService;

    /**
     * Constructs the controller with the required NeoService.
     * @param neoService the service used for graph database interactions
     */
    public NeoController(NeoService neoService) {
        this.neoService = neoService;
    }

    /**
     * Retrieves all ski areas stored in the Neo4j database.
     * @return a list of all {@link NeoSkiArea} entities
     */
    @GetMapping("/ski-areas")
    public List<NeoSkiArea> getNeoSkiAreas() {
        return neoService.getAllSkiAreas();
    }

    /**
     * Creates a new ski area node in the graph.
     * @param skiArea the ski area data to persist
     * @return the created {@link NeoSkiArea} entity
     */
    @PostMapping("/ski-areas")
    public NeoSkiArea createSkiArea(@RequestBody NeoSkiArea skiArea) {
        return neoService.createSkiArea(skiArea);
    }

    /**
     * Retrieves all ski runs stored in the Neo4j database.
     * @return a list of all {@link NeoSkiRun} entities
     */
    @GetMapping("/ski-runs")
    public List<NeoSkiRun> getNeoSkiRuns() {
        return neoService.getAllSkiRuns();
    }

    /**
     * Creates a new ski run node in the graph.
     * @param skiRun the ski run data to persist
     * @return the created {@link NeoSkiRun} entity
     */
    @PostMapping("/ski-runs")
    public NeoSkiRun createSkiRun(@RequestBody NeoSkiRun skiRun) {
        return neoService.createSkiRun(skiRun);
    }

    /**
     * Retrieves all ski lifts stored in the Neo4j database.
     * @return a list of all {@link NeoSkiLift} entities
     */
    @GetMapping("/ski-lifts")
    public List<NeoSkiLift> getNeoSkiLifts() {
        return neoService.getAllSkiLifts();
    }

    /**
     * Creates a new ski lift node in the graph.
     * @param skiLift the ski lift data to persist
     * @return the created {@link NeoSkiLift} entity
     */
    @PostMapping("/ski-lifts")
    public NeoSkiLift createSkiLift(@RequestBody NeoSkiLift skiLift) {
        return neoService.createSkiLift(skiLift);
    }

    /**
     * Performs a spatial query to find the ski area closest to a specific point.
     * @param point the geographical coordinates (latitude/longitude) to search from
     * @return the {@link NeoSkiArea} node nearest to the provided point
     */
    @PostMapping("/get-closest-ski-area")
    public NeoSkiArea getClosestSkiArea(@RequestBody NeoSkiPoint point) {
        return neoService.getClosestSkiArea(point);
    }

    @PostMapping("/get-ski-area-by-name")
    public NeoSkiArea getSkiAreaByName(@RequestBody String name) {
        return neoService.getSkiAreaByName(name);
    }

    /**
     * Facilitates bulk creation of ski infrastructure by processing raw JSON strings.
     * @param request a request object containing JSON strings for an area, its runs, and its lifts
     */
    /*
    @PostMapping("/create-ski-area-using-json")
    public void createSkiAreaUsingJson(@RequestBody CreateSkiAreaUsingJsonRequest request) {
        neoService.createSkiAreaUsingJson(request.getSkiAreaJson(), request.getSkiRunsJson(), request.getSkiLiftsJson());
    }*/
    
    /**
     * Finds the closest point in the graph database via the provided coordinates. Used to find the nearest point or ski area.
     * @param point the geographical coordinates (latitude/longitude) to search from
     * @return the {@link NeoSkiPoint} node nearest to the provided coordinates
     */
    @PostMapping("/get-closest-point")
    public NeoSkiPoint getClosestPoint(@RequestBody NeoSkiPoint point) {
        Logger.getLogger(NeoController.class.getName()).info("Received request for closest point to (" + point.getLon() + ", " + point.getLat() + ")");
        return neoService.getClosestPoint(point);
    }

    /**
     * Retrieves ski runs for the provided ski area and returns them in GeoJSON format for mapping purposes.
     * @param areaId
     * @return {@link GeoJSONFeatureCollection} containing the ski runs to be returned as a GeoJSON object by Spring Boot
     */
    @GetMapping("/ski-areas/{areaId}/ski-runs/geojson")
    public GeoJSONFeatureCollection getSkiRunsGeoJSON(@PathVariable String areaId) {
        return neoService.getSkiRunsGeoJSON(areaId); 
    }
    
    /**
     * Retrieves ski lifts for the provided ski area and returns them in GeoJSON format for mapping purposes.
     * @param areaId
     * @return {@link GeoJSONFeatureCollection} containing the ski lifts to be returned as a GeoJSON object by Spring Boot
     */
    @GetMapping("/ski-areas/{areaId}/ski-lifts/geojson")
    public GeoJSONFeatureCollection getSkiLiftsGeoJSON(@PathVariable String areaId) {
        return neoService.getSkiLiftsGeoJSON(areaId);
    }

}
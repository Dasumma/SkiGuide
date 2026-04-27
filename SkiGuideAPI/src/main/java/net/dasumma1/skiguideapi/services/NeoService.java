package net.dasumma1.skiguideapi.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import net.dasumma1.skiguideapi.area_objects.Point;
import net.dasumma1.skiguideapi.neo_repositories.SkiAreaRepository;
import net.dasumma1.skiguideapi.neo_repositories.SkiLiftRepository;
import net.dasumma1.skiguideapi.neo_repositories.SkiRunRepository;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiPoint;
import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiRun;

@Service
public class NeoService {

    private final SkiAreaRepository skiAreaRepository;
    private final SkiRunRepository skiRunRepository;
    private final SkiLiftRepository skiLiftRepository;

    /**
     * Constructs a NeoService with the required Neo4j repositories.
     *
     * @param skiAreaRepository repository for ski area nodes
     * @param skiRunRepository repository for ski run nodes
     * @param skiLiftRepository repository for ski lift nodes
     */
    public NeoService(SkiAreaRepository skiAreaRepository, SkiRunRepository skiRunRepository, SkiLiftRepository skiLiftRepository) {
        this.skiAreaRepository = skiAreaRepository;
        this.skiRunRepository = skiRunRepository;
        this.skiLiftRepository = skiLiftRepository;
    }

    // Neo4j-based operations
    /**
     * Retrieves all ski areas from the Neo4j repository.
     *
     * @return list of all NeoSkiArea objects
     */
    public List<NeoSkiArea> getAllSkiAreas() {
        return skiAreaRepository.findAll();
    }

    /**
     * Retrieves a ski area by its identifier.
     *
     * @param areaId identifier of the ski area
     * @return the NeoSkiArea object if found, otherwise null
     */
    public NeoSkiArea getSkiAreaById(String areaId) {
        return skiAreaRepository.findById(areaId).orElse(null);
    }

    /**
     * Persists a ski area node in Neo4j.
     *
     * @param skiArea ski area object to save
     * @return saved NeoSkiArea object
     */
    public NeoSkiArea createSkiArea(NeoSkiArea skiArea) {
        return skiAreaRepository.save(skiArea);
    }

    /**
     * Retrieves all ski runs from the Neo4j repository.
     *
     * @return list of all NeoSkiRun objects
     */
    public List<NeoSkiRun> getAllSkiRuns() {
        return skiRunRepository.findAll();
    }

    /**
     * Retrieves a ski run by its identifier.
     *
     * @param runId identifier of the ski run
     * @return the NeoSkiRun object if found, otherwise null
     */
    public NeoSkiRun getSkiRunById(String runId) {
        return skiRunRepository.findById(runId).orElse(null);
    }

    /**
     * Persists a ski run node in Neo4j.
     *
     * @param skiRun ski run object to save
     * @return saved NeoSkiRun object
     */
    public NeoSkiRun createSkiRun(NeoSkiRun skiRun) {
        return skiRunRepository.save(skiRun);
    }

    /**
     * Retrieves all ski lifts from the Neo4j repository.
     *
     * @return list of all NeoSkiLift objects
     */
    public List<NeoSkiLift> getAllSkiLifts() {
        return skiLiftRepository.findAll();
    }

    /**
     * Retrieves a ski lift by its identifier.
     *
     * @param liftId identifier of the ski lift
     * @return the NeoSkiLift object if found, otherwise null
     */
    public NeoSkiLift getSkiLiftById(String liftId) {
        return skiLiftRepository.findById(liftId).orElse(null);
    }

    /**
     * Persists a ski lift node in Neo4j.
     *
     * @param skiLift ski lift object to save
     * @return saved NeoSkiLift object
     */
    public NeoSkiLift createSkiLift(NeoSkiLift skiLift) {
        return skiLiftRepository.save(skiLift);
    }

    /**
     * Retrieves ski areas that are associated with a given ski run.
     *
     * @param runId identifier of the ski run
     * @return list of matching NeoSkiArea objects
     */
    public List<NeoSkiArea> getSkiAreaBySkiRunId(String runId) {
        return skiAreaRepository.findSkiAreaBySkiRunId(runId);
    }

    /**
     * Finds a route between two points using a filtered set of ski runs.
     * This method recreates the graph in Neo4j before requesting the path.
     *
     * @param filteredRuns list of run IDs to include in the search graph
     * @param start starting point of the route
     * @param end destination point of the route
     * @return string representation of the found route result
     */
    public String findRoute(List<String> filteredRuns, Point start, Point end) {
        skiAreaRepository.deleteGdsGraph();
        skiAreaRepository.createGdsGraph(filteredRuns);
        Logger.getLogger("NeoService").info("Points from " + start.toString() + " to " + end.toString());
        var routeResult = skiAreaRepository.getRoute(
            start.getId(),
            end.getId());

        if(routeResult == null) {
            routeResult = new net.dasumma1.skiguideapi.area_objects.RouteResult(List.of(), 0.0);
        }

        return routeResult.toString();
    }

    /**
     * Finds the closest ski area to a given point.
     *
     * @param point geographic point used as the search origin
     * @return nearest NeoSkiArea object
     */
    public NeoSkiArea getClosestSkiArea(Point point) {
        return skiAreaRepository.getClosestSkiArea(point);
    }

    /**
     * Placeholder method for creating ski area JSON data.
     * Currently this method does not perform any action.
     */
    public void createSkiAreaJson(){
        // Assuming no JSON provided, perhaps skip or handle differently
        // For now, do nothing or call with empty strings if needed
    }

    /**
     * Creates ski area, ski run, and ski lift nodes from JSON payloads.
     * It also connects runs and lifts, then calculates distances.
     *
     * @param skiAreaJson JSON text containing ski area definitions
     * @param skiRunsJson JSON text containing ski run definitions
     * @param skiLiftsJson JSON text containing ski lift definitions
     */
    public void createSkiAreaUsingJson(String skiAreaJson, String skiRunsJson, String skiLiftsJson) {
        skiAreaRepository.createSkiAreas(skiAreaJson);
        skiAreaRepository.createSkiRunsMetadata(skiRunsJson);
        skiAreaRepository.createRunPoints(skiRunsJson);
        skiAreaRepository.createRunEntriesExits(skiRunsJson);
        skiAreaRepository.createSkiLifts(skiLiftsJson);
        skiAreaRepository.connectRunsLifts();
        skiAreaRepository.calculateDistances();
    }

    public NeoSkiPoint getClosestPoint(Point point) {
        NeoSkiPoint closestPoint = skiAreaRepository.findClosestPoint(point.getLongitude(), point.getLatitude());
        return closestPoint != null ? closestPoint : new NeoSkiPoint(point.getLongitude(), point.getLatitude());
    }
}
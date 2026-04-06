package net.dasumma1.skiguideapi.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import net.dasumma1.skiguideapi.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.neo_objects.SkiAreaRepository;
import net.dasumma1.skiguideapi.neo_objects.SkiLiftRepository;
import net.dasumma1.skiguideapi.neo_objects.SkiRunRepository;

@Service
public class NeoService {

    private final SkiAreaRepository skiAreaRepository;
    private final SkiRunRepository skiRunRepository;
    private final SkiLiftRepository skiLiftRepository;

    public NeoService(SkiAreaRepository skiAreaRepository, SkiRunRepository skiRunRepository, SkiLiftRepository skiLiftRepository) {
        this.skiAreaRepository = skiAreaRepository;
        this.skiRunRepository = skiRunRepository;
        this.skiLiftRepository = skiLiftRepository;
    }

    // Neo4j-based operations
    public List<NeoSkiArea> getAllSkiAreas() {
        return skiAreaRepository.findAll();
    }

    public NeoSkiArea getSkiAreaById(String areaId) {
        return skiAreaRepository.findById(areaId).orElse(null);
    }

    public NeoSkiArea createSkiArea(NeoSkiArea skiArea) {
        return skiAreaRepository.save(skiArea);
    }

    public List<NeoSkiRun> getAllSkiRuns() {
        return skiRunRepository.findAll();
    }

    public NeoSkiRun getSkiRunById(String runId) {
        return skiRunRepository.findById(runId).orElse(null);
    }

    public NeoSkiRun createSkiRun(NeoSkiRun skiRun) {
        return skiRunRepository.save(skiRun);
    }

    public List<NeoSkiLift> getAllSkiLifts() {
        return skiLiftRepository.findAll();
    }

    public NeoSkiLift getSkiLiftById(String liftId) {
        return skiLiftRepository.findById(liftId).orElse(null);
    }

    public NeoSkiLift createSkiLift(NeoSkiLift skiLift) {
        return skiLiftRepository.save(skiLift);
    }

    public List<NeoSkiArea> getSkiAreaBySkiRunId(String runId) {
        return skiAreaRepository.findSkiAreaBySkiRunId(runId);
    }

    public String findRoute(List<String> filteredRuns, Point start, Point end) {
        skiAreaRepository.deleteGdsGraph();
        skiAreaRepository.createGdsGraph(filteredRuns.toString());
        return skiAreaRepository.customQuery().toString();
    }
}
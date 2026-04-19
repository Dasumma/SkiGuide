package net.dasumma1.skiguideapi.controllers;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.services.NeoService;

@RestController
@RequestMapping("/api/neo")
public class NeoController {

    private final NeoService neoService;

    public NeoController(NeoService neoService) {
        this.neoService = neoService;
    }

    // Neo4j-based endpoints
    @GetMapping("/ski-areas")
    public List<NeoSkiArea> getNeoSkiAreas() {
        return neoService.getAllSkiAreas();
    }

    @PostMapping("/ski-areas")
    public NeoSkiArea createSkiArea(@RequestBody NeoSkiArea skiArea) {
        return neoService.createSkiArea(skiArea);
    }

    @GetMapping("/ski-runs")
    public List<NeoSkiRun> getNeoSkiRuns() {
        return neoService.getAllSkiRuns();
    }

    @PostMapping("/ski-runs")
    public NeoSkiRun createSkiRun(@RequestBody NeoSkiRun skiRun) {
        return neoService.createSkiRun(skiRun);
    }

    @GetMapping("/ski-lifts")
    public List<NeoSkiLift> getNeoSkiLifts() {
        return neoService.getAllSkiLifts();
    }

    @PostMapping("/ski-lifts")
    public NeoSkiLift createSkiLift(@RequestBody NeoSkiLift skiLift) {
        return neoService.createSkiLift(skiLift);
    }

    @PostMapping("/get-closest-ski-area")
    public NeoSkiArea getClosestSkiArea(@RequestBody Point point) {
        return neoService.getClosestSkiArea(point);
    }
}

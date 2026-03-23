package net.dasumma1.skiguideapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    @GetMapping("/n4j-ski-areas")
    public List<NeoSkiArea> getNeoSkiAreas() {
        return neoService.getAllSkiAreas();
    }

    @PostMapping("/n4j-ski-areas")
    public NeoSkiArea createSkiArea(@org.springframework.web.bind.annotation.RequestBody NeoSkiArea skiArea) {
        return neoService.createSkiArea(skiArea);
    }

    @GetMapping("/n4j-ski-runs")
    public List<NeoSkiRun> getNeoSkiRuns() {
        return neoService.getAllSkiRuns();
    }

    @PostMapping("/n4j-ski-runs")
    public NeoSkiRun createSkiRun(@org.springframework.web.bind.annotation.RequestBody NeoSkiRun skiRun) {
        return neoService.createSkiRun(skiRun);
    }

    @GetMapping("/n4j-ski-lifts")
    public List<NeoSkiLift> getNeoSkiLifts() {
        return neoService.getAllSkiLifts();
    }

    @PostMapping("/n4j-ski-lifts")
    public NeoSkiLift createSkiLift(@org.springframework.web.bind.annotation.RequestBody NeoSkiLift skiLift) {
        return neoService.createSkiLift(skiLift);
    }
}

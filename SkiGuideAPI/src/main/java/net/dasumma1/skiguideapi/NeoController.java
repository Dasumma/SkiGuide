package net.dasumma1.skiguideapi;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.neo_objects.NeoSkiArea;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiLift;
import net.dasumma1.skiguideapi.neo_objects.NeoSkiRun;
import net.dasumma1.skiguideapi.neo_objects.SkiAreaRepository;
import net.dasumma1.skiguideapi.neo_objects.SkiLiftRepository;
import net.dasumma1.skiguideapi.neo_objects.SkiRunRepository;

@RestController
@RequestMapping("/api/neo")
public class NeoController {
    private final SkiAreaRepository skiAreaRepository;
    private final SkiRunRepository skiRunRepository;
    private final SkiLiftRepository skiLiftRepository;
    public NeoController(SkiAreaRepository skiAreaRepository, SkiRunRepository skiRunRepository, SkiLiftRepository skiLiftRepository) {
        this.skiAreaRepository = skiAreaRepository;
        this.skiRunRepository = skiRunRepository;
        this.skiLiftRepository = skiLiftRepository;
    }
    

    // Neo4j-based endpoints
    @GetMapping("/n4j-ski-areas")
    public List<NeoSkiArea> getNeoSkiAreas() {
        return skiAreaRepository.findAll();
    }

    @PostMapping("/n4j-ski-areas")
    public NeoSkiArea createSkiArea(@org.springframework.web.bind.annotation.RequestBody NeoSkiArea skiArea) {
        return skiAreaRepository.save(skiArea);
    }

    @GetMapping("/n4j-ski-runs")
    public List<NeoSkiRun> getNeoSkiRuns() {
        return skiRunRepository.findAll();
    }

    @PostMapping("/n4j-ski-runs")
    public NeoSkiRun createSkiRun(@org.springframework.web.bind.annotation.RequestBody NeoSkiRun skiRun) {
        return skiRunRepository.save(skiRun);
    }

    @GetMapping("/n4j-ski-lifts")
    public List<NeoSkiLift> getNeoSkiLifts() {
        return skiLiftRepository.findAll();
    }

    @PostMapping("/n4j-ski-lifts")
    public NeoSkiLift createSkiLift(@org.springframework.web.bind.annotation.RequestBody NeoSkiLift skiLift) {
        return skiLiftRepository.save(skiLift);
    }

}

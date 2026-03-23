package net.dasumma1.skiguideapi.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.services.SkiAreaService;

@RestController
@RequestMapping("/api")
public class SkiAreaController {

    private final SkiAreaService skiAreaService;

    public SkiAreaController(SkiAreaService skiAreaService) {
        this.skiAreaService = skiAreaService;
    }

    @GetMapping("/find-areas-in-neo-not-in-sparql")
    public Map<String, String> findAreasInNeoNotInSparql(){
        return skiAreaService.findAreasInNeoNotInSparql();
    }

    @GetMapping("/find-areas-in-sparql-not-in-neo")
    public Map<String, String> findAreasInSparqlNotInNeo(){
        return skiAreaService.findAreasInSparqlNotInNeo();
    }

    @GetMapping("/add-missing-areas-to-sparql")
    public String addMissingAreasToSparql(){
        return skiAreaService.addMissingAreasToSparql();
    }

    @GetMapping("/find-runs-in-neo-not-in-sparql")
    public Map<String, String> findRunsInNeoNotInSparql(){
        return skiAreaService.findRunsInNeoNotInSparql();
    }

    @GetMapping("/find-runs-in-sparql-not-in-neo")
    public Map<String, String> findRunsInSparqlNotInNeo(){
        return skiAreaService.findRunsInSparqlNotInNeo();
    }

    @GetMapping("/add-missing-runs-to-sparql")
    public String addMissingRunsToSparql(){
        return skiAreaService.addMissingRunsToSparql();
    }

    @GetMapping("/find-lifts-in-neo-not-in-sparql")
    public Map<String, String> findLiftsInNeoNotInSparql(){ 
        return skiAreaService.findLiftsInNeoNotInSparql();
    }   

    @GetMapping("/find-lifts-in-sparql-not-in-neo")
    public Map<String, String> findLiftsInSparqlNotInNeo(){         
        return skiAreaService.findLiftsInSparqlNotInNeo();
    }
    
    @GetMapping("/add-missing-lifts-to-sparql")
    public String addMissingLiftsToSparql(){
        return skiAreaService.addMissingLiftsToSparql();
    }
}
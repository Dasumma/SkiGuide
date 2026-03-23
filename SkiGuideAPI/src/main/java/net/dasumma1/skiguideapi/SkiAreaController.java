package net.dasumma1.skiguideapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SkiAreaController {
    public SkiAreaController() {

    }
    
    @GetMapping("/find-areas-in-neo-not-in-sparql")
    public String findAreasInNeoNotInSparql(){
        throw new UnsupportedOperationException("This endpoint is not implemented yet. It will require a custom SPARQL query to find ski areas that exist in Neo4j but not in the SPARQL endpoint, and then return those results in a readable format.");
    }
}
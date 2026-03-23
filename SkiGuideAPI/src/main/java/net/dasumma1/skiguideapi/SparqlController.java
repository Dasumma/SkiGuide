package net.dasumma1.skiguideapi;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.dasumma1.skiguideapi.rdf_objects.RdfSkiAreaRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiLiftRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunRequest;

@RestController
@RequestMapping("/api/sparql")
public class SparqlController {
    private final RDFConnection fusekiClient;

    public SparqlController() {
        // Use root dataset URL and explicit query/update endpoints to avoid 415 errors on server-side content-type mismatch
        fusekiClient = RDFConnectionRemote.create()
                .destination("http://localhost:3030/dataset")
                .queryEndpoint("sparql")
                .updateEndpoint("update")
                .build();
    }

    // SPARQL-based endpoints
    @GetMapping("/sparql-ski-areas")
    public String getSparqlSkiAreas() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?s ?name WHERE { ?s a ski:SkiArea . OPTIONAL { ?s ski:name ?name } } LIMIT 50";
        ResultSet results = fusekiClient.query(query).execSelect();
        StringBuilder sb = new StringBuilder();
        results.forEachRemaining(qs -> sb.append(qs).append("\n"));
        return sb.toString();
    }

    @PostMapping("/sparql-ski-areas")
    public String createSparqlSkiArea(@org.springframework.web.bind.annotation.RequestBody RdfSkiAreaRequest request) {
        String uri = buildUri("ski-area", request.getId());
        String update = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "INSERT DATA { <" + uri + "> a ski:SkiArea ; ski:name \"" + escapeLiteral(request.getName()) + "\" . }";
        fusekiClient.update(update);
        return "Inserted SKIAREA " + uri;
    }

    @GetMapping("/sparql-ski-runs")
    public String getSparqlSkiRuns() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?r ?name ?area WHERE { ?r a ski:SkiRun . OPTIONAL { ?r ski:name ?name } OPTIONAL { ?area ski:hasSkiRun ?r } } LIMIT 50";
        ResultSet results = fusekiClient.query(query).execSelect();
        StringBuilder sb = new StringBuilder();
        results.forEachRemaining(qs -> sb.append(qs).append("\n"));
        return sb.toString();
    }

    @PostMapping("/sparql-ski-runs")
    public String createSparqlSkiRun(@org.springframework.web.bind.annotation.RequestBody RdfSkiRunRequest request) {
        String runUri = buildUri("ski-run", request.getId());
        String areaUri = buildUri("ski-area", request.getAreaId());
        StringBuilder insert = new StringBuilder()
            .append("PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("INSERT DATA { ")
            .append("<").append(runUri).append("> a ski:SkiRun ; ")
            .append("ski:name \"").append(escapeLiteral(request.getName())).append("\" ; ");
            
        if (request.getDifficulty() != null) {
            insert.append("ski:difficulty \"").append(escapeLiteral(request.getDifficulty())).append("\" ; ");
        }
        if (request.getAreaId() != null) {
            insert.append("ski:isRunOf <").append(areaUri).append("> . ");
            insert.append("<").append(areaUri).append("> a ski:SkiArea ; ski:hasSkiRun <").append(runUri).append("> . ");
        } else {
            insert.append(". ");
        }
        insert.append("}");

        fusekiClient.update(insert.toString());
        return "Inserted SKIRUN " + runUri;
    }
    
    @GetMapping("/sparql-ski-lifts")
    public String getSparqlSkiLifts() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?l ?name ?area WHERE { ?l a ski:SkiLift . OPTIONAL { ?l ski:name ?name } OPTIONAL { ?area ski:hasSkiLift ?l } } LIMIT 50";
        ResultSet results = fusekiClient.query(query).execSelect();
        StringBuilder sb = new StringBuilder();
        results.forEachRemaining(qs -> sb.append(qs).append("\n"));
        return sb.toString();
    }

    @PostMapping("/sparql-ski-lifts")
    public String createSparqlSkiLift(@org.springframework.web.bind.annotation.RequestBody RdfSkiLiftRequest request) {
        String liftUri = buildUri("ski-lift", request.getId());
        String areaUri = buildUri("ski-area", request.getAreaId());
        StringBuilder insert = new StringBuilder()
            .append("PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("INSERT DATA { ")
            .append("<").append(liftUri).append("> a ski:SkiLift ; ")
            .append("ski:name \"").append(escapeLiteral(request.getName())).append("\" ; ");
            
        if (request.getType() != null) {
            insert.append("ski:type \"").append(escapeLiteral(request.getType())).append("\" ; ");
        }
        if (request.getAreaId() != null) {
            insert.append("ski:isLiftOf <").append(areaUri).append("> . ");
            insert.append("<").append(areaUri).append("> a ski:SkiArea ; ski:hasSkiLift <").append(liftUri).append("> . ");
        } else {
            insert.append(". ");
        }
        insert.append("}");

        fusekiClient.update(insert.toString());
        return "Inserted SKILIFT " + liftUri;
    }
    /* Helper Methods */
    /**
     * Builds a clean URI for a given type and ID, generating a UUID if ID is null or empty.
     * @param type
     * @param id
     * @return
     */
    private static String buildUri(String type, String id) {
        String cleanId = id == null ? java.util.UUID.randomUUID().toString() : id.trim().replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9_-]", "");
        return "http://example.org/" + type + "/" + cleanId;
    }

    /**
     * Escapes special characters in a literal value for safe inclusion in Sparql queries.
     * @param value
     * @return
     */
    private static String escapeLiteral(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

package net.dasumma1.skiguideapi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.springframework.stereotype.Service;

import net.dasumma1.skiguideapi.rdf_objects.RdfSkiAreaRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiLiftRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunPreferencesRequest;
import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunRequest;

@Service
public class SparqlService {

    private final RDFConnection fusekiClient;

    public SparqlService() {
        // Use root dataset URL and explicit query/update endpoints to avoid 415 errors on server-side content-type mismatch
        this.fusekiClient = RDFConnectionRemote.create()
                .destination("http://localhost:3030/dataset")
                .queryEndpoint("sparql")
                .updateEndpoint("update")
                .build();
    }

    // SPARQL-based operations
    public Map<String, String> getSparqlSkiAreas() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?s ?name WHERE { ?s a ski:SkiArea .  ?s ski:name ?name . } LIMIT 50";
        ResultSet results = fusekiClient.query(query).execSelect();
        Map<String, String> sb = new HashMap<>();
        results.forEachRemaining(qs -> sb.put(getLastPartOfUri(qs.get("s").toString()), qs.get("name").toString()));
        return sb;
    }

    public String createSparqlSkiArea(RdfSkiAreaRequest request) {
        String uri = buildUri("ski-area", request.getId());
        String update = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "INSERT DATA { <" + uri + "> a ski:SkiArea ; ski:name \"" + escapeLiteral(request.getName()) + "\" . }";
        fusekiClient.update(update);
        return "Inserted SKIAREA " + uri;
    }

    public List<RdfSkiRunRequest> getSparqlSkiRuns() {
        StringBuilder query = new StringBuilder()
            .append("PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("SELECT ?r ?name ?difficulty ?snowmaking ?grooming ?patrolled ?oneway ?lit ?gladed ")
            .append("WHERE { ")
            .append("?r a ski:SkiRun . ")
            .append("?r ski:name ?name . ")
            .append("OPTIONAL { ?r ski:isRunOf ?area . } ")
            .append("OPTIONAL { ?r ski:hasDifficulty ?difficulty } ")
            .append("OPTIONAL { ?r ski:hasSnowmaking ?snowmaking } ")
            .append("OPTIONAL { ?r ski:isGroomed ?grooming } ")
            .append("OPTIONAL { ?r ski:isPatrolled ?patrolled } ")
            .append("OPTIONAL { ?r ski:isOneway ?oneway } ")
            .append("OPTIONAL { ?r ski:isLit ?lit } ")
            .append("OPTIONAL { ?r ski:isGladed ?gladed } ")
            .append("} LIMIT 50 ");

        ResultSet results = fusekiClient.query(query.toString()).execSelect();
        List<RdfSkiRunRequest> sb = new ArrayList<RdfSkiRunRequest>();
        results.forEachRemaining(qs -> 
            sb.add(
                new RdfSkiRunRequest(
                    getLastPartOfUri(qs.get("r").toString()), 
                    qs.get("name").toString(),
                    qs.get("difficulty") != null ? Integer.parseInt(qs.get("difficulty").toString()) : 0,
                    qs.get("grooming") != null ? Boolean.parseBoolean(qs.get("grooming").toString()) : null,
                    qs.get("patrolled") != null ? Boolean.parseBoolean(qs.get("patrolled").toString()) : null,
                    qs.get("snowmaking") != null ? Boolean.parseBoolean(qs.get("snowmaking").toString()) : null,
                    qs.get("oneway") != null ? Boolean.parseBoolean(qs.get("oneway").toString()) : null,
                    qs.get("lit") != null ? Boolean.parseBoolean(qs.get("lit").toString()) : null,
                    qs.get("gladed") != null ? Boolean.parseBoolean(qs.get("gladed").toString()) : null
                )
            )
        );
        return sb;
    }

    public String createSparqlSkiRun(RdfSkiRunRequest request, List<String> areaIds) {
        String runUri = buildUri("ski-run", request.getId());
        StringBuilder insert = new StringBuilder()
            .append("PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("INSERT DATA { ")
            .append("<").append(runUri).append("> a ski:SkiRun ; ")
            .append("ski:name \"").append(escapeLiteral(request.getName())).append("\" ; ");
            for (String areaId : areaIds) {
                insert.append("ski:isRunOf <").append( buildUri("ski-area", areaId)).append("> ; ");
            }
            
            insert.append("ski:hasDifficulty \"").append(request.getHasDifficulty()).append("\" ; ");
            if (request.getIsGroomed() != null) {
                insert.append("ski:isGroomed ").append(request.getIsGroomed()).append(" ; ");
            }
            if (request.getIsPatrolled() != null) {
                insert.append("ski:isPatrolled ").append(request.getIsPatrolled()).append(" ; ");
            }
            if (request.getHasSnowmaking() != null) {
                insert.append("ski:hasSnowmaking ").append(request.getHasSnowmaking()).append(" ; ");
            }
            if (request.getIsOneway() != null){
                insert.append("ski:isOneway ").append(request.getIsOneway()).append(" ; ");
            }
            if (request.getIsLit() != null) {
                insert.append("ski:isLit ").append(request.getIsLit()).append(" ; ");
            }
            if (request.getIsGladed() != null) {
                insert.append("ski:isGladed ").append(request.getIsGladed()).append(" . ");
            }
            insert.append("}");

        Logger.getLogger(SparqlService.class.getName()).info("Generated SPARQL Query: " + insert.toString());
        fusekiClient.update(insert.toString());
        return "Inserted SKIRUN " + runUri;
    }

    public Map<String, String> getSparqlSkiLifts() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?l ?name ?area WHERE { ?l a ski:SkiLift . OPTIONAL { ?l ski:name ?name } OPTIONAL { ?area ski:hasSkiLift ?l } } LIMIT 50";
        ResultSet results = fusekiClient.query(query).execSelect();
        Map<String, String> sb = new HashMap<>();
        results.forEachRemaining(qs -> sb.put(getLastPartOfUri(qs.get("l").toString()), qs.get("name").toString()));
        return sb;
    }

    public String createSparqlSkiLift(RdfSkiLiftRequest request) {
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
        return "http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology/" + type + "/" + cleanId;
    }

    private static String getLastPartOfUri(String uri) {
        if (uri == null) return null;
        String[] parts = uri.split("/");
        return parts[parts.length - 1];
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

    public List<String> getFilteredSkiRuns(RdfSkiRunPreferencesRequest preferences) {
        // Build SPARQL query based on provided filters
        StringBuilder query = new StringBuilder()
            .append("PREFIX : <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ")
            .append("SELECT ?r ?area ?score ");
        //Build weighted filters
        
        query.append("WHERE { ")
            .append("?r a :SkiRun . ")
            .append("?r :isRunOf ?area . ")
            .append("OPTIONAL { ?r :hasDifficulty ?hasDifficulty . } ")
            .append("OPTIONAL { ?r :isGladed ?isGladed . } ")
            .append("OPTIONAL { ?r :isGroomed ?isGroomed . } ")
            .append("OPTIONAL { ?r :isLit ?isLit . } ")
            .append("OPTIONAL { ?r :isOneway ?isOneway . } ")
            .append("OPTIONAL { ?r :hasSnowmaking ?hasSnowmaking . } ")
            .append("OPTIONAL { ?r :isPatrolled ?isPatrolled . } ")
            .append("BIND ( ");
            
        query.append(String.format("ABS(xsd:integer(COALESCE(?hasDifficulty, 4)) * 25 - %f) ", preferences.getHasDifficulty()));
            query.append(String.format("+ IF(COALESCE(?isGladed, true), 0, 1) * %f / 5", preferences.getIsGladed()));
            query.append(String.format("+ IF(COALESCE(?isGroomed, true), 0, 1) * %f / 5", preferences.getIsGroomed()));
            query.append(String.format("+ IF(COALESCE(?isLit, true), 0, 1) * %f / 5 ", preferences.getIsLit()));
            query.append(String.format("+ IF(COALESCE(?isOneway, true), 0, 1) * %f / 5 ", preferences.getIsOneway()));
            query.append(String.format("+ IF(COALESCE(?hasSnowmaking, true), 0, 1) * %f / 5 ", preferences.getHasSnowmaking()));
            query.append(String.format("+ IF(COALESCE(?isPatrolled, true), 0, 1) * %f / 5", preferences.getIsPatrolled()));

        query.append(" AS ?score ) ")
            .append("} ")
            .append("ORDER BY DESC(?score)");

        Logger.getLogger(SparqlService.class.getName()).info("Generated SPARQL Query: " + query.toString());
        List<String> runIds = new ArrayList<String>();
        fusekiClient.query(query.toString()).execSelect().forEachRemaining(qs -> {
            // Map results to RdfSkiRunRequest objects and add to list
            // For simplicity, only ID is mapped here, but you can expand this to include all relevant properties
            runIds.add("'"+getLastPartOfUri(qs.get("r").toString())+"'");
            // Add logic to create RdfSkiRunRequest from query solution and add to result list
        });

        return runIds;
    }

    public static int getDifficultyWeight(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return 1;
            case "intermediate": return 2;
            case "difficult": return 3;
            case "expert": return 4;
            default: return 4; // Unknown difficulty
        }
    }
}
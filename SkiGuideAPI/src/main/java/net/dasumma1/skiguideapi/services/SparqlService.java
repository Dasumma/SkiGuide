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

import net.dasumma1.skiguideapi.request_objects.RdfSkiAreaRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiLiftRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunPreferencesRequest;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunRequest;

/**
 * Service class for interacting with an Apache Jena Fuseki SPARQL endpoint.
 * This service handles RDF-based CRUD operations for ski areas, runs, and lifts,
 * and provides preference-based filtering using weighted SPARQL BIND expressions.
 */
@Service
public class SparqlService {

    /** Connection client to the Fuseki server. */
    private final RDFConnection fusekiClient;

    /**
     * Initializes the SparqlService by establishing a remote connection to the Fuseki dataset.
     * Configures explicit query and update endpoints to ensure compatibility with server-side content types.
     */
    public SparqlService() {
        this.fusekiClient = RDFConnectionRemote.create()
                .destination("http://localhost:3030/dataset")
                .queryEndpoint("sparql")
                .updateEndpoint("update")
                .build();
    }

    /**
     * Retrieves all ski areas currently stored in the RDF triplestore.
     * @return a Map of ski area IDs (extracted from URIs) to their human-readable names.
     */
    public Map<String, String> getSparqlSkiAreas() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?s ?name WHERE { ?s a ski:SkiArea .  ?s ski:name ?name . } LIMIT 50";
        ResultSet results = fusekiClient.query(query).execSelect();
        Map<String, String> sb = new HashMap<>();
        results.forEachRemaining(qs -> sb.put(getLastPartOfUri(qs.get("s").toString()), qs.get("name").toString()));
        return sb;
    }

    /**
     * Creates a new SkiArea entry in the RDF triplestore.
     * @param request the object containing the ID and name of the area to insert.
     * @return a confirmation string containing the generated URI of the inserted area.
     */
    public String createSparqlSkiArea(RdfSkiAreaRequest request) {
        String uri = buildUri("ski-area", request.getId());
        String update = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "INSERT DATA { <" + uri + "> a ski:SkiArea ; ski:name \"" + escapeLiteral(request.getName()) + "\" . }";
        fusekiClient.update(update);
        return "Inserted SKIAREA " + uri;
    }

    /**
     * Retrieves detailed information from the RDF triplestore.
     * Uses OPTIONAL clauses to fetch attributes like difficulty, snowmaking, and grooming status.
     * @return a List of {@link RdfSkiRunRequest} objects populated with run metadata.
     */
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
            .append("} ");

        ResultSet results = fusekiClient.query(query.toString()).execSelect();
        List<RdfSkiRunRequest> sb = new ArrayList<RdfSkiRunRequest>();
        results.forEachRemaining(qs -> {
            // Extract nodes as literals where applicable
            var difficultyNode = qs.get("difficulty");
            var groomingNode = qs.get("grooming");
            var patrolledNode = qs.get("patrolled");
            var snowmakingNode = qs.get("snowmaking");
            var onewayNode = qs.get("oneway");
            var litNode = qs.get("lit");
            var gladedNode = qs.get("gladed");

            sb.add(
                    new RdfSkiRunRequest(
                        getLastPartOfUri(qs.get("r").toString()), 
                        // Use .asLiteral().getString() for plain strings
                        qs.get("name").asLiteral().getString(),
                        
                        // Use .asLiteral().getInt() for integers
                        difficultyNode != null ? difficultyNode.asLiteral().getInt() : 0,
                        
                        // Use .asLiteral().getBoolean() for booleans
                        groomingNode != null ? groomingNode.asLiteral().getBoolean() : null,
                        patrolledNode != null ? patrolledNode.asLiteral().getBoolean() : null,
                        snowmakingNode != null ? snowmakingNode.asLiteral().getBoolean() : null,
                        onewayNode != null ? onewayNode.asLiteral().getBoolean() : null,
                        litNode != null ? litNode.asLiteral().getBoolean() : null,
                        gladedNode != null ? gladedNode.asLiteral().getBoolean() : null
                    )
                );
            }
        );
        return sb;
    }

    /**
     * Creates a new SkiRun in the RDF triplestore.
     * 
     * @param request the run data including name, difficulty, and various attributes.
     * @param areaIds a list of area IDs to associate with the run.
     * @return a confirmation string containing the run URI.
     */
    public String createSparqlSkiRun(RdfSkiRunRequest request, List<String> areaIds) {
        String runUri = buildUri("ski-run", request.getId());
        StringBuilder insert = new StringBuilder()
            .append("PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("INSERT DATA { ")
            // Subject definition
            .append("<").append(runUri).append("> a ski:SkiRun . ")
            .append("<").append(runUri).append("> ski:name \"").append(escapeLiteral(request.getName())).append("\" . ")
            .append("<").append(runUri).append("> ski:hasDifficulty ").append(request.getHasDifficulty()).append(" . ");

        // Link to Areas
        for (String areaId : areaIds) {
            insert.append("<").append(runUri).append("> ski:isRunOf <").append(buildUri("ski-area", areaId)).append("> . ");
            insert.append("<").append(buildUri("ski-area", areaId)).append("> a ski:SkiArea ; ").append("ski:hasSkiRun <").append(runUri).append("> . ");
        }

        // Optional Attributes - Using individual triples to avoid semicolon errors
        appendTripleIfNotNull(insert, runUri, "ski:isGroomed", request.getIsGroomed());
        appendTripleIfNotNull(insert, runUri, "ski:isPatrolled", request.getIsPatrolled());
        appendTripleIfNotNull(insert, runUri, "ski:hasSnowmaking", request.getHasSnowmaking());
        appendTripleIfNotNull(insert, runUri, "ski:isOneway", request.getIsOneway());
        appendTripleIfNotNull(insert, runUri, "ski:isLit", request.getIsLit());
        appendTripleIfNotNull(insert, runUri, "ski:isGladed", request.getIsGladed());

        insert.append("}");

        Logger.getLogger(SparqlService.class.getName()).info("Generated SPARQL Query: " + insert.toString());
        fusekiClient.update(insert.toString());
        return "Inserted SKIRUN " + runUri;
    }

    private void appendTripleIfNotNull(StringBuilder sb, String subjectUri, String predicate, Object value) {
        if (value != null) {
            sb.append("<").append(subjectUri).append("> ").append(predicate).append(" ").append(value).append(" . ");
        }
    }

    /**
     * Retrieves all ski lifts currently stored in the RDF triplestore.
     * @return a Map of lift IDs to lift names.
     */
    public Map<String, String> getSparqlSkiLifts() {
        String query = "PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> "
                + "SELECT ?l ?name ?area WHERE { ?l a ski:SkiLift . OPTIONAL { ?l ski:name ?name } OPTIONAL { ?area ski:hasSkiLift ?l } }";
        ResultSet results = fusekiClient.query(query).execSelect();
        Map<String, String> sb = new HashMap<>();
        results.forEachRemaining(qs -> sb.put(getLastPartOfUri(qs.get("l").toString()), qs.get("name").toString()));
        return sb;
    }

    /**
     * Creates a new SkiLift in the RDF triplestore and establishes 
     * bidirectional links between the lift and its parent area.
     * 
     * @param request the lift data including name, type, and associated area ID.
     * @param areaIds a list of area IDs to associate with the lift.
     * @return a confirmation string containing the lift URI.
     */
    public String createSparqlSkiLift(RdfSkiLiftRequest request, List<String> areaIds) {
        String liftUri = buildUri("ski-lift", request.getId());
        
        StringBuilder insert = new StringBuilder()
            .append("PREFIX ski: <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("INSERT DATA { ")
            // Main Lift Definition
            .append("<").append(liftUri).append("> a ski:SkiLift ; ")
            .append("ski:name \"").append(escapeLiteral(request.getName())).append("\" . ");

        if (request.getType() != null) {
            insert.append("<").append(liftUri).append("> ski:type \"")
                .append(escapeLiteral(request.getType())).append("\" . ");
        }

        // Relationships
        for (String areaId : areaIds) {
            String areaUri = buildUri("ski-area", areaId);
            
            // Link Lift -> Area (Using the correct 'ski' prefix)
            insert.append("<").append(liftUri).append("> ski:isLiftOf <").append(areaUri).append("> . ");
            
            // Link Area -> Lift
            insert.append("<").append(areaUri).append("> a ski:SkiArea ; ")
                .append("ski:hasSkiLift <").append(liftUri).append("> . ");
        }

        insert.append("}");

        fusekiClient.update(insert.toString());
        return "Inserted SKILIFT " + liftUri;
    }

    /**
     * Generates a URI for an RDF resource based on its type and ID.
     * Sanitizes the ID by replacing spaces with hyphens and removing special characters.
     * @param type the resource type (e.g., "ski-run", "ski-area").
     * @param id the unique identifier for the resource.
     * @return the fully qualified URI string.
     */
    private static String buildUri(String type, String id) {
        String cleanId = id == null ? java.util.UUID.randomUUID().toString() : id.trim().replaceAll("\\s+", "-").replaceAll("[^a-zA-Z0-9_-]", "");
        return "http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology/" + type + "/" + cleanId;
    }

    /**
     * Extracts the local identifier from a full URI string.
     * @param uri the full URI (e.g., "http://.../ski-run/run-123").
     * @return the substring after the last slash.
     */
    private static String getLastPartOfUri(String uri) {
        if (uri == null) return null;
        String[] parts = uri.split("/");
        return parts[parts.length - 1];
    }

    /**
     * Escapes special characters in a literal string to prevent SPARQL injection 
     * and ensure valid syntax for literals containing quotes or backslashes.
     * @param value the string to escape.
     * @return the escaped string safe for inclusion in a SPARQL query.
     */
    private static String escapeLiteral(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    /**
     * Queries the triplestore for ski runs and calculates a "score" for each based on user preferences.
     * The score uses a mathematical calculation in a BIND clause to weight different 
     * attributes (difficulty, grooming, lighting, etc.) provided in the request.
     * @param preferences the user's weighted preferences for run features.
     * @return a List of run IDs ordered by their suitability score (descending).
     */
    public List<String> getFilteredSkiRuns(RdfSkiRunPreferencesRequest preferences) {
        StringBuilder query = new StringBuilder()
            .append("PREFIX : <http://www.semanticweb.org/dasum/ontologies/2026/1/ski-map-ontology#> ")
            .append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ")
            .append("SELECT ?r ?area ?score ");
        
        query.append("WHERE { ")
            .append("?r a :SkiRun . ")
            .append("?r :isRunOf <").append(buildUri("ski-area", preferences.getSkiAreaId())).append("> . ")
            .append("OPTIONAL { ?r :hasDifficulty ?hasDifficulty . } ")
            .append("OPTIONAL { ?r :isGladed ?isGladed . } ")
            .append("OPTIONAL { ?r :isGroomed ?isGroomed . } ")
            .append("OPTIONAL { ?r :isLit ?isLit . } ")
            .append("OPTIONAL { ?r :isOneway ?isOneway . } ")
            .append("OPTIONAL { ?r :hasSnowmaking ?hasSnowmaking . } ")
            .append("OPTIONAL { ?r :isPatrolled ?isPatrolled . } ")
            .append("BIND ( ");
            
        query.append(String.format("ABS(xsd:integer(COALESCE(?hasDifficulty, 4)) * 25 - %f) ", preferences.getHasDifficulty()));
            //query.append(String.format("+ IF(COALESCE(?isGladed, true), 0, 1) * %f / 25", preferences.getIsGladed()));
            //query.append(String.format("+ IF(COALESCE(?isGroomed, true), 0, 1) * %f / 25", preferences.getIsGroomed()));
            //query.append(String.format("+ IF(COALESCE(?isLit, true), 0, 1) * %f / 25 ", preferences.getIsLit()));
            //query.append(String.format("+ IF(COALESCE(?isOneway, true), 0, 1) * %f / 25 ", preferences.getIsOneway()));
            //query.append(String.format("+ IF(COALESCE(?hasSnowmaking, true), 0, 1) * %f / 25 ", preferences.getHasSnowmaking()));
            //query.append(String.format("+ IF(COALESCE(?isPatrolled, true), 0, 1) * %f / 25 ", preferences.getIsPatrolled()));

        query.append(" AS ?score ) ")
            .append("} ")
            .append("ORDER BY ASC(?score) ")
            .append("LIMIT ").append(preferences.getTrailLimit());

        Logger.getLogger(SparqlService.class.getName()).info("Generated SPARQL Query: " + query.toString());
        List<String> runIds = new ArrayList<String>();
        fusekiClient.query(query.toString()).execSelect().forEachRemaining(qs -> {
            runIds.add(getLastPartOfUri(qs.get("r").toString()));
        });
        
        return runIds;
    }

    /**
     * Converts a string-based difficulty label into a numerical weight for calculation.
     * @param difficulty the difficulty label (e.g., "easy", "expert").
     * @return an integer weight from 1 (easy) to 4 (expert).
     */
    public static int getDifficultyWeight(String difficulty) {
        if (difficulty == null) {
            return 4; // Default to hardest if not specified
        }
        switch (difficulty.toLowerCase()) {
            case "novice": return 1;
            case "easy": return 1;
            case "intermediate": return 2;
            case "difficult": return 3;
            case "expert": return 4;
            default: return 4;
        }
    }
}
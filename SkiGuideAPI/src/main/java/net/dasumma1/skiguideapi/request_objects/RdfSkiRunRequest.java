package net.dasumma1.skiguideapi.request_objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for creating or updating a Ski Run in the SPARQL triplestore.
 * <p>
 * This class encapsulates the descriptive and categorical attributes of a ski run, 
 * mapping them to semantic properties within the RDF ontology for advanced 
 * querying and filtering.
 */
@Schema(description = "Request object for creating a new ski run in the RDF store")
public class RdfSkiRunRequest {

    /** The unique identifier for the ski run, used to define the resource URI. */
    private String id;

    /** The human-readable name of the ski run. */
    private String name;

    /** The numerical representation of the run's difficulty level. */
    private int hasDifficulty;

    /** Indicates if the run is typically groomed. */
    private Boolean isGroomed;

    /** Indicates if the run is under ski patrol supervision. */
    private Boolean isPatrolled;

    /** Indicates if the run has artificial snowmaking infrastructure. */
    private Boolean hasSnowmaking;

    /** Indicates if the run follows a strict one-way traffic flow. */
    private Boolean isOneway;

    /** Indicates if the run is equipped with lights for night skiing. */
    private Boolean isLit;

    /** Indicates if the run is a gladed (tree skiing) area. */
    private Boolean isGladed;
    

    /**
     * Constructs a new RdfSkiRunRequest with all specified semantic attributes.
     * @param id            the unique run identifier
     * @param name          the name of the run
     * @param hasDifficulty the difficulty rating
     * @param isGroomed      grooming status flag
     * @param isPatrolled    patrol status flag
     * @param hasSnowmaking  snowmaking flag
     * @param isOneway       one-way traffic flag
     * @param isLit          lighting flag
     * @param isGladed       gladed terrain flag
     */
    public RdfSkiRunRequest(String id, String name, int hasDifficulty, Boolean isGroomed, Boolean isPatrolled, Boolean hasSnowmaking, Boolean isOneway, Boolean isLit, Boolean isGladed) {
        this.id = id;
        this.name = name;
        this.hasDifficulty = hasDifficulty;
        this.isGroomed = isGroomed;
        this.isPatrolled = isPatrolled;
        this.hasSnowmaking = hasSnowmaking;
        this.isOneway = isOneway;
        this.isLit = isLit;
        this.isGladed = isGladed;
    }

    /** @return the unique run ID. */
    public String getId() {
        return id;
    }

    /** @return the run name. */
    public String getName() {
        return name;
    }

    /** @return the difficulty level. */
    public int getHasDifficulty() {
        return hasDifficulty;
    }
    
    /** @return true if groomed, false or null otherwise. */
    public Boolean getIsGroomed() {
        return isGroomed;
    }
    
    /** @return true if patrolled, false or null otherwise. */
    public Boolean getIsPatrolled() {
        return isPatrolled;
    }

    /** @return true if snowmaking is present, false or null otherwise. */
    public Boolean getHasSnowmaking() {
        return hasSnowmaking;
    }

    /** @return true if one-way, false or null otherwise. */
    public Boolean getIsOneway() {
        return isOneway;
    }

    /** @return true if lit for night skiing, false or null otherwise. */
    public Boolean getIsLit() {
        return isLit;
    }

    /** @return true if gladed terrain, false or null otherwise. */
    public Boolean getIsGladed() {
        return isGladed;
    }
}
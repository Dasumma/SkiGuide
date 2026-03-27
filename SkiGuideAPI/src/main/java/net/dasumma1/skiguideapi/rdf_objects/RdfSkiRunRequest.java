package net.dasumma1.skiguideapi.rdf_objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new ski run in the RDF store")
public class RdfSkiRunRequest {
    private String id;
    private String name;
    private int hasDifficulty;
    private Boolean isGroomed;
    private Boolean isPatrolled;
    private Boolean hasSnowmaking;
    private Boolean isOneway;
    private Boolean isLit;
    private Boolean isGladed;

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHasDifficulty() {
        return hasDifficulty;
    }
    
    public Boolean getIsGroomed() {
        return isGroomed;
    }
    
    public Boolean getIsPatrolled() {
        return isPatrolled;
    }

    public Boolean getHasSnowmaking() {
        return hasSnowmaking;
    }

    public Boolean getIsOneway() {
        return isOneway;
    }

    public Boolean getIsLit() {
        return isLit;
    }

    public Boolean getIsGladed() {
        return isGladed;
    }
}
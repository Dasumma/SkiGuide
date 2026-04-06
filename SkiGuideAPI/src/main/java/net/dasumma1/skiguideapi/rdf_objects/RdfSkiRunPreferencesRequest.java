package net.dasumma1.skiguideapi.rdf_objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for filtered runs based on preferences")
public class RdfSkiRunPreferencesRequest {
    @Schema(minimum="0", maximum = "100")
    private Double hasDifficulty;
    @Schema(minimum="0", maximum = "100")
    private Double isGroomed;
    @Schema(minimum="0", maximum = "100")
    private Double isPatrolled;
    @Schema(minimum="0", maximum = "100")
    private Double hasSnowmaking;
    @Schema(minimum="0", maximum = "100")
    private Double isOneway;
    @Schema(minimum="0", maximum = "100")
    private Double isLit;
    @Schema(minimum="0", maximum = "100")
    private Double isGladed;

    public RdfSkiRunPreferencesRequest(Double hasDifficulty, Double isGroomed, Double isPatrolled, Double hasSnowmaking, Double isOneway, Double isLit, Double isGladed) {
        this.hasDifficulty = hasDifficulty;
        this.isGroomed = isGroomed;
        this.isPatrolled = isPatrolled;
        this.hasSnowmaking = hasSnowmaking;
        this.isOneway = isOneway;
        this.isLit = isLit;
        this.isGladed = isGladed;
    }

    public Double getHasDifficulty(){
        return hasDifficulty;
    }

    public Double getIsGroomed(){
        return isGroomed;
    }

    public Double getIsPatrolled(){
        return isPatrolled;
    }

    public Double getHasSnowmaking(){
        return hasSnowmaking;
    }

    public Double getIsOneway(){
        return isOneway;
    }

    public Double getIsLit(){
        return isLit;
    }

    public Double getIsGladed(){
        return isGladed;
    }
}

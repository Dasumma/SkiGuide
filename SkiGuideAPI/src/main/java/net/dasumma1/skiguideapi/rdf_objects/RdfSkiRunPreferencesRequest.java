package net.dasumma1.skiguideapi.rdf_objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for filtered runs based on preferences")
public class RdfSkiRunPreferencesRequest {
    @Schema(minimum="0", maximum = "100")
    private double hasDifficulty;
    @Schema(minimum="0", maximum = "100")
    private double isGroomed;
    @Schema(minimum="0", maximum = "100")
    private double isPatrolled;
    @Schema(minimum="0", maximum = "100")
    private double hasSnowmaking;
    @Schema(minimum="0", maximum = "100")
    private double isOneway;
    @Schema(minimum="0", maximum = "100")
    private double isLit;
    @Schema(minimum="0", maximum = "100")
    private double isGladed;

    public RdfSkiRunPreferencesRequest(double hasDifficulty, double isGroomed, double isPatrolled, double hasSnowmaking, double isOneway, double isLit, double isGladed) {
        this.hasDifficulty = hasDifficulty;
        this.isGroomed = isGroomed;
        this.isPatrolled = isPatrolled;
        this.hasSnowmaking = hasSnowmaking;
        this.isOneway = isOneway;
        this.isLit = isLit;
        this.isGladed = isGladed;
    }

    public double getHasDifficulty(){
        return hasDifficulty;
    }

    public double getIsGroomed(){
        return isGroomed;
    }

    public double getIsPatrolled(){
        return isPatrolled;
    }

    public double getHasSnowmaking(){
        return hasSnowmaking;
    }

    public double getIsOneway(){
        return isOneway;
    }

    public double getIsLit(){
        return isLit;
    }

    public double getIsGladed(){
        return isGladed;
    }
}

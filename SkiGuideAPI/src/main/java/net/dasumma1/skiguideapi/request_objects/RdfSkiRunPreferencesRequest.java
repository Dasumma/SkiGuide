package net.dasumma1.skiguideapi.request_objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) representing weighted user preferences for ski run attributes.
 * <p>
 * Each field accepts a value between 0 and 100, representing the importance or 
 * weight the user assigns to that specific characteristic. These weights are 
 * utilized by the SPARQL engine to calculate a suitability score for ranking runs.
 */
@Schema(description = "Request object for filtered runs based on preferences")
public class RdfSkiRunPreferencesRequest {

    @Schema(description = "Ski Area ID")
    private String skiAreaId;

    @Schema(description = "Weight for difficulty matching", minimum="0", maximum = "100")
    private Double hasDifficulty;

    @Schema(description = "Weight for groomed terrain preference", minimum="0", maximum = "100")
    private Double isGroomed;

    @Schema(description = "Weight for patrolled safety preference", minimum="0", maximum = "100")
    private Double isPatrolled;

    @Schema(description = "Weight for snowmaking reliability preference", minimum="0", maximum = "100")
    private Double hasSnowmaking;

    @Schema(description = "Weight for one-way traffic preference", minimum="0", maximum = "100")
    private Double isOneway;

    @Schema(description = "Weight for night lighting preference", minimum="0", maximum = "100")
    private Double isLit;

    @Schema(description = "Weight for gladed/tree skiing preference", minimum="0", maximum = "100")
    private Double isGladed;

    @Schema(description = "Number of trails to return in the response", minimum="1", maximum = "100")
    private Integer trailLimit;

    /**
     * Constructs a preferences request with full weighting parameters.
     * @param hasDifficulty  weight for run difficulty
     * @param isGroomed      weight for grooming status
     * @param isPatrolled    weight for patrol presence
     * @param hasSnowmaking  weight for snowmaking capabilities
     * @param isOneway       weight for one-way restrictions
     * @param isLit          weight for night lighting
     * @param isGladed       weight for gladed terrain
     * @param trailLimit     number of trails to return
     */
    public RdfSkiRunPreferencesRequest(String skiAreaId, Double hasDifficulty, Double isGroomed, Double isPatrolled, Double hasSnowmaking, Double isOneway, Double isLit, Double isGladed, Integer trailLimit) {
        this.skiAreaId = skiAreaId;
        this.hasDifficulty = hasDifficulty;
        this.isGroomed = isGroomed;
        this.isPatrolled = isPatrolled;
        this.hasSnowmaking = hasSnowmaking;
        this.isOneway = isOneway;
        this.isLit = isLit;
        this.isGladed = isGladed;
        this.trailLimit = trailLimit;
    }

    /** @return the weight assigned to difficulty. */
    public Double getHasDifficulty(){
        return hasDifficulty;
    }

    /** @return the weight assigned to grooming status. */
    public Double getIsGroomed(){
        return isGroomed;
    }

    /** @return the weight assigned to patrol presence. */
    public Double getIsPatrolled(){
        return isPatrolled;
    }

    /** @return the weight assigned to snowmaking capabilities. */
    public Double getHasSnowmaking(){
        return hasSnowmaking;
    }

    /** @return the weight assigned to one-way traffic. */
    public Double getIsOneway(){
        return isOneway;
    }

    /** @return the weight assigned to night lighting. */
    public Double getIsLit(){
        return isLit;
    }

    /** @return the weight assigned to gladed terrain. */
    public Double getIsGladed(){
        return isGladed;
    }

    /** @return the number of trails to return. */
    public Integer getTrailLimit(){
        return trailLimit;
    }

    /** @return the ski area ID. */
    public String getSkiAreaId() {
        return skiAreaId;
    }
}
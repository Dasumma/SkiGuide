package net.dasumma1.skiguideapi.neo_repositories.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Represents a SkiRun node within the Neo4j graph database.
 * <p>
 * This entity stores comprehensive metadata about a specific ski trail,
 * including its difficulty level, maintenance status (grooming/snowmaking),
 * and various safety or environmental flags used for filtering and routing.
 */
@Node("SkiRun")
public class NeoSkiRun {

    /** The unique identifier for the ski run, mapped as the Neo4j node ID. */
    @Id
    private final String id;

    /** The human-readable name of the ski run. */
    private final String name;

    /** The identifier of the {@link NeoSkiArea} that contains this run. */
    private final String areaId;

    /** The difficulty classification (e.g., "easy", "intermediate", "expert"). */
    private final String difficulty;

    /** The grooming status or type of maintenance performed on the run. */
    private final String grooming;

    /** Indicates if the run is actively monitored by ski patrol. */
    private final Boolean patrolled;

    /** Indicates if the run is equipped with artificial snowmaking capabilities. */
    private final Boolean snowmaking;

    /** Indicates if travel on this run is restricted to a single direction. */
    private final Boolean oneway;

    /** Indicates if the run is illuminated for night skiing. */
    private final Boolean lit;

    /** Indicates if the run consists of gladed terrain (skiing through trees). */
    private final Boolean gladed;

    /**
     * Constructs a new NeoSkiRun entity with all specified attributes.
     * * @param id         the unique identifier for the run
     * @param name       the name of the run
     * @param difficulty the difficulty rating
     * @param areaId     the ID of the parent ski area
     * @param grooming   the grooming status
     * @param patrolled  patrol status flag
     * @param snowmaking snowmaking equipment flag
     * @param oneway     one-way traffic flag
     * @param lit        night lighting flag
     * @param gladed     tree skiing flag
     */
    public NeoSkiRun(String id, String name, String difficulty, String areaId, String grooming, Boolean patrolled, Boolean snowmaking, Boolean oneway, Boolean lit, Boolean gladed) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.areaId = areaId;
        this.grooming = grooming;
        this.patrolled = patrolled;
        this.snowmaking = snowmaking;
        this.oneway = oneway;
        this.lit = lit;
        this.gladed = gladed;
    }

    /** @return the unique run ID. */
    public String getId() {
        return id;
    }

    /** @return the run name. */
    public String getName() {
        return name;
    } 

    /** @return the difficulty level string. */
    public String getDifficulty() {
        return difficulty;
    }

    /** @return the parent area ID. */
    public String getAreaId() {
        return areaId;
    }

    /** @return the grooming description. */
    public String getGrooming() {
        return grooming;
    }

    /** @return true if patrolled, false otherwise. */
    public Boolean getPatrolled() {
        return patrolled;
    }

    /** @return true if traffic is one-way, false otherwise. */
    public Boolean getOneway() {
        return oneway;
    }

    /** @return true if snowmaking is available, false otherwise. */
    public Boolean getSnowmaking() {
        return snowmaking;
    }

    /** @return true if the run is lit for night use, false otherwise. */
    public Boolean getLit() {
        return lit;
    }

    /** @return true if the run contains glades, false otherwise. */
    public Boolean getGladed() {
        return gladed;
    }
}
package net.dasumma1.skiguideapi.neo_repositories.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Represents a SkiLift node within the Neo4j graph database.
 * <p>
 * This entity captures the technical details of a ski lift and its 
 * structural association with a parent ski area.
 */
@Node("SkiLift")
public class NeoSkiLift {

    /** The unique identifier for the ski lift, mapped as the Neo4j node ID. */
    @Id
    private final String id;

    /** The human-readable name of the ski lift (e.g., "Gondola One"). */
    private final String name;

    /** The specific category of the lift (e.g., "chair_lift", "gondola", "t-bar"). */
    private final String type;

    /** The ID of the {@link NeoSkiArea} that this lift belongs to. */
    private final String areaId;

    /**
     * Constructs a new NeoSkiLift entity.
     * @param id     the unique identifier for the lift
     * @param name   the name of the lift
     * @param type   the type of lift infrastructure
     * @param areaId the identifier of the parent ski area
     */
    public NeoSkiLift(String id, String name, String type, String areaId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.areaId = areaId;
    }

    /**
     * Gets the unique identifier of this ski lift.
     * @return the lift ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of this ski lift.
     * @return the lift name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the classification type of the lift infrastructure.
     * @return the lift type string
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the identifier for the associated ski area.
     * @return the area ID
     */
    public String getAreaId() {
        return areaId;
    }
}
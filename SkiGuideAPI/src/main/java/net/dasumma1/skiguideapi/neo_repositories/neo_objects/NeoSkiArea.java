package net.dasumma1.skiguideapi.neo_repositories.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Represents a SkiArea node within the Neo4j graph database.
 * <p>
 * This entity serves as the primary container for a ski resort's identity,
 * acting as a hub for associated ski runs and lifts in the graph.
 */
@Node("SkiArea")
public class NeoSkiArea {

    /** The unique identifier for the ski area, mapped as the Neo4j node ID. */
    @Id
    private final String id;

    /** The human-readable name of the ski area. */
    private final String name;

    /**
     * Constructs a new NeoSkiArea entity.
     * * @param id   the unique identifier for the area
     * @param name the name of the ski area
     */
    public NeoSkiArea(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this ski area.
     * * @return the area ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of this ski area.
     * * @return the area name
     */
    public String getName() {
        return name;
    }
}
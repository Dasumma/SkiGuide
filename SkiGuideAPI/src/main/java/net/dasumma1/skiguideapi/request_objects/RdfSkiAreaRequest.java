package net.dasumma1.skiguideapi.request_objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for creating or updating a Ski Area in the SPARQL triplestore.
 * <p>
 * This object is used to capture the basic administrative identity of a ski resort
 * so it can be registered as a Resource within the application's RDF ontology.
 */
@Schema(description = "Request object for creating a new ski area in the RDF store")
public class RdfSkiAreaRequest {

    /** * The unique identifier for the ski area. 
     * This is typically used to construct the URI in the RDF store.
     */
    private String id;

    /** The formal name of the ski area (e.g., "Whistler Blackcomb"). */
    private String name;

    /**
     * Default constructor for JSON deserialization.
     */
    public RdfSkiAreaRequest() {
    }

    /**
     * Gets the unique identifier.
     * @return the area ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     * @param id the unique ID for the area
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the ski area.
     * @return the area name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the ski area.
     * @param name the descriptive name of the resort
     */
    public void setName(String name) {
        this.name = name;
    }
}
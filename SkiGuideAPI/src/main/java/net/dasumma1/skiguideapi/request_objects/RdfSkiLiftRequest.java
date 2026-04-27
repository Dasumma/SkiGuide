package net.dasumma1.skiguideapi.request_objects;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) for creating or updating a Ski Lift in the SPARQL triplestore.
 * <p>
 * This object captures the necessary metadata to define a lift resource within the 
 * ontology, including its classification and its semantic link to a specific ski area.
 */
@Schema(description = "Request object for creating a new ski lift in the RDF store")
public class RdfSkiLiftRequest {

    /** The unique identifier for the ski lift, used as a URI component in the RDF store. */
    private String id;

    /** The descriptive name of the lift (e.g., "Eagle Bahn Gondola"). */
    private String name;

    /** The technical type of the lift (e.g., "chair_lift", "gondola", "t-bar"). */
    private String type;

    /** The identifier of the {@link RdfSkiAreaRequest} that this lift is associated with. */
    private String areaId;

    /**
     * Default constructor for JSON deserialization.
     */
    public RdfSkiLiftRequest() {
    }

    /**
     * Gets the unique identifier.
     * @return the lift ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     * @param id the unique ID for the lift
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the ski lift.
     * @return the lift name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the ski lift.
     * @param name the descriptive name of the lift
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the classification type of the lift.
     * @return the lift type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the classification type of the lift.
     * @param type the category of lift infrastructure
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the identifier for the associated ski area.
     * @return the parent area ID
     */
    public String getAreaId() {
        return areaId;
    }

    /**
     * Sets the identifier for the associated ski area.
     * @param areaId the parent area ID to establish the semantic relationship
     */
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
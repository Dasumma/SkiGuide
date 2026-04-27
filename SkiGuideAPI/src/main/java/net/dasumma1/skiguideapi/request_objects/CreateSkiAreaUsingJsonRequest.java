package net.dasumma1.skiguideapi.request_objects;

/**
 * Data Transfer Object (DTO) used for bulk creation of a ski area and its features.
 * <p>
 * This class encapsulates raw GeoJSON strings representing the ski area boundaries, 
 * the network of ski runs, and the lift infrastructure. These strings are typically 
 * processed by APOC procedures in the Neo4j repository.
 */
public class CreateSkiAreaUsingJsonRequest {

    /** Raw JSON string containing the ski area's primary metadata and geometry. */
    private String skiAreaJson;

    /** Raw JSON string containing a FeatureCollection of ski runs. */
    private String skiRunsJson;

    /** Raw JSON string containing a FeatureCollection of ski lifts. */
    private String skiLiftsJson;

    /**
     * Default constructor for JSON deserialization.
     */
    public CreateSkiAreaUsingJsonRequest() {
    }

    /**
     * Gets the JSON string representing the ski area.
     * @return the raw ski area JSON
     */
    public String getSkiAreaJson() {
        return skiAreaJson;
    }

    /**
     * Sets the JSON string representing the ski area.
     * @param skiAreaJson the raw ski area JSON to set
     */
    public void setSkiAreaJson(String skiAreaJson) {
        this.skiAreaJson = skiAreaJson;
    }

    /**
     * Gets the JSON string representing the collection of ski runs.
     * @return the raw ski runs JSON
     */
    public String getSkiRunsJson() {
        return skiRunsJson;
    }

    /**
     * Sets the JSON string representing the collection of ski runs.
     * @param skiRunsJson the raw ski runs JSON to set
     */
    public void setSkiRunsJson(String skiRunsJson) {
        this.skiRunsJson = skiRunsJson;
    }

    /**
     * Gets the JSON string representing the collection of ski lifts.
     * @return the raw ski lifts JSON
     */
    public String getSkiLiftsJson() {
        return skiLiftsJson;
    }

    /**
     * Sets the JSON string representing the collection of ski lifts.
     * @param skiLiftsJson the raw ski lifts JSON to set
     */
    public void setSkiLiftsJson(String skiLiftsJson) {
        this.skiLiftsJson = skiLiftsJson;
    }
}
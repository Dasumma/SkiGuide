package net.dasumma1.skiguideapi.neo_repositories.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Point")
public class NeoSkiPoint {
    @Id
    private final Double lon;
    private final Double lat;
    private final Double alt;

    /**
     * Constructor for NeoSkiPoint
     * @param lon Longitude of the point
     * @param lat Latitude of the point
     * @param alt Altitude of the point
     */
    public NeoSkiPoint(Double lon, Double lat, Double alt) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
    }

    /**
     * Getter for the Longitude of the point
     * @return The longitude of the point
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Getter for the Latitude of the point
     * @return The latitude of the point
     */
    public Double getLat() {
        return lat; 
    }

    /**
     * Getter for the Altitude of the point
     * @return The altitude of the point
     */
    public Double getAlt() {
        return alt; 
    }

    /**
     * Override of the toString method to provide a string representation of the NeoSkiPoint
     * @return A string representation of the NeoSkiPoint  
     */
    @Override
    public String toString() {
        return String.format("NeoSkiPoint{longitude=%f, latitude=%f, altitude=%f}", lon, lat, alt);
    }

    /**
     * Getter for the ID of the point, which is a combination of the longitude and latitude
     * @return
     */
    public String getId() {
        return String.format("%f;%f", lon, lat);
    }
}

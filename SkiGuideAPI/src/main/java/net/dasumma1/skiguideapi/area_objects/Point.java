package net.dasumma1.skiguideapi.area_objects;

/**
 * Represents a geographical coordinate consisting of longitude and latitude.
 * <p>
 * This class is used as a standard data structure for defining locations,
 * such as the start and end points of a ski route or the coordinates 
 * of specific infrastructure.
 */
public class Point {

    /** The longitude (X-coordinate) of the point. */
    private Double longitude;

    /** The latitude (Y-coordinate) of the point. */
    private Double latitude;

    /**
     * Constructs a new Point with specific coordinates.
     * * @param longitude the longitudinal value of the point
     * @param latitude  the latitudinal value of the point
     */
    public Point(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of this point.
     * @return the longitude value
     */
    public Double getLongitude() { return longitude; }

    /**
     * Sets the longitude of this point.
     * @param longitude the new longitude value
     */
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    /**
     * Gets the latitude of this point.
     * @return the latitude value
     */
    public Double getLatitude() { return latitude; }

    /**
     * Sets the latitude of this point.
     * @param latitude the new latitude value
     */
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public String toString() {
        return String.format("Point{longitude=%f, latitude=%f}", longitude, latitude);
    }

    public String getId() {
        return String.format("%f;%f", longitude, latitude);
    }
}
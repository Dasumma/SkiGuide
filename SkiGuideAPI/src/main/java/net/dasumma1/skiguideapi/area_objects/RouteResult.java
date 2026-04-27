package net.dasumma1.skiguideapi.area_objects;

import java.util.List;
import org.springframework.data.repository.query.Param;

/**
 * Represents the final output of a route calculation.
 * <p>
 * This class encapsulates the geometric path (a series of coordinates) 
 * and the total calculated distance of the journey. It is designed to be 
 * easily serialized into JSON for API responses.
 */
public class RouteResult {

    /** * The ordered list of coordinates representing the route. 
     * Each double array typically contains [longitude, latitude].
     */
    private final List<double[]> path;

    /** The total distance of the path, usually in meters or kilometers. */
    private final Double distance;

    /**
     * Constructs a new RouteResult.
     * * @param path     the list of coordinate pairs defining the route
     * @param distance the total traversal distance
     */
    public RouteResult(@Param("path") List<double[]> path, @Param("distance") Double distance) {
        this.path = path;
        this.distance = distance;
    }

    /**
     * Gets the geometric path of the route.
     * @return a list of double arrays where index 0 is longitude and 1 is latitude
     */
    public List<double[]> getPath() { return path; }

    /**
     * Gets the total distance of the route.
     * @return the distance as a Double
     */
    public Double getDistance() { return distance; }

    /**
     * Converts the list of coordinate arrays into a JSON-compliant string representation.
     * <p>
     * For example: [[lon1,lat1],[lon2,lat2]]
     * * @return a formatted string of the coordinate path
     */
    public String pathToString(){
        StringBuilder pathString = new StringBuilder().append("[");
        if (path.isEmpty()) {
            return "[]";
        }
        for (double[] point : path){
            pathString.append("[" + point[0] + "," + point[1] + "],");
        }
        pathString.setLength(pathString.length()-1);
        pathString.append("]");
        return pathString.toString();
    }

    /**
     * Returns a JSON string representation of the RouteResult object.
     * @return a JSON-formatted string containing the path and distance
     */
    @Override
    public String toString() {
        return "{\"routeResult\": {" +
                "\"path\": " + pathToString() +
                ", \"distance\": " + distance +
                "}}";
    }
}
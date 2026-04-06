package net.dasumma1.skiguideapi.area_objects;

import java.util.List;


import org.springframework.data.repository.query.Param;

public class RouteResult {
    private final List<double[]> path;
    private final Double distance;

    public RouteResult(@Param("path") List<double[]> path, @Param("distance") Double distance) {
        this.path = path;
        this.distance = distance;
    }

    // Getters
    public List<double[]> getPath() { return path; }
    public Double getDistance() { return distance; }

    public String pathToString(){
        StringBuilder pathString = new StringBuilder().append("[");
        for (double[] point : path){
            pathString.append("[" + point[0] + "," + point[1] + "],");
        }
        pathString.setLength(pathString.length()-1);
        pathString.append("]");
        return pathString.toString();
    }

    @Override
    public String toString() {
        return "RouteResult{" +
                "path=" + pathToString() +
                ", distance=" + distance +
                '}';
    }
}
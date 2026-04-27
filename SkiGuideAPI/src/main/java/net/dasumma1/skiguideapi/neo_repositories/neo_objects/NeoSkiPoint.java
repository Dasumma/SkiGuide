package net.dasumma1.skiguideapi.neo_repositories.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Point")
public class NeoSkiPoint {
    @Id
    private final Double longitude;
    private final Double latitude;

    public NeoSkiPoint(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public Double getLatitude() {
        return latitude; 
    }
    public String toString() {
        return String.format("NeoSkiPoint{longitude=%f, latitude=%f}", longitude, latitude);
    }
}

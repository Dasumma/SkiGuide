
package net.dasumma1.skiguideapi.neo_repositories.neo_objects.GeoJSONFeature;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a GeoJSON Feature Collection for ski runs and lifts.
 */
public class GeoJSONFeatureRequest{
    /**
     * Represents a GeoJSON Feature Collection, which is the standard format for encoding a collection of geographic features. Contains list of {@link GeoJSONFeature} objects.
     */
    public record GeoJSONFeatureCollection(
        String type,
        List<GeoJSONFeature> features
    ) {}

    /**
     * Represents a single GeoJSON feature, contains {@link GeoJSONProperties} and {@link GeoJSONGeometry} object references.
     */
    public record GeoJSONFeature(
        String type,
        GeoJSONProperties properties,
        GeoJSONGeometry geometry
    ) {}

    /**
     * Represents the properties of a GeoJSON feature, such as the ID and name of the ski run or lift. This is where you can add additional attributes as needed.
     */
    public record GeoJSONProperties(
        String id,
        String name
    ) {}

    /**
     * Represents the geometry of a GeoJSON feature, includes the type and list of coordinates. To make this work for polygons would need create a list of the coordinate list.
     */
    public record GeoJSONGeometry(
        String type,
        List<List<Double>> coordinates 
    ) {}
}
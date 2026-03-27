package net.dasumma1.skiguideapi.rdf_objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new ski lift in the RDF store")
public class RdfSkiLiftRequest {
    private String id;
    private String name;
    private String type;
    private String areaId;

    public RdfSkiLiftRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
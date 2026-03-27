package net.dasumma1.skiguideapi.rdf_objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for creating a new ski area in the RDF store")
public class RdfSkiAreaRequest {
    private String id;
    private String name;

    public RdfSkiAreaRequest() {
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
}
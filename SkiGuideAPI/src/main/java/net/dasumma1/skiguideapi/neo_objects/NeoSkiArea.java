package net.dasumma1.skiguideapi.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SkiArea")
public class NeoSkiArea {
    @Id
    private final String id;
    private final String name;

    public NeoSkiArea(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

package net.dasumma1.skiguideapi.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SkiLift")
public class NeoSkiLift {
    @Id
    private final String id;
    private final String name;

    public NeoSkiLift(String id, String name) {
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

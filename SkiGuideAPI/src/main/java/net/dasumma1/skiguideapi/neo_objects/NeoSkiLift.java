package net.dasumma1.skiguideapi.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SkiLift")
public class NeoSkiLift {
    @Id
    private final String id;
    private final String name;
    private final String type;
    private final String areaId;

    public NeoSkiLift(String id, String name, String type, String areaId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.areaId = areaId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getAreaId() {
        return areaId;
    }
}

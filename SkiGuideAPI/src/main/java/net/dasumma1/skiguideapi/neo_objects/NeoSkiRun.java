package net.dasumma1.skiguideapi.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SkiRun")
public class NeoSkiRun {
    @Id
    private final String id;
    private final String name;
    private final String difficulty;
    private final String areaId;

    public NeoSkiRun(String id, String name, String difficulty, String areaId) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.areaId = areaId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    } 

    public String getDifficulty() {
        return difficulty;
    }

    public String getAreaId() {
        return areaId;
    }
}

package net.dasumma1.skiguideapi.neo_objects;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("SkiRun")
public class NeoSkiRun {
    @Id
    private final String id;
    private final String name;
    private final String areaId;
    private final String difficulty;
    private final String grooming;
    private final Boolean patrolled;
    private final Boolean snowmaking;
    private final Boolean oneway;
    private final Boolean lit;
    private final Boolean gladed;

    public NeoSkiRun(String id, String name, String difficulty, String areaId, String grooming, Boolean patrolled, Boolean snowmaking, Boolean oneway, Boolean lit, Boolean gladed) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.areaId = areaId;
        this.grooming = grooming;
        this.patrolled = patrolled;
        this.snowmaking = snowmaking;
        this.oneway = oneway;
        this.lit = lit;
        this.gladed = gladed;
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

    public String getGrooming() {
        return grooming;
    }

    public Boolean getPatrolled() {
        return patrolled;
    }

    public Boolean getOneway() {
        return oneway;
    }

    public Boolean getSnowmaking() {
        return snowmaking;
    }

    public Boolean getLit() {
        return lit;
    }

    public Boolean getGladed() {
        return gladed;
    }
}

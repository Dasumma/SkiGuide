package net.dasumma1.skiguideapi.area_objects;

import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunPreferencesRequest;

public class GetBestRouteUsingPriorityRunsRequest {
    private RdfSkiRunPreferencesRequest preferences;
    private org.springframework.data.geo.Point start;
    private org.springframework.data.geo.Point end;

    public GetBestRouteUsingPriorityRunsRequest(RdfSkiRunPreferencesRequest preferences, org.springframework.data.geo.Point start, org.springframework.data.geo.Point end) {
        this.preferences = preferences;
        this.start = start;
        this.end = end;
    }

    // Getters and Setters are REQUIRED for Jackson to work
    public RdfSkiRunPreferencesRequest getPreferences() { return preferences; }
    public void setPreferences(RdfSkiRunPreferencesRequest preferences) { this.preferences = preferences; }
    public org.springframework.data.geo.Point getStart() { return start; }
    public void setStart(org.springframework.data.geo.Point start) { this.start = start; }
    public org.springframework.data.geo.Point getEnd() { return end; }
    public void setEnd(org.springframework.data.geo.Point end) { this.end = end; }
}

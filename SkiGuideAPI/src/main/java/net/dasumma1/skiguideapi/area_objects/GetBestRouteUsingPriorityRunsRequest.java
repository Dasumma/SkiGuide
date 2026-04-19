package net.dasumma1.skiguideapi.area_objects;

import org.springframework.data.geo.Point;

import net.dasumma1.skiguideapi.rdf_objects.RdfSkiRunPreferencesRequest;

public class GetBestRouteUsingPriorityRunsRequest {
    private RdfSkiRunPreferencesRequest preferences;
    private Point start;
    private Point end;

    public GetBestRouteUsingPriorityRunsRequest(RdfSkiRunPreferencesRequest preferences, Point start, Point end) {
        this.preferences = preferences;
        this.start = start;
        this.end = end;
    }

    public RdfSkiRunPreferencesRequest getPreferences() { return preferences; }
    public void setPreferences(RdfSkiRunPreferencesRequest preferences) { this.preferences = preferences; }
    public Point getStart() { return start; }
    public void setStart(Point start) { this.start = start; }
    public Point getEnd() { return end; }
    public void setEnd(Point end) { this.end = end; }
}

package net.dasumma1.skiguideapi.area_objects;

import net.dasumma1.skiguideapi.neo_repositories.neo_objects.NeoSkiPoint;
import net.dasumma1.skiguideapi.request_objects.RdfSkiRunPreferencesRequest;

/**
 * Data Transfer Object (DTO) representing a request to calculate the best route 
 * based on specific ski run preferences and geographical constraints.
 * <p>
 * This object bundles the weighted preferences for run attributes (like difficulty 
 * and grooming) with the physical start and end coordinates for the routing engine.
 */
public class GetBestRouteUsingPriorityRunsRequest {

    /** The weighted preferences used to score and filter potential ski runs. */
    private RdfSkiRunPreferencesRequest preferences;
    
    /** The starting geographical point for the route search. */
    private NeoSkiPoint start;
    
    /** The destination geographical point for the route search. */
    private NeoSkiPoint end;

    /**
     * Constructs a new routing request with the specified criteria.
     * @param preferences the user's weighted run preferences
     * @param start       the starting {@link Point}
     * @param end         the destination {@link Point}
     */
    public GetBestRouteUsingPriorityRunsRequest(RdfSkiRunPreferencesRequest preferences, NeoSkiPoint start, NeoSkiPoint end) {
        this.preferences = preferences;
        this.start = start;
        this.end = end;
    }

    /** @return the current user preferences for the route calculation. */
    public RdfSkiRunPreferencesRequest getPreferences() { return preferences; }
    
    /** @param preferences the preferences to be used for run scoring. */
    public void setPreferences(RdfSkiRunPreferencesRequest preferences) { this.preferences = preferences; }
    
    /** @return the starting point of the requested route. */
    public NeoSkiPoint getStart() { return start; }
    
    /** @param start the new starting point for the calculation. */
    public void setStart(NeoSkiPoint start) { this.start = start; }
    
    /** @return the target end point of the requested route. */
    public NeoSkiPoint getEnd() { return end; }
    
    /** @param end the new destination point for the calculation. */
    public void setEnd(NeoSkiPoint end) { this.end = end; }
}
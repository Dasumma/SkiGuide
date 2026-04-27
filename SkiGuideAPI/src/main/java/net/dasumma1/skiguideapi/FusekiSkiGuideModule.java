package net.dasumma1.skiguideapi;

import java.util.Set;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.sys.FusekiModule;
import org.apache.jena.rdf.model.Model;

/**
 * A custom Fuseki Module implementation for the SkiGuide API.
 * <p>
 * This class hooks into the Apache Jena Fuseki server lifecycle, allowing for 
 * custom configuration, initialization logic, or extension of the server's 
 * capabilities when it starts up.
 */
public class FusekiSkiGuideModule implements FusekiModule {

    /**
     * Prepares the Fuseki server builder before the server is built and started.
     * This method is called by the Fuseki framework during the configuration phase.
     *
     * @param serverBuilder the builder used to configure the Fuseki server
     * @param datasetNames  a set of names of the datasets being deployed
     * @param configModel   the RDF model containing the server configuration
     */
    @Override
    public void prepare(FusekiServer.Builder serverBuilder, Set<String> datasetNames, Model configModel) {
        System.out.println("SkiGuideModule started");
    }

    /**
     * Returns the identifying name of this Fuseki module.
     *
     * @return the module name "SkiGuideModule"
     */
    @Override
    public String name() {
        return "SkiGuideModule";
    }

}
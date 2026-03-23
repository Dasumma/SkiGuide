package net.dasumma1.skiguideapi;

import java.util.Set;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.sys.FusekiModule;
import org.apache.jena.rdf.model.Model;

public class FusekiSkiGuideModule implements FusekiModule {
    @Override
    public void prepare(FusekiServer.Builder serverBuilder, Set<String> datasetNames, Model configModel) {
        System.out.println("SkiGuideModule started");
    }

    @Override
    public String name() {
        return "SkiGuideModule";
    }

}

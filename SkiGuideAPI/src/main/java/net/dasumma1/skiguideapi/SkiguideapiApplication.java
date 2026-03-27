package net.dasumma1.skiguideapi;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.sys.FusekiModules;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.tdb2.TDB2Factory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkiguideapiApplication {

	public static void main(String[] args) {
		try{
			startEmbeddedFuseki();
		}
		catch(Exception e){
			System.err.println("Failed to start embedded Fuseki server: " + e.getMessage());
			e.printStackTrace();
		}
		SpringApplication
		.run(SkiguideapiApplication.class, args);
	}

	private static void startEmbeddedFuseki() {
		Dataset dataset = TDB2Factory.connectDataset("data/tdb2");
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		InfModel infModel = ModelFactory.createInfModel(reasoner, dataset.getDefaultModel());
		
		FusekiServer server = FusekiServer.create()
				.add("/dataset", dataset)
				.port(3030)
				.fusekiModules(FusekiModules.create(new FusekiSkiGuideModule()))
				.build();
		server.start();
		System.out.println("Embedded Fuseki started at http://localhost:3030/dataset");
	}
}

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

/**
 * The entry point for the Ski Guide API application.
 * <p>
 * This class bootstrap the Spring Boot environment and initializes an 
 * embedded Apache Jena Fuseki server to handle SPARQL queries and RDF storage.
 */
@SpringBootApplication
public class SkiguideapiApplication {

	/**
	 * Main method to launch the application.
	 * Attempts to start the embedded Fuseki server before initializing the Spring Application context.
	 * * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			startEmbeddedFuseki();
		}
		catch(Exception e) {
			System.err.println("Failed to start embedded Fuseki server: " + e.getMessage());
			e.printStackTrace();
		}
		SpringApplication.run(SkiguideapiApplication.class, args);
	}

	/**
	 * Configures and starts an embedded Apache Jena Fuseki server.
	 * <p>
	 * The server uses TDB2 persistent storage located at "data/tdb2", 
	 * listens on port 3030, and exposes a dataset endpoint at "/dataset".
	 * It also registers the custom {@link FusekiSkiGuideModule}.
	 */
	private static void startEmbeddedFuseki() {
		// Connect to the TDB2 persistent dataset
		Dataset dataset = TDB2Factory.connectDataset("data/tdb2");
		
		// Note: Inference/Reasoning models can be enabled here if required by the ontology
		// Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		// InfModel infModel = ModelFactory.createInfModel(reasoner, dataset.getDefaultModel());
		
		FusekiServer server = FusekiServer.create()
				.add("/dataset", dataset)
				.port(3030)
				.fusekiModules(FusekiModules.create(new FusekiSkiGuideModule()))
				.build();
		
		server.start();
		System.out.println("Embedded Fuseki started at http://localhost:3030/dataset");
	}
}
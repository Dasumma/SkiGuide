package net.dasumma1.skiguideapi;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.sys.FusekiModules;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
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
	 * @param args command line arguments
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
		String tdbPath = "data/tdb2";
		String ontologyFile = "src/main/resources/SkiAreaOntology-NoInstances.owl";

		Dataset dataset = TDB2Factory.connectDataset(tdbPath);

		dataset.begin(ReadWrite.WRITE);
		try {
			if (!dataset.getDefaultModel().isEmpty()) {
				System.out.println("TDB2 is empty. Loading " + ontologyFile + "...");
				
				RDFDataMgr.read(dataset.getDefaultModel(), ontologyFile);
				
				dataset.commit(); 
				System.out.println("Load complete and committed.");
			} else {
				dataset.abort(); 
				System.out.println("TDB2 already contains data. Skipping load.");
			}
		} catch (Exception e) {
			dataset.abort();
			System.err.println("Error loading ontology: " + e.getMessage());
			e.printStackTrace();
		} finally {
			dataset.end();
		}

		// Setup Inference and Server
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		InfModel infModel = ModelFactory.createInfModel(reasoner, dataset.getDefaultModel());
		Dataset infDataset = DatasetFactory.create(infModel);

		FusekiServer server = FusekiServer.create()
				.add("/dataset", infDataset)
				.port(3030)
				.fusekiModules(FusekiModules.create(new FusekiSkiGuideModule()))
				.build();
		
		server.start();
		System.out.println("Embedded Fuseki started at http://localhost:3030/dataset");
	}
}
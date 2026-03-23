package net.dasumma1.skiguideapi;

import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.fuseki.main.sys.FusekiModules;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkiguideapiApplication {

	public static void main(String[] args) {
		startEmbeddedFuseki();
		SpringApplication
		.run(SkiguideapiApplication.class, args);
	}

	private static void startEmbeddedFuseki() {
		Dataset dataset = DatasetFactory.createTxnMem();
		FusekiServer server = FusekiServer.create()
				.add("/dataset", dataset)
				.port(3030)
				.fusekiModules(FusekiModules.create(new FusekiSkiGuideModule()))
				.build();
		server.start();
		System.out.println("Embedded Fuseki started at http://localhost:3030/dataset");
	}
}

package edu.rmit;

import org.neo4j.cypher.CypherException;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: davidsavage
 * Date: 21/08/13
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */

public class EnronIO {
	private String dbPath;
	private String workingDirectory;
	private GraphDatabaseService neo4jDB;
	private ExecutionEngine ex;

	private static enum RelTypes implements RelationshipType {
		EMAILED
	}

	public EnronIO(String workingDirectory) {
		this.workingDirectory = workingDirectory;
		dbPath = workingDirectory + "/neo4j-db";

		//Instantiate the database server object
		neo4jDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);

		//Instantiate the Cypher execution engine
		ex = new ExecutionEngine(neo4jDB);
	}


	public ExecutionResult runCypherQuery(String query) {
		try {
			return ex.execute(query);
		}
		catch(CypherException e) {
			System.err.println("Invalid Cypher query: " + query);
			e.printStackTrace();
			return null;
		}
	}


	public String runCypherQueryToString(String query) {
		ExecutionResult res = runCypherQuery(query);
		if(res != null) {
			return res.dumpToString();
		}
		else {
			return "";
		}
	}


	public void closeDBConnection() {
		neo4jDB.shutdown();
	}


	public void loadEnronDataSet() {
		List<EnronEmail> entries = this.readFromCSV("data/mid_from_to_date_len.txt");
		UniqueFactory<Node> factory;

		Transaction tx = neo4jDB.beginTx();
		try {
			//Create a factory object that stores indices in an index set named employees
			factory = new UniqueFactory.UniqueNodeFactory(neo4jDB, "employees") {
				protected void initialize(Node created, Map<String, Object> properties) {
					created.setProperty("id", properties.get("id"));
				}
			};
		}
		finally {
			tx.finish();
		}

		for(EnronEmail email: entries) {
			addEmail(factory, email);
		}
	}


	public void addEmail(UniqueFactory<Node> factory, EnronEmail email) {
		Node fromEmp, toEmp;
		Relationship rel;

		Transaction tx = neo4jDB.beginTx();
		try {
			//Create the from and to nodes if they don't already exist
			fromEmp = factory.getOrCreate("id", email.to);
			toEmp = factory.getOrCreate("id", email.from);

			//Create a relationship between the nodes representing the email
			rel = fromEmp.createRelationshipTo(toEmp, RelTypes.EMAILED);
			rel.setProperty("date", email.sendDate);
			rel.setProperty("time", email.sendTime);
			rel.setProperty("length", email.length);

			tx.success();
		}
		finally {
			tx.finish();
		}
	}


	private LinkedList<EnronEmail> readFromCSV(String filename) {
		LinkedList<EnronEmail> entries = new LinkedList<EnronEmail>();
		String line;

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while((line = br.readLine()) != null) entries.add(new EnronEmail(line.split(",")));
			br.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		return entries;
	}
}

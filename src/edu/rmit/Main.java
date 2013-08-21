package edu.rmit;

import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Node;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.index.UniqueFactory;

import java.util.List;
import java.util.Map;

public class Main {

	private static final String DB_PATH = "target/neo4j-db";

	private static enum RelTypes implements RelationshipType {
		EMAILED
	}

    public static void main(String[] args) {
	    //Instantiate the database server object
	    GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

	    loadEnronDataSet(graphDB);

	    ExecutionEngine ex = new ExecutionEngine(graphDB);
	    String res = ex.execute(
			    "start n=node(*) return count(n)").dumpToString();
	    System.out.print(res);

	    graphDB.shutdown();
    }


	public static void loadEnronDataSet(GraphDatabaseService graphDB) {
		List<EnronEmail> entries = EnronIO.readFromCSV("/Users/davidsavage/Desktop/mid_from_to_date_len.txt");
		UniqueFactory<Node> factory;

		Transaction tx = graphDB.beginTx();
		try {
			//Create a factory object that stores indices in an index set named employees
			factory = new UniqueFactory.UniqueNodeFactory(graphDB, "employees") {
				protected void initialize(Node created, Map<String, Object> properties) {
					created.setProperty("id", properties.get("id"));
				}
			};
		}
		finally {
			tx.finish();
		}

		for(EnronEmail email: entries) {
			addEmail(graphDB, factory, email);
		}
	}


	public static void addEmail(GraphDatabaseService graphDB, UniqueFactory<Node> factory, EnronEmail email) {
		Node fromEmp, toEmp;
		Relationship rel;

		Transaction tx = graphDB.beginTx();
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
}

package edu.rmit;

import org.neo4j.cypher.CypherException;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private static final GregorianCalendar calendar = new GregorianCalendar();
	
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
	
	public Map generateSubgraphsForWeek(int week, int k) {
		LinkedList<LinkedList<Double>> subgraph;
		HashMap<Integer, DoubleMatrix2D> subgraphs = new HashMap<Integer, DoubleMatrix2D>();

		//Perform the cypher query. For each node n, we want to find all
		//neighbours m, and any relationships between these neighbours
		ExecutionResult result = runCypherQuery(
				"MATCH (ego)-[r1]->(t1) WHERE r1.time = " + week +
				" WITH ego, t1 MATCH p = (t1)-[r2?]->(t2)<-[r3]-(ego)" +
				" WHERE r3.time = " + week + " AND r2.time = " + week +
				" WITH ego.id AS ego, t1.id AS t1, t2.id AS t2, COUNT(p) as paths" +
				" ORDER BY t2 RETURN ego, t1, COLLECT(paths) ORDER BY ego, t1");
		return null;
		/*
		//Go through each row in the result set
		for(Map<String, Object> row: result) {
			Integer fromID = (Integer)row.get("from");
			//If the base node has not yet been added to the map
			if(subgraphs.containsKey(fromID)) {

			} else {
				//We don't know the number of neighbouring nodes
				//yet so we can't instantiate the matrix
				subgraphs.put(fromID, null);
				subgraph = new LinkedList<LinkedList<Double>>();
				subgraph.add(new LinkedList<Double>());
				subgraph.peekLast().add();
			}
			
			//Add the neighbour as a node in the subgraph
			subgraphs.get(fromID).add((Integer)row.get("to"));
		}
		return subgraphs;  */
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
		String enronFile = "data/mid_from_to_date_len.csv";
		//String enronFile = "data/time_from_to.csv";

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

		List<EnronEmail> entries = this.readFromCSV(enronFile);
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
			fromEmp = factory.getOrCreate("id", email.toID);
			toEmp = factory.getOrCreate("id", email.fromID);

			//Create a relationship between the nodes representing the email
			rel = fromEmp.createRelationshipTo(toEmp, RelTypes.EMAILED);
			rel.setProperty("time", email.timeSent);
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
			while((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				entries.add(new EnronEmail(
						/*entries.size(),
						Integer.parseInt(fields[2]),
						Integer.parseInt(fields[1]),
						dateToInt(parseDate(Integer.parseInt(fields[0]))),
						0));*/
						Integer.parseInt(fields[0]),
						Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]),
						dateToInt(parseDate(fields[3])),
						Integer.parseInt(fields[5])));

				//if(entries.size() > 200) break;
			}
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


	public static void printBars(List<Double> list) {
		int maxHeight = 0;
		for(Double v: list) maxHeight = Math.max(maxHeight, (int)Math.ceil(v / 0.5));

		for(int i = 0;i <= maxHeight;i++)  {
			System.out.print((maxHeight - i) + "\t");
			for(Double v: list) {
				int y = (int) Math.ceil(v / 0.5);
				if(y >= maxHeight - i) {
					System.out.print("*");
				}
				else {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}


	private static int dateToInt(Date date) {
		int week = 0;
		calendar.setTime(parseDate("1999-05-11"));
		while(calendar.getTime().before(date)) {
			calendar.add(GregorianCalendar.DATE, 7);
			week++;
		}

		return week;
	}
	
	
	private static Date parseDate(String dateString) {
		try {
			return dateFormatter.parse(dateString);
		}
		catch(ParseException e) {
			System.err.println("Date string " + dateString +
					" is not in the correct format - yyyy-mm-dd");
			return null;
		}
	}

	private static Date parseDate(long seconds) {
		return new Date((long)1000 * seconds);
	}
}

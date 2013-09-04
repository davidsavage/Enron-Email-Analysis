package edu.rmit;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

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

	private String workingDirectory;


	public EnronIO(String workingDirectory) {
		this.workingDirectory = workingDirectory;
	}


	public DirectedGraph<Integer, EnronEmail> loadEnronDataSet() {
		String enronFile = "data/mid_from_to_date_len.csv";
		//String enronFile = "data/time_from_to.csv";

		DirectedGraph<Integer, EnronEmail> graph = new DirectedSparseMultigraph<Integer, EnronEmail>();

		List<EnronEmail> entries = this.readFromCSV(enronFile);
		for(EnronEmail email: entries) {
			graph.addVertex(email.fromID);
			graph.addVertex(email.toID);
			graph.addEdge(email, email.fromID, email.toID);
		}

		return graph;
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

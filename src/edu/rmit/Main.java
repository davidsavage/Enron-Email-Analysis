package edu.rmit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    EnronIO eio = new EnronIO("target");
		EnronEmailGraph graph = null;

	    String line = "";

	    System.out.println("Enter \"quit\" to quit\n");

	    while(!line.equals("quit")) {
		    line = getInputFromPrompt(in).toLowerCase();
			if(line.equals("load")) {
				graph = new EnronEmailGraph(eio.loadEnronDataSet());
			}
			else if(line.equals("ss-norm") && graph != null) {
				List<List> ss = getScanStatistics(graph, true);
				EnronIO.displayAsLinePlot(ss, "Normalised Scan Statistics");
			}
			else if(line.equals("ss") && graph != null) {
				List<List> ss = getScanStatistics(graph, false);
				EnronIO.displayAsLinePlot(ss, "Scan Statistics");
			}
	    }
    }

	public static List<List> getScanStatistics(EnronEmailGraph graph, boolean normalised) {
		EnronScanStatistics ss1 = new EnronScanStatistics(163);
		EnronScanStatistics ss2 = new EnronScanStatistics(163);

		for(int i = 2;i < 163;i++) {
			ss1.addTimeStep(graph.generateSubgraphsForWeek(i, 1));
			ss2.addTimeStep(graph.generateSubgraphsForWeek(i, 2));
		}

		LinkedList<List> ss = new LinkedList<List>();
		if(normalised) {
			ss.add(ss1.getNormalisedScanStatistic(20));
			ss.add(ss2.getNormalisedScanStatistic(20));
		}
		else {
			ss.add(ss1.getScanStatistic());
			ss.add(ss2.getScanStatistic());
		}

		return ss;
	}


	public static String getInputFromPrompt(BufferedReader in) {
		System.out.print("> ");
		try {
			return in.readLine();
		}
		catch(IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}

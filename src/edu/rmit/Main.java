package edu.rmit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
				List<double[]> ss = getScanStatistics(graph, true);
				EnronIO.displayAsLinePlot(ss, "Normalised Scan Statistics");
			}
			else if(line.equals("ss") && graph != null) {
				List<double[]> ss = getScanStatistics(graph, false);
				EnronIO.displayAsLinePlot(ss, "Scan Statistics");
			}
	    }
    }

	public static List<double[]> getScanStatistics(EnronEmailGraph graph, boolean normalised) {
		EnronScanStatistics[] ss = new EnronScanStatistics[5];
		for(int i = 0;i < ss.length;i++) ss[i] = new EnronScanStatistics(163);

		for(int i = 2;i < 163;i++) {//163
			Map<Integer, Double>[] vals = graph.getGraphPropertiesForWeek(i);
			for(int j = 0;j < ss.length;j++) ss[j].addTimeStep(vals[j]);
		}

		LinkedList<double[]> allSS = new LinkedList<double[]>();
		if(normalised) {
			for(int i = 0;i < ss.length;i++) allSS.add(ss[i].getNormalisedScanStatistic(20));
		}
		else {
			for(int i = 0;i < ss.length;i++) allSS.add(ss[i].getScanStatistic());
		}

		return allSS;
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

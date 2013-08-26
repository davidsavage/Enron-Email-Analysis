package edu.rmit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    EnronIO eio = new EnronIO("target");
		EnronScanStatistics ss = new EnronScanStatistics();
	    String line = "";

	    System.out.println("Enter \"quit\" to quit\n");

	    while(!line.equals("quit")) {
		    line = getInputFromPrompt(in).toLowerCase();
			if(line.equals("load")) {
				eio.loadEnronDataSet();
			}
			else if(line.equals("ss-degree")) {
				ss.addTimeStep(eio.generateSubgraphsForWeek(2, 1));
			}
			else if(!line.equals("quit")) {
		        String res = eio.runCypherQueryToString(line);
		        System.out.println(res);
		    }
	    }

	    eio.closeDBConnection();
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

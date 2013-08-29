package edu.rmit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    EnronIO eio = new EnronIO("target");
		EnronScanStatistics ss = new EnronScanStatistics(163);
	    String line = "";

	    System.out.println("Enter \"quit\" to quit\n");

	    while(!line.equals("quit")) {
		    line = getInputFromPrompt(in).toLowerCase();
			if(line.equals("load")) {
				eio.loadEnronDataSet();
			}
			else if(line.equals("ss-degree")) {
				for(int i = 2;i < 150;i++) {
					ss.addTimeStep(eio.generateSubgraphsForWeek(i, 1));
				}
				EnronIO.printBars(ss.getScanStatistic());
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

package edu.rmit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    EnronIO eio = new EnronIO("target");
	    String line = "";

        System.out.println("Reload Enron Data Set?");
	    line = getInputFromPrompt(in).toLowerCase();
		if(line.equals("yes") || line.equals("y")) {
			eio.loadEnronDataSet();
		}

	    System.out.println("Enter Cypher queries to interact with the database, e.g.");
	    System.out.println("\"start n=node(*) return count(n)\"\n");
	    System.out.println("Enter \"quit\" to quit\n");

	    while(!line.toLowerCase().equals("quit")) {
		    line = getInputFromPrompt(in);
		    if(!line.toLowerCase().equals("quit")) {
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

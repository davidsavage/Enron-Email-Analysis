package edu.rmit;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: davidsavage
 * Date: 21/08/13
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */

public class EnronIO {
    public static List<EnronEmail> readFromCSV(String filename) {
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rmit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author e10483
 */
public class EnronScanStatistics {
	//List of lists to hold the statistic for each vertex
	private HashMap<Integer, int[]> degree;
	private LinkedList<Double> scanStatistic;
	private int numWeeks;
	
	public EnronScanStatistics(int numWeeks) {
		degree = new HashMap<Integer, int[]>();
		scanStatistic = new LinkedList<Double>();
		this.numWeeks = numWeeks;
	}
	
	public void addTimeStep(Map<Integer, List> subgraphs) {
		double vdm, vdv, maxSS = 0.0;
		int currDegree;
		int tau = 5;
		
		int currWeek = scanStatistic.size();

		//Go through each vertex in the graph
		for(Integer fromID: subgraphs.keySet()) {
			//if this is the first time this vertex has been seen, create a new entry in the hash table and initialise
			if(degree.get(fromID) == null) degree.put(fromID, new int[numWeeks]);
			
			//Calculate the raw statistic and then store the value in the list associated with the current vertex
			currDegree = subgraphs.get(fromID).size();
			degree.get(fromID)[currWeek] = currDegree;
			
			if(currWeek - tau > 0) {
				//Standardise the statistic
				vdm = vertexDependentMean(currWeek, tau, degree.get(fromID));
				vdv = Math.max(1.0, vertexDependentVariance(currWeek, tau, vdm, degree.get(fromID)));
				maxSS = Math.max(maxSS, (currDegree - vdm) / vdv);
			}
			else {
				maxSS = Math.max(maxSS, currDegree);
			}
		}
		scanStatistic.add(maxSS);
	}

	public double vertexDependentMean(int t, int tau, int[] rawStatistic) {
		double summation = 0;
		
		for(int i = t - tau;i < t;i++) {
			summation += (double)rawStatistic[i];
		}
		
		return summation / (double)tau;
	}
	
	public double vertexDependentVariance(int t, int tau, double vertexDependentMean, int[] rawStatistic) {
		double summation = 0;
		
		for(int i = t - tau;i < t;i++) {
			summation += Math.pow((double)rawStatistic[i] - vertexDependentMean, 2);
		}
		
		return summation / (double)(tau - 1);
	}
	
	public List getScanStatistic() {
		return scanStatistic;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rmit;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;

/**
 *
 * @author e10483
 */
public class EnronScanStatistics {
	private static final int tau = 20;

	//List of lists to hold the statistic for each vertex
	private HashMap<Integer, Double[]> degree;
	private LinkedList<Double> scanStatistic;
	private LinkedList<Double> normalisedScanStatistic;
	private int numWeeks;


	public EnronScanStatistics(int numWeeks) {
		degree = new HashMap<Integer, Double[]>();
		scanStatistic = new LinkedList<Double>();
		normalisedScanStatistic = new LinkedList<Double>();
		this.numWeeks = numWeeks;
	}


	public void addTimeStep(Map<Integer, Graph<Integer, EnronEmail>> subgraphs) {
		double vdm, vdv, maxSS = 0.0;
		int currDegree;
		
		int currWeek = scanStatistic.size();

		//Go through each vertex in the graph
		for(Integer fromID: subgraphs.keySet()) {
			//if this is the first time this vertex has been seen,
			//create a new entry in the hash table and initialise
			if(!degree.containsKey(fromID)) {
				degree.put(fromID, new Double[numWeeks]);
			}

			//Calculate the raw statistic and store in the array associated with the current vertex
			currDegree = subgraphs.get(fromID).getEdgeCount();
			degree.get(fromID)[currWeek] = Double.valueOf((double)currDegree);


			if(currWeek - tau >= 0) {
				//Standardise the statistic (eq. 6 - 8) and take the maximum value across all nodes
				maxSS = Math.max(maxSS, standardise(currWeek, tau, degree.get(fromID)));
			}
			else {
				maxSS = Math.max(maxSS, currDegree);
			}
		}

		scanStatistic.add(maxSS);
	}


	public double standardise(int t, int tau, Double[] rawStatistic) {
		double rmean = runningMean(t, tau, rawStatistic);
		double rvar = Math.max(1.0, runningVariance(t, tau, rmean, rawStatistic));
		return (rawStatistic[t] - rmean) / Math.max(1.0, Math.sqrt(rvar));
	}


	public double runningMean(int t, int tau, Double[] rawStatistic) {
		double summation = 0;
		
		for(int i = t - tau;i < t;i++) {
			summation += (double)rawStatistic[i];
		}
		
		return summation / (double)tau;
	}


	public double runningVariance(int t, int tau, double runningMean, Double[] rawStatistic) {
		double summation = 0;
		
		for(int i = t - tau;i < t;i++) {
			summation += Math.pow((double)rawStatistic[i] - runningMean, 2);
		}
		
		return summation / (double)(tau - 1);
	}

	public List<Double> getNormalisedScanStatistic(int tau) {
		LinkedList<Double> norm = new LinkedList<Double>();
		Double[] ssArray = new Double[scanStatistic.size()];
		scanStatistic.toArray(ssArray);

		for(int t = tau; t < ssArray.length;t++) {
			norm.add(standardise(t, tau, ssArray));
		}

		return norm;
	}

	public List getScanStatistic() {
		return scanStatistic;
	}
}

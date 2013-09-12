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
	private HashMap<Integer, double[]> rawStatistic;
	private double[] scanStatistic;
	private double[] normalisedScanStatistic;
	private int timeStep;


	public EnronScanStatistics(int numWeeks) {
		rawStatistic = new HashMap<Integer, double[]>();
		scanStatistic = new double[numWeeks];
		normalisedScanStatistic = new double[numWeeks];
		timeStep = 0;
	}


	public void addTimeStep(Map<Integer, Double> vals) {
		double maxSS = 0.0;

		//Go through each vertex in the graph
		for(Integer fromID: vals.keySet()) {
			//if this is the first time this vertex has been seen,
			//create a new entry in the hash table and initialise
			if(!rawStatistic.containsKey(fromID)) {
				rawStatistic.put(fromID, new double[scanStatistic.length]);
			}

			//Calculate the raw statistic and store in the array associated with the current vertex
			rawStatistic.get(fromID)[timeStep] = vals.get(fromID);

			if(timeStep - tau >= 0) {
				//Standardise the statistic (eq. 6 - 8) and take the maximum value across all nodes
				maxSS = Math.max(maxSS, standardise(timeStep, tau, rawStatistic.get(fromID)));
			}
			else {
				maxSS = Math.max(maxSS, rawStatistic.get(fromID)[timeStep]);
			}
		}

		scanStatistic[timeStep] = maxSS;
		timeStep++;
	}


	public double standardise(int t, int tau, double[] rawStatistic) {
		double rmean = runningMean(t, tau, rawStatistic);
		double rvar = Math.max(1.0, runningVariance(t, tau, rmean, rawStatistic));
		return (rawStatistic[t] - rmean) / Math.max(1.0, Math.sqrt(rvar));
	}


	public double runningMean(int t, int tau, double[] rawStatistic) {
		double summation = 0;
		
		for(int i = t - tau;i < t;i++) {
			summation += (double)rawStatistic[i];
		}
		
		return summation / (double)tau;
	}


	public double runningVariance(int t, int tau, double runningMean, double[] rawStatistic) {
		double summation = 0;
		
		for(int i = t - tau;i < t;i++) {
			summation += Math.pow((double)rawStatistic[i] - runningMean, 2);
		}
		
		return summation / (double)(tau - 1);
	}


	public double[] getNormalisedScanStatistic(int tau) {
		double[] norm = new double[scanStatistic.length];

		for(int t = tau; t < scanStatistic.length;t++) {
			norm[t] = standardise(t, tau, scanStatistic);
		}

		return norm;
	}

	public double[] getScanStatistic() {
		return scanStatistic;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rmit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author e10483
 */
public class EnronScanStatistics {
	private LinkedList<Integer> degree;
	
	public EnronScanStatistics() {
		degree = new LinkedList<Integer>();
	}
	
	public void addTimeStep(Map<String, List> subgraphs) {
		int maxDegree = 0;
		for(List subgraph: subgraphs.values()) {
			maxDegree = Math.max(maxDegree, subgraph.size());
		}
		this.degree.add(Integer.valueOf(maxDegree));
		System.out.println(degree);
	}

	public double vertexDependentMean(int t, int tau, List<Double> rawStatistic) {
		double summation = 0;
		
		Iterator<Double> it = rawStatistic.listIterator(t - tau);
		while(it.hasNext()) {
			summation += it.next().doubleValue();
		}
		
		return summation / (double)tau;
	}
}

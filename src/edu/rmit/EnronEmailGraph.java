package edu.rmit;

/**
 * Created with IntelliJ IDEA.
 * User: e10483
 * Date: 4/09/13
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import org.apache.commons.collections15.Predicate;
import java.util.HashMap;
import java.util.Map;



import java.util.Set;

public class EnronEmailGraph {
	private DirectedGraph<Integer, EnronEmail> emailGraph;

	public EnronEmailGraph(DirectedGraph<Integer, EnronEmail> emailGraph) {
		this.emailGraph = emailGraph;
		System.out.println(emailGraph.getVertexCount());
	}


	public Map<Integer, Graph<Integer, EnronEmail>> generateSubgraphsForWeek(int week, int k) {
		//Create a list to hold the subgraphs
		Map<Integer, Graph<Integer, EnronEmail>> subgraphs = new HashMap<Integer, Graph<Integer, EnronEmail>>();

		//Filter the edges, leaving only those representing emails sent in the given week
		EdgePredicateFilter<Integer, EnronEmail> edgeFilter =
				new EdgePredicateFilter<Integer, EnronEmail>(new TimePredicate<EnronEmail>(week));
		Graph<Integer, EnronEmail> byWeek = edgeFilter.transform(emailGraph);

		//Go through each vertex in the graph
		for(Integer v: byWeek.getVertices()) {
			//Find the k-induced subgraph
			KNeighborhoodFilter<Integer, EnronEmail> kFilter =
					new KNeighborhoodFilter<Integer, EnronEmail>(v, k, KNeighborhoodFilter.EdgeType.OUT);

			//Add the subgraph to the list
			subgraphs.put(v, kFilter.transform(byWeek));
		}

		return subgraphs;
	}


    public double averageBetweenessConnectivity(Integer ego, Graph<Integer, EnronEmail> subgraph) {
        DijkstraShortestPath path = new DijkstraShortestPath(subgraph);
        int abc = 0;

        for(Integer n1: subgraph.getVertices()) {
            for(Integer n2: subgraph.getVertices()) {
                for(Object email: path.getPath(n1, n2)) {
                    if(((EnronEmail)email).fromID == ego) abc++;
                }
            }
        }

        return (double) abc;
    }


	public Map<Integer, Double>[] getGraphPropertiesForWeek(int week) {
		Map<Integer, Double>[] props = new HashMap[5];
		for(int i = 0;i < props.length;i++) props[i] = new HashMap<Integer, Double>();

		//Get the k-induced subgraphs
		Map<Integer, Graph<Integer, EnronEmail>> subgraphs = generateSubgraphsForWeek(week, 1);
		for(Integer v: subgraphs.keySet()) {
			props[0].put(v, (double) subgraphs.get(v).getEdgeCount());
			props[1].put(v, (double) subgraphs.get(v).getVertexCount());
		}

		subgraphs = generateSubgraphsForWeek(week, 2);
		for(Integer v: subgraphs.keySet()) {
			props[2].put(v, (double) subgraphs.get(v).getEdgeCount());
            props[3].put(v, averageBetweenessConnectivity(v, subgraphs.get(v)));
		}

		return props;
	}

	
	private class TimePredicate<T> implements Predicate<T> {
		private int time;
		
		public TimePredicate(int time) {
			this.time = time;
		}

		public boolean evaluate(T e) {
			return ((EnronEmail)e).timeSent == time;
		}
	}
}

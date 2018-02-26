package edu.unh.cs980.PageRank;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class pageRank {

	static int noofedges = 0;

	/**
	 * Convergence criterion: Transition probabilities do not change more that
	 * EPSILON from one iteration to another.
	 */
	final static double EPSILON = 0.0001;

	/**
	 * Never do more than this number of iterations regardless of whether the
	 * transistion probabilities converge or not.
	 */
	final static int MAX_NUMBER_OF_ITERATIONS = 1000;

	/**
	 * When the user is bored and wants to surf other page Random Jump factor
	 */
	final static double alpha = 0.15;

	/* --------------------------------------------- */
	/**
	 * 
	 * @param graph_text_file
	 * @return
	 * @throws IOException
	 */
	private static HashMap<String, ArrayList<String>> readDocs(String graph_text_file) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<String>> adj_matrix = new HashMap<String, ArrayList<String>>();
		BufferedReader br = new BufferedReader(new FileReader(new File(graph_text_file)));
		String line = br.readLine();
		while (line != null) {
			String[] tokens = line.split("\t"); // handle white space
			ArrayList<String> targets = new ArrayList<String>();
			for (int i = 1; i < tokens.length; i++) {
				targets.add(tokens[i]);
				noofedges++;
				
				
			}

			adj_matrix.put(tokens[0], targets);
			line = br.readLine();
		}

		return adj_matrix;
	}

	/* ---------------------PageRank- computation ------------------------ */
	private static void computePagerank(HashMap<String, ArrayList<String>> pageMatrix) {
		// TODO Auto-generated method stub
		boolean convergence = false;
		HashMap<String, Double> newPageRankScore = new HashMap<String, Double>();
		HashMap<String, Double> oldPageRankScore = new HashMap<String, Double>();
		for (String node : pageMatrix.keySet()) {
			newPageRankScore.put(node,  (double) (1 / noofedges));
			oldPageRankScore.put(node, newPageRankScore.get(node));
			
		}

		
		while (!convergence) {
			for (String node : newPageRankScore.keySet()) {
				double sinkScore = 0;
				for (String oldnode : newPageRankScore.keySet()) {
					if (oldnode.equals(node))
						continue;
					else if (pageMatrix.get(oldnode).contains(node)) {
						sinkScore += newPageRankScore.get(oldnode) / pageMatrix.get(oldnode).size();
					}

				}
				newPageRankScore.put(node, alpha / noofedges + (1 - alpha) * sinkScore);
			}

			// Check it converges
			for (String node : newPageRankScore.keySet()) {
				if (Math.abs(newPageRankScore.get(node) - oldPageRankScore.get(node)) > EPSILON)
					convergence = true;
				else
					convergence = false;
			}
			for (String node : newPageRankScore.keySet()) {
				oldPageRankScore.put(node, newPageRankScore.get(node));
			}

		}

		// Normalize
		double sumscore = 0;
		for (String n : newPageRankScore.keySet())
			sumscore += newPageRankScore.get(n);
		for (String n : newPageRankScore.keySet())
			newPageRankScore.put(n, newPageRankScore.get(n) / sumscore);

		LinkedHashMap<String, Double> sortedHashMap = sortMapByValues(newPageRankScore);
		
		for(String node : sortedHashMap.keySet())
		{
			System.out.println(node+ " - "+newPageRankScore.get(node));
		}
		
		
		
	}
	
	
	// Sort the map
	public static LinkedHashMap<String, Double> sortMapByValues (HashMap<String, Double> passedmap)
	{
		List<String> mapkeys = new ArrayList<>(passedmap.keySet());
		List<Double> mapValues = new ArrayList<>(passedmap.values());
		Collections.reverse(mapkeys);
		Collections.reverse(mapValues);
		LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>(); 
		
		Iterator<Double> valuesIt = mapValues.iterator();
		while(valuesIt.hasNext())
		{
			Double val = valuesIt.next();
			Iterator<String> keyIt = mapkeys.iterator();
			while(keyIt.hasNext())
			{
				String key = keyIt.next();
				Double comp1 = passedmap.get(key);
				Double comp2 = val;
				if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
			}
		}
		
		return sortedMap;
		
	}

	/* ---------------------Main method ------------------------ */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		HashMap<String, ArrayList<String>> pageMatrix = readDocs(args[0]);
		System.out.println("PageRank");
		System.out.println("Number of components: 1");
		System.out.println("Number of nodes: " + pageMatrix.size());
		System.out.println("Number of edges: " + noofedges);
		System.out.println("Random Jump Factor: " + alpha);
		
		/*---------------------------------------------------------------------*/
		System.out.println("Personalized PageRank");
		System.out.println("Number of components: 1");
		System.out.println("Number of nodes: " + pageMatrix.size());
		System.out.println("Number of edges: " + noofedges);
		System.out.println("Random Jump Factor: " + alpha);

		computePagerank(pageMatrix);

	}

}

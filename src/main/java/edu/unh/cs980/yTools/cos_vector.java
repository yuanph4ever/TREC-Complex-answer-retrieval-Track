package edu.unh.cs980.yTools;

import java.util.ArrayList;

//56341

public class cos_vector {
	
	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	public static void main(String[] args) {
		double[] a = {0.1, 0.1, 0.1};
		double[] b = {0.1, 0.2, 0.3};
		double[] c = {0.3, 0.4, 0.5};
		double cos = cosineSimilarity(a, b);
		System.out.println(cos);
		cos = cosineSimilarity(a, c);
		System.out.println(cos);
		
		ArrayList<double[]> al = new ArrayList<double[]>();
		
		al.add(a);
		al.add(b);
		al.add(c);
		
		double[] aa = al.get(2);
		
		cos = cosineSimilarity(a, aa);
		System.out.println(cos);
		
		String[] d = {"0.5", "0.6", "0.7"};
		double[] parsed = new double[d.length];
		for (int i = 0; i<d.length; i++) 
			parsed[i] = Double.valueOf(d[i]);
		
		cos = cosineSimilarity(a, parsed);
		System.out.println(cos);
		
		
	}

}

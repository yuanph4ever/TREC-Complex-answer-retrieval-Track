
package edu.unh.cs980;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import edu.unh.cs980.Classifier.Classify;
import edu.unh.cs980.Classifier.ClassifyPassageHeadings;
import edu.unh.cs980.RetrievalModel.BM25;
import edu.unh.cs980.entitiesExpansion.QueryExpansionWithEntities;
import edu.unh.cs980.kmeans.QueryByCluster;
import weka.classifiers.Classifier;

public class Main {

	

	public static void main(String[] args) throws Exception {

		

		System.setProperty("file.encoding", "UTF-8");


		String pagesFile = args[0];
		String indexPath = args[1];
		String outputPath = args[2];



		BM25 bm25 = new BM25(pagesFile, indexPath, outputPath);
		
		
		
	}

}

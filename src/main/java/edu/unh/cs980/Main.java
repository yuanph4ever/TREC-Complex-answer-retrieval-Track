
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

	private static void usage() {
		System.out.println(
				"Command line parameters:Method_Signal Outline_Cbor Lucene_INDEX Output_Dir kmeans_clu_index_Dir types_clu_index_Dir");
		System.exit(-1);
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 4)
			usage();

		System.setProperty("file.encoding", "UTF-8");

		String method_signal = args[0];
		String pagesFile = args[1];
		String indexPath = args[2];
		String outputPath = args[3];

		String kmeans_clu_index = args[4];
		String types_clu_index = args[5];

		int num_of_runfile = 0;

		System.out.println("Get method signal: " + method_signal);
		System.out.println("Start searching and generating runfiles...");

		/*
		 * Query Expansion with entities, use top 1, 2, 3, 4, 5
		 */
		if (method_signal.equals("-exp")) {
			System.out.println("Start Query Expansion with Entities");
			for (int i = 1; i < 6; i++) {
				QueryExpansionWithEntities qewe = new QueryExpansionWithEntities(pagesFile, indexPath, outputPath, i);
				num_of_runfile++;
			}
			System.out.println("Query Expansion with entities DONE");
		}

		/*
		 * Query by using kmeans clusters
		 */
		else if (method_signal.equals("-kmeansClu")) {
			System.out.println("Start Query by K-means Cluster");
			QueryByCluster qbk = new QueryByCluster(pagesFile, indexPath, "-k", kmeans_clu_index, outputPath);
			num_of_runfile++;
			System.out.println("Query by K-means Cluster DONE");
		}

		/*
		 * Map<String, List<String>> map_qid_ptext = qbk.getModel();
		 * 
		 * int count = 0; for (Map.Entry<String,List<String>> entry :
		 * map_qid_ptext.entrySet()) { System.out.println("Key = " +
		 * entry.getKey()); System.out.println("Value = " + entry.getValue());
		 * count ++; }
		 * 
		 * System.out.println("number of query is " + count);
		 */

		/*
		 * Query by using types clusters
		 */
		else if (method_signal.equals("-typesClu")) {
			System.out.println("Start Query by Types Cluster");
			QueryByCluster qbc = new QueryByCluster(pagesFile, indexPath, "-c", types_clu_index, outputPath);
			num_of_runfile++;
			System.out.println("Query by Types Cluster DONE");
		}

		else if (method_signal.equals("-classify")) {
			BM25 bm25baseline = new BM25(pagesFile, indexPath, outputPath);

			Map<String, String> pagepassage = bm25baseline.getPageHeadingMap();
			Map<String, String> sectionpassage = bm25baseline.getSectionHeadingMap();

			Classify classifyPage = new Classify(pagepassage, outputPath, pagesFile, indexPath);
			num_of_runfile++;
		}
		
		// Print usage
		else {
			usage();
		}

		System.out.println("All works DONE. Generate " + num_of_runfile + " runfiles in " + outputPath);

	}

}

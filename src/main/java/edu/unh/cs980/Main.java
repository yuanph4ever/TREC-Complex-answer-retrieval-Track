package edu.unh.cs980;

import java.io.File;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import edu.unh.cs980.Classifier.ClassifierModel;
import edu.unh.cs980.Classifier.Classify;
import edu.unh.cs980.Classifier.ReadRunFileAndClassify;
import edu.unh.cs980.RetrievalModel.BM25;
import edu.unh.cs980.TrainClassifier.TrainSet;
import edu.unh.cs980.entitiesExpansion.QueryExpansionWithEntities;
import edu.unh.cs980.kmeans.QueryByCluster;
import weka.classifiers.Classifier;

import java.time.Clock;

public class Main {

	private static void usage() {
		System.out.println(
				"Command line parameters:Method_Signal Outline_Cbor Lucene_INDEX Output_Dir *kmeans_clu_index_Dir/types_clu_index_Dir");
		System.out.println("Methods_Signal: ");
		System.out.println("  -exp: query expansion with entities");
		System.out.println("  -kmeansClu: query by using kmeans clusters");
		System.out.println("  -typesClu: query by using types clusters");
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
		String clu_index = "";

		if (args.length == 5) {

			if (method_signal.equals("-kmeansClu") || method_signal.equals("-typesClu")) {
				clu_index = args[4];
			} else {
				usage();
			}

		}

		int num_of_runfile = 0;

		// Make directory if one does'nt exist.......
		String directoryName = outputPath;
		File directory = new File(directoryName);
		if (!directory.exists())
			directory.mkdirs();

		outputPath = directory.getPath();

		System.out.println("Get method signal: " + method_signal);
		System.out.println("Start searching and generating runfiles...");

		/*
		 * Query Expansion with entities, use top 1, 2, 3, 4, 5
		 */
		if (method_signal.equals("-exp")) {
			System.out.println("Start Query Expansion with Entities");
			for (int i = 1; i < 6; i++) {
				QueryExpansionWithEntities qewe = new QueryExpansionWithEntities("page", pagesFile, indexPath,
						outputPath, i);
				System.out.println("Query Expansion with top " + i + " DONE");
				num_of_runfile++;
			}
			System.out.println("Query Expansion with entities DONE");
		}

		/*
		 * Query by using kmeans clusters
		 */
		else if (method_signal.equals("-kmeansClu")) {
			System.out.println("Start Query by K-means Cluster");
			QueryByCluster qbk = new QueryByCluster("page", pagesFile, indexPath, "-k", clu_index, outputPath);
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
			QueryByCluster qbc = new QueryByCluster("section", pagesFile, indexPath, "-c", clu_index, outputPath);
			num_of_runfile++;
			System.out.println("Query by Types Cluster DONE");
		}

		else if (method_signal.equals("-classify")) {

			// Nithin - methods

			// Train
			String paraPageTrain = "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-page--title-ql-none--Text-std-k1000-benchmarkY1train.v201.cbor.outlines.run";
			String paraSectionTrain = "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-section--sectionPath-ql-none--Text-std-k1000-benchmarkY1train.v201.cbor.outlines.run";
			// Test
			String paraPageTest = "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-page--title-ql-none--Text-std-k1000-benchmarkY1test.v201.cbor.outlines.run";
			String paraSectionTest = "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-section--sectionPath-ql-none--Text-std-k1000-benchmarkY1test.v201.cbor.outlines.run";
			/**********************************************************************************************************/

			System.out.println("======================= BaseLine Candidate set=====================================");
			//BM25 bm25 = new BM25(pagesFile, indexPath, outputPath);

			/**********************************************************************************************************/

			System.out.println("======================= Classifying BM25 Set =====================================");
			//Classify classifyPage = new Classify(outputPath, pagesFile, indexPath);

			

			/******************************************** Classify K-means ********************************************/
			
			System.out.println("Classifiying Kmeans Resuslt");
			String kmeansRunFile = outputPath + "/train_runfile_section_cluster_kmeans_20k";
			File isFileExist = new File(kmeansRunFile);
			if(!isFileExist.exists())
			{
				System.out.println("Kmeans output doesnot exist - moving to classifying candidate set");
			}
			else
			{
				//ReadRunFileAndClassify rrfcSecKMeans = new ReadRunFileAndClassify(kmeansRunFile, indexPath, outputPath,"kmeanssection");
			}
			
			/**********************************************************************************************************/

			System.out.println("======================= Classifying Laura Candidate Set ===========================");
			ReadRunFileAndClassify rrfc = new ReadRunFileAndClassify(paraPageTrain, indexPath, outputPath, "para");
			ReadRunFileAndClassify rrfcSec = new ReadRunFileAndClassify(paraSectionTrain, indexPath, outputPath,
					"section");
			System.out.println(" =================All works done ==================================================");

			/**********************************************************************************************************/
		}

		// Print usage
		else {
			usage();
		}

		System.out.println("All works DONE. Generate " + num_of_runfile + " runfiles in " + outputPath);
		System.out.println("All works DONE. Generate " + num_of_runfile + " runfiles in " + outputPath);

	}

}

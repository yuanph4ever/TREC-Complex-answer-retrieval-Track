package edu.unh.cs980;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import edu.unh.cs980.Classifier.ClassifyPassageHeadings;
import edu.unh.cs980.RetrievalModel.BM25;
import edu.unh.cs980.TopicModel.CreateArrfDataset;
import edu.unh.cs980.TopicModel.CreateTestSet;
import edu.unh.cs980.TopicModel.TopicModelGenerator;
import edu.unh.cs980.entitiesExpansion.QueryExpansionWithEntities;
import edu.unh.cs980.kmeans.QueryByCluster;
import weka.classifiers.Classifier;

public class Main {
	
	private static void usage() {
        System.out.println("Command line parameters: Outline_Cbor Lucene_INDEX Output_Dir kmeans_clu_index_Dir types_clu_index_Dir");
        System.exit(-1);
    }

	public static void main(String[] args) throws Exception {
		
		if (args.length < 5)
            usage();
		
		System.setProperty("file.encoding", "UTF-8");

		String pagesFile = args[0];
		String indexPath = args[1];
		String outputPath = args[2];
		
		String kmeans_clu_index = args[3];
		String types_clu_index = args[4];
		
		int num_of_runfile = 0;
		
		System.out.println("Start searching and generating runfiles...");
		
		/*
		 * Query Expansion with entities, use top 1, 2, 3, 4, 5
		 */
		
		System.out.println("Start Query Expansion with Entities");
		for(int i = 1; i < 6; i ++) {
			QueryExpansionWithEntities qewe = new QueryExpansionWithEntities(pagesFile, indexPath, outputPath, i);
			num_of_runfile ++;
		}
		System.out.println("Query Expansion with entities DONE");
		
		/*
		 * Query by using kmeans clusters
		 */
		System.out.println("Start Query by K-means Cluster");
		QueryByCluster qbk = new QueryByCluster(pagesFile, indexPath, "-k", kmeans_clu_index, outputPath);
		num_of_runfile ++;
		System.out.println("Query by K-means Cluster DONE");
		
		/*
		 * Query by using types clusters
		 */
		System.out.println("Start Query by Types Cluster");
		QueryByCluster qbc = new QueryByCluster(pagesFile, indexPath, "-c", types_clu_index, outputPath);
		num_of_runfile ++;
		System.out.println("Query by Types Cluster DONE");
		
		
		// Start searching for the passages
//		BM25 bm25 = new BM25(pagesFile, indexPath, outputPath);
//		
//		Map<String, List<String>> pageHeadingMap = bm25.getPageHeadingMap();
//		Map<String, List<String>> sectionHeadingMap = bm25.getSectionHeadingMap();
		
		
//		TopicModelGenerator tmg = new TopicModelGenerator(pageHeadingMap, outputPath+"/trainPageHeading");
//		
//		System.out.println("Training Set Generated");
//		
//		//ClassifyPassageHeadings cpf =  new ClassifyPassageHeadings("/Users/Nithin/Desktop/Runfile/trainPageHeading");
//		
//		Classifier cls = (Classifier) weka.core.SerializationHelper.read("/Users/Nithin/git/TREC-Complex-answer-retrieval-Track/trainedModel/RF_Page.model");
//		
//		System.out.println();
		
//		CreateTestSet cts = new CreateTestSet(pageHeadingMap, outputPath+"testArrff");
		
		System.out.println("All works DONE. Generate " + num_of_runfile + " runfiles in " + outputPath);

	}

}

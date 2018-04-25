package edu.unh.cs980;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import edu.unh.cs980.Classifier.ClassifyPassageHeadings;
import edu.unh.cs980.RetrievalModel.BM25;
import edu.unh.cs980.TopicModel.TopicModelGenerator;
import edu.unh.cs980.entitiesExpansion.QueryExpansionWithEntities;
import edu.unh.cs980.kmeans.QueryByCluster;
import weka.classifiers.Classifier;

import java.time.Clock; 

public class Main {

	private static void usage() {
        System.out.println("Command line parameters:Method_Signal Outline_Cbor Lucene_INDEX Output_Dir *kmeans_clu_index_Dir/types_clu_index_Dir");
        System.out.println("Methods_Signal: ");
        System.out.println("  -exp: query expansion with entities");
        System.out.println("  -kmeansClu: query by using kmeans clusters");
        System.out.println("  -typesClu: query by using types clusters");
        System.exit(-1);
    }

	public static void main(String[] args) throws Exception {
		
		if (args.length < 4)
            usage();
		
		Clock clock = Clock.systemUTC();  
		
		System.setProperty("file.encoding", "UTF-8");
		
		String method_signal = args[0];
		String pagesFile = args[1];
		String indexPath = args[2];
		String outputPath = args[3];
		String clu_index = "";
		
		if( args.length == 5 ) {
			
			if( method_signal.equals("-kmeansClu") || method_signal.equals("-typesClu") ) {
				clu_index = args[4];
			}else {
				usage();
			}
			
		}
		
		int num_of_runfile = 0;
		
		System.out.println("Get method signal: " + method_signal);
		System.out.println("Start searching and generating runfiles...");
		
		String start_time = clock.instant().toString();
		System.out.println("start time: " + start_time); 

		/*
		 * Query Expansion with entities, use top 1, 2, 3, 4, 5
		 */		
		if(method_signal.equals("-exp")) {
			System.out.println("Start Query Expansion with Entities");
			for(int i = 1; i < 6; i ++) {
				QueryExpansionWithEntities qewe = new QueryExpansionWithEntities("section", pagesFile, indexPath, outputPath, i);
				System.out.println("Query Expansion with top " + i + " DONE");
				num_of_runfile ++;
			}
			System.out.println("Query Expansion with entities DONE");
		}
	
		
		/*
		 * Query by using kmeans clusters
		 */
		else if(method_signal.equals("-kmeansClu")) {
			System.out.println("Start Query by K-means Cluster");
			QueryByCluster qbk = new QueryByCluster("page", pagesFile, indexPath, "-k", clu_index, outputPath);
			num_of_runfile ++;
			System.out.println("Query by K-means Cluster DONE");
		}
		
		/*
		Map<String, List<String>> map_qid_ptext = qbk.getModel();
		
		int count = 0;
		for (Map.Entry<String,List<String>> entry : map_qid_ptext.entrySet()) {
			System.out.println("Key = " + entry.getKey());
			System.out.println("Value = " + entry.getValue());
			count ++;
		}
            
		System.out.println("number of query is " + count);
		*/
		
		/*
		 * Query by using types clusters
		 */
		else if(method_signal.equals("-typesClu")) {
			System.out.println("Start Query by Types Cluster");
			QueryByCluster qbc = new QueryByCluster("section", pagesFile, indexPath, "-c", clu_index, outputPath);
			num_of_runfile ++;
			System.out.println("Query by Types Cluster DONE");
		}

		else {
			usage();
		}
		
		System.out.println("All works DONE. Generate " + num_of_runfile + " runfiles in " + outputPath);	
		System.out.println("start time: " + start_time);
		System.out.println("end time: " + clock.instant()); 
		
	}

}

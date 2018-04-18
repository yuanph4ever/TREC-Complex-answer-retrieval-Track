
package edu.unh.cs980;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

import edu.unh.cs980.Classifier.ClassifierModel;
import edu.unh.cs980.Classifier.Classify;
import edu.unh.cs980.RetrievalModel.BM25;
import edu.unh.cs980.TrainClassifier.TrainSet;
import edu.unh.cs980.entitiesExpansion.QueryExpansionWithEntities;
import edu.unh.cs980.kmeans.QueryByCluster;
import weka.classifiers.Classifier;

public class Main {

	
	    
	

	public static void main(String[] args) throws Exception {

		

		System.setProperty("file.encoding", "UTF-8");

		String trainFileCorpus = args[0];
		String outputPath = args[1];
		String modelPath = args[2];

		

//		TrainSet ts = new TrainSet(trainFileCorpus, outputPath, 1000);
//		
//		String arffFile = outputPath+"/TrainingData.arff";
//		ClassifierModel cm = new ClassifierModel(arffFile, modelPath);
//		//cm.buildJ48Classifier(arffFile, modelPath);
//		//cm.buildNaiveBayesClassifier(arffFile, modelPath);
//		cm.buildRandomForestClassifier(arffFile, modelPath);
//		
		
		
		
		
		
		
		String para = "also because the 2:3:3:2 pulldown scheme was devised in order to make pulldown removal for editing in native 24p more efficient, the pulldown arrangement is not ideal for watching footage.  there can be exaggerated stutters in motion, because the frames which are split into three fields are not only onscreen for 50 longer than the other frames, they are back-to-back.  as such, 2:3:3:2 pulldown should be used only when a native 24p edit is planned, and not for final viewing.  this includes when shooting the footage initially, and also when printing back to tape from an nle.',";
		
		
		sampleClassify sc = new sampleClassify(para);
		

// 		Complete Run
		
//		String pagesFile = args[0];
//		String indexPath = args[1];
//		String outputPath = args[2];
//
////		BM25 bm25 = new BM25(pagesFile, indexPath, outputPath);
////		Map<String, String> pageHeading = bm25.getPageHeadingMap();
//		
//		Classify classifyPage = new Classify(outputPath, pagesFile, indexPath);
		
		
	}

}

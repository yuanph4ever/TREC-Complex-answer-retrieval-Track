package edu.unh.cs980.Classifier;


/*
 * Author - Nithin


 * This is a classifier which classifies wikipedia passages into Headings.
 */



import java.util.List;


import java.util.Map;
import java.util.Properties;



import weka.classifiers.trees.RandomForest;
import weka.core.converters.ConverterUtils.DataSource;

public class ClassifyPassageHeadings {
	
	private static final double CONFIDENCE_THRESHOLD = 0.2;//prediction confidence
	static String dirParagraphCorpus = "";
	static String resultsDir = "";
	
	public RandomForest trainclassifier() throws Exception
    {
		int numTopics = 40;
		int seed  = 1;
		int folds = 10;
		DataSource trainSource = new DataSource("");
		RandomForest classifier=new RandomForest();
		System.out.println("Training with "+classifier.getClass().getName());
		return null;
    	
    }

	
	
}
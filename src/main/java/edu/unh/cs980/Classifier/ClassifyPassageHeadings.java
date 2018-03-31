package edu.unh.cs980.Classifier;

/*
 * Author - Nithin


 * This is a classifier which classifies wikipedia passages into Headings.
 */

import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Resample;

public class ClassifyPassageHeadings {

	private static final double CONFIDENCE_THRESHOLD = 0.2;// prediction
															// confidence

	public ClassifyPassageHeadings(String arffFile, String modelPath) throws Exception {
		
		
		
//		J48Classifier j48 = new J48Classifier(arffFile, modelPath);
//		
		System.out.println("J48 model built");
		
		System.out.println("Naive Bayes classifier running");
		NaiveBayes nb = new NaiveBayes(arffFile, modelPath);
//		
//		RFClassifier rf = new RFClassifier();
//		rf.trainclassifier(arffFile, modelPath);

		String text = "rainfall, and the surface runoff which may result from rainfall, produces four main types of soil erosion: splash erosion, sheet erosion, rill erosion, and gully erosion. splash erosion is generally seen as the first and least severe stage in the soil erosion process, which is followed by sheet erosion, then rill erosion and finally gully erosion (the most severe of the four)";
		
	}

}
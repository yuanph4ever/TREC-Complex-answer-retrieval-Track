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

	public RandomForest trainclassifier(String arrfFile) throws Exception {

		int seed  = 1;
		int folds = 10;
		DataSource trainSource = new DataSource(arrfFile + ".arff");
		Instances trainingSet = trainSource.getDataSet();
		if (trainingSet.classIndex() == -1)
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

		// Resample for minority class
		Resample reSample = new Resample();
		reSample.setInputFormat(trainingSet);
		
		trainingSet=Filter.useFilter(trainingSet, reSample);
		
		Random rand = new Random(seed);
		trainingSet.randomize(rand);
		if (trainingSet.classAttribute().isNominal())
			trainingSet.stratify(folds);

		RandomForest classifier=new RandomForest();

		System.out.println("Training with "+classifier.getClass().getName());
		System.out.println(trainingSet.numInstances());
		
		Evaluation eval = new Evaluation(trainingSet);
		
		eval.crossValidateModel(classifier, trainingSet, 10, new Random(1), new Object[] { });
		
		System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===\n", false));
		System.out.println(eval.toClassDetailsString()+"\n"+eval.toMatrixString()+"\n");
		classifier.buildClassifier(trainingSet);
		return classifier;

	}

}
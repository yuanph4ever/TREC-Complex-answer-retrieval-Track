package edu.unh.cs980.Classifier;

import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.unsupervised.instance.Resample;

public class RFClassifier {

	public RandomForest trainclassifier(String arrfFile, String modelPath) throws Exception {

		System.out.println("Training RF classifier with the trainset");
		int seed = 1;
		int folds = 10;
		DataSource trainSource = new DataSource(arrfFile);
		Instances trainingSet = trainSource.getDataSet();
		if (trainingSet.classIndex() == -1)
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);

		// Resample for minority class
		Resample reSample = new Resample();
		reSample.setInputFormat(trainingSet);

		trainingSet = Filter.useFilter(trainingSet, reSample);

//		StringToWordVector filter = new StringToWordVector();
//		filter.setInputFormat(trainingSet);
//		filter.setIDFTransform(true);
//		filter.setUseStoplist(true);
//		System.out.println("Filter applied - StringtoWord");
//		LovinsStemmer stemmer = new LovinsStemmer();
//		filter.setStemmer(stemmer);
//		filter.setLowerCaseTokens(true);
//		System.out.println("Stemmer done");
//
//		// StringToNominal stnfilter = new StringToNominal();
//		// stnfilter.setInputFormat(trainingSet);
//
//		trainingSet = Filter.useFilter(trainingSet, filter);

		Random rand = new Random(seed);
		trainingSet.randomize(rand);
		if (trainingSet.classAttribute().isNominal())
			trainingSet.stratify(folds);
		System.out.println("Applying  filter");

		RandomForest classifier = new RandomForest();

		System.out.println("Training with " + classifier.getClass().getName());
		System.out.println(trainingSet.numInstances());

		Evaluation eval = new Evaluation(trainingSet);

		eval.crossValidateModel(classifier, trainingSet, 10, new Random(1), new Object[] {});

		System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===\n", false));
		System.out.println(eval.toClassDetailsString() + "\n" + eval.toMatrixString() + "\n");
		classifier.buildClassifier(trainingSet);

		weka.core.SerializationHelper.write(modelPath + "/trainedModel/RF_Page.model", classifier);

		return classifier;

	}

}

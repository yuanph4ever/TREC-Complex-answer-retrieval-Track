package edu.unh.cs980.Classifier;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class NaiveBayes {
	
	public NaiveBayes(String arffFile, String modelPath) throws Exception
	{
		DataSource trainSource = new DataSource("/Users/Nithin/Desktop/Runfile/trainPageHeading.arff");
		Instances trainingSet = trainSource.getDataSet();
		if (trainingSet.classIndex() == -1)
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
		weka.classifiers.bayes.NaiveBayes nb = new weka.classifiers.bayes.NaiveBayes();
		StringToWordVector filter = new StringToWordVector();
		  filter.setInputFormat(trainingSet);
		  filter.setIDFTransform(true);
		  filter.setUseStoplist(true);
		  System.out.println("Filter applied - StringtoWord");
		  LovinsStemmer stemmer = new LovinsStemmer();
		  filter.setStemmer(stemmer);
		  filter.setLowerCaseTokens(true);
		  System.out.println("Stemmer done");
		  //Create the FilteredClassifier object
		  FilteredClassifier fc = new FilteredClassifier();
		  //specify filter
		  fc.setFilter(filter);
		  //specify base classifier
		//specify base classifier
		  fc.setClassifier(nb);
		  fc.buildClassifier(trainingSet);
		
		
//		nb.buildClassifier(trainingSet);
		
		System.out.println(nb.getClass().toString());
		
		weka.core.SerializationHelper.write(modelPath + "/trainedModel/RF_Page.model", fc);
		
//		System.out.println("model saved in " + );
		
	}

}

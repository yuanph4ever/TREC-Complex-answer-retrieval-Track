package edu.unh.cs980.Classifier;

import weka.core.Instance;
//import required classes
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.stemmers.LovinsStemmer;

import java.util.Arrays;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class J48Classifier{

  public static void main(String args[]) throws Exception{
  //load dataset
  DataSource source = new DataSource("/Users/Nithin/Desktop/Runfile/pageAr.arff");
  Instances dataset = source.getDataSet();
  System.out.println("loaded dataSet");
  //set class index to the last attribute
  dataset.setClassIndex(dataset.numAttributes()-1);

  System.out.println("build Started");
  //the base classifier
  J48 tree = new J48();

  //the filter
  StringToWordVector filter = new StringToWordVector();
  filter.setInputFormat(dataset);
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
  fc.setClassifier(tree);
  //Build the meta-classifier
  fc.buildClassifier(dataset);

  System.out.println(tree.graph());
  System.out.println(tree);
  

  double pred = tree.classifyInstance("tree");
  System.out.println("====== RESULT ====== \tCLASSIFIED AS:\t" + );
 }
}
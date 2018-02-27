package edu.unh.cs980.classifier;


/*
 * Author - Nithin


 * This is a classifier which classifies wikipedia passages into Headings.
 */



import java.util.List;

import java.util.Map;
import java.util.Properties;


import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
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
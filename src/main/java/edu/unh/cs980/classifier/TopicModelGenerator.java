package edu.unh.cs980.classifier;

import cc.mallet.util.*;
import cc.mallet.types.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;

public class TopicModelGenerator {

	public static void main(String[] args) throws Exception {

		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add(new TokenSequenceRemoveStopwords(new File("inputFiles/stoplists/en.txt"), "UTF-8", false, false,
				false));
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		int numTopics = 40;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 0.1, 0.01);

		model.addInstances(instances);

		model.setNumIterations(2000);
		model.estimate();

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = instances.getDataAlphabet();


		Formatter out = new Formatter(new StringBuilder(), Locale.US);


		// Estimate the topic distribution of the first instance,
		// given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		
		

	}
}

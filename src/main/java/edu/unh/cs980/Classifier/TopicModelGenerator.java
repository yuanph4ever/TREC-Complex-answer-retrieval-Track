package edu.unh.cs980.Classifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.InstanceList;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class TopicModelGenerator implements Serializable {

	private Instances trainingData;
	private FastVector classValues;
	private FastVector attributes;

	public TopicModelGenerator(Map<String, List<String>> passageHeadings, String outputPath) throws IOException {
		this(passageHeadings.size());
		for (Entry<String, List<String>> entry : passageHeadings.entrySet()) {
			addHeading(entry.getKey());
		}
		setupAfterHeadingAdded();

		for (Entry<String, List<String>> entry : passageHeadings.entrySet()) {
			for (String e : entry.getValue()) {
				addParagrah(e, entry.getKey());
			}
		}

		createDatasetFile(outputPath);
	}

	public TopicModelGenerator(int classSize) {

		// Create vector of attributes.
		this.attributes = new FastVector(2);
		// Add attribute for holding texts.
		this.attributes.addElement(new Attribute("text", (FastVector) null));
		// Add class attribute.
		this.classValues = new FastVector(classSize);

	}

	public void addHeading(String heading) {
		heading = heading.toLowerCase();
		// if required, double the capacity.
		int capacity = classValues.capacity();
		if (classValues.size() > (capacity - 5)) {
			classValues.setCapacity(capacity * 2);
		}
		classValues.addElement(heading);
	}

	public void addParagrah(String paragraph, String classValue) throws IllegalStateException {

		paragraph = paragraph.toLowerCase();
		classValue = classValue.toLowerCase();
		// Make message into instance.
		Instance instance = makeInstance(paragraph, trainingData);
		// Set class value for instance.
		instance.setClassValue(classValue);
		// Add instance to training data.
		trainingData.add(instance);

	}

	private Instance makeInstance(String paragraph, Instances data) {
		// Create instance of length two.

//		// Begin by importing documents from text to feature sequences
//		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
//
//		// Pipes: lowercase, tokenize, remove stopwords, map to features
//		pipeList.add(new CharSequenceLowercase());
//		pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
//		pipeList.add(new TokenSequenceRemoveStopwords(new File("inputFiles/stoplists/en.txt"), "UTF-8", false, false,
//				false));
//		pipeList.add(new TokenSequence2FeatureSequence());

		
		Instance instance = new Instance(2);
		
		
		// Set value for message attribute
		Attribute messageAtt = data.attribute("text");
		instance.setValue(messageAtt, messageAtt.addStringValue(paragraph));

		// Give instance access to attribute information from the dataset.
		instance.setDataset(data);
		return instance;
	}

	public void setupAfterHeadingAdded() {
		attributes.addElement(new Attribute("@@class@@.", classValues));
		// Create dataset with initial capacity of 100, and set index of class.
		trainingData = new Instances("Sample", attributes, 100);
		trainingData.setClassIndex(trainingData.numAttributes() - 1);
	}

	public void createDatasetFile(String path) throws IOException {
		File f = new File(path + ".arff");
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(trainingData.toString());
		bw.close();
		System.out.println("check for arff file");
	}
}

package edu.unh.cs980.Classifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class TestSetArff implements Serializable {

	private Instances testData;
	private FastVector classValues;
	private FastVector attributes;

	public TestSetArff(Map<String, List<String>> passageHeadings, String outputPath) throws IOException {
		this(passageHeadings.size());
		for (Entry<String, List<String>> entry : passageHeadings.entrySet()) {
			addHeading(entry.getKey());
		}
		setupAfterHeadingAdded();

		for (Entry<String, List<String>> entry : passageHeadings.entrySet()) {
			for (String e : entry.getValue()) {
				// removeStopWords(e);
				addParagrah(e, entry.getKey());
			}
		}

		createDatasetFile(outputPath);
	}

	public TestSetArff(int classSize) {

		// Create vector of attributes.
		this.attributes = new FastVector(2);
		// Add attribute for holding texts.
		this.attributes.addElement(new Attribute("text", (FastVector) null));
		// Add class attribute.
		this.classValues = new FastVector(classSize);

	}

	private void addHeading(String heading) {
		heading = heading.toLowerCase();
		// if required, double the capacity.
		int capacity = classValues.capacity();
		if (classValues.size() > (capacity - 5)) {
			classValues.setCapacity(capacity * 2);
		}
		classValues.addElement(heading);

	}

	private void setupAfterHeadingAdded() {
		// TODO Auto-generated method stub

	}

	private void addParagrah(String paragraph, String classValue) {
		paragraph = paragraph.toLowerCase();
		classValue = classValue.toLowerCase();
		// Make message into instance.
		Instance instance = makeInstance(paragraph, testData);
		// Set class value for instance.
		instance.setClassValue(classValue);
		// Add instance to training data.
		testData.add(instance);

	}

	private Instance makeInstance(String paragraph, Instances data) {
		// Create instance of length two.
		Instance instance = new Instance(2);

		// Set value for message attribute
		Attribute messageAtt = data.attribute("text");

		instance.setValue(messageAtt, messageAtt.addStringValue(paragraph));

		// Give instance access to attribute information from the dataset.
		instance.setDataset(data);
		return instance;
	}

	private void createDatasetFile(String outputPath) throws IOException {
		File f = new File(outputPath + ".arff");
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(testData.toString());
		bw.close();
		System.out.println("check for arff file");

	}

}

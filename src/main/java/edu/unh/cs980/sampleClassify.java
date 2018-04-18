package edu.unh.cs980;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class sampleClassify {

	public sampleClassify(String para) throws Exception {
		
		Classifier clsRF = (Classifier) weka.core.SerializationHelper.read("/Users/Nithin/Desktop/Prototype3/Model/10000/RF_Page.model");
		DataSource source = new DataSource("/Users/Nithin/Desktop/Prototype3/trainset/TrainingData.arff");
		Instances trainingData = source.getDataSet();
		trainingData.setClassIndex(trainingData.numAttributes() - 1);

		Instances testset = trainingData.stringFreeStructure();
		Instance insta = makeInstance(para, testset);

		double predicted = clsRF.classifyInstance(insta);
		System.out.println(trainingData.classAttribute().value((int) predicted));
	}

	private Instance makeInstance(String text, Instances data) {
		// Create instance of length two.
		Instance instance = new Instance(2);
		// Set value for message attribute
		Attribute messageAtt = data.attribute("text");
		instance.setValue(messageAtt, messageAtt.addStringValue(text));
		// Give instance access to attribute information from the dataset.
		instance.setDataset(data);
		return instance;
	}
}

package edu.unh.cs980.Classifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.Data.PageSkeleton;
import edu.unh.cs.treccar_v2.Data.Section;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs980.RetrievalModel.BM25.MyQueryBuilder;
import edu.unh.cs980.nTools.TextClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesMultinomialUpdateable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Classify {

	public Classify(Map<String, String> paraHeading, String outputPath, String pagesFile, String indexPath) throws Exception {

		String model_J48 = "/Users/Nithin/Desktop/Model/J48_Page.model";
		String model_RF = "/Users/Nithin/Desktop/Model/RF_Page.model";
		String model_NB = " /Users/Nithin/Desktop/Model/NB_Page.model";

		Classifier clsJ48 = (Classifier) weka.core.SerializationHelper.read(model_J48);


		CreateTestSet cts = new CreateTestSet(paraHeading, outputPath);

		DataSource testSource = new DataSource(outputPath + "classification.arff");
		Instances testingSet = testSource.getDataSet();

		testingSet.setClassIndex(testingSet.numAttributes() - 1);

		classiyPageSearch(outputPath, indexPath, pagesFile, clsJ48, testingSet, "J48");
		
		System.out.println("classification results written to the file " +  outputPath + "/" +"J48" + "runfile_pagePr2");
		
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

	private void classiyPageSearch(String outputPath, String indexPath, String pagesFile, Classifier classifier, Instances data, String classifierName) throws Exception {
		
		data.setClassIndex(data.numAttributes() - 1);
		
		outputPath = outputPath + "/" +classifierName;
		File runfile = new File(outputPath + "runfile_pagePr2");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);

		// paragraphs-run-sections
		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");
		searcher.setSimilarity(new BM25Similarity());
		final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());
		final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));

		System.out.println("starting searching for pages ...");

		int count = 0;
		int instance = 0;
		List<String> uniquePara;
		for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
			
			uniquePara = new ArrayList<String>();
			final String queryId = page.getPageId();

			String queryStr = buildSectionQueryStr(page, Collections.<Data.Section>emptyList());

			TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 50);
			ScoreDoc[] scoreDoc = tops.scoreDocs;

			for (int i = 0; i < scoreDoc.length; i++) {
				ScoreDoc score = scoreDoc[i];
				final Document doc = searcher.doc(score.doc); // to access
																// stored
																// content
				// print score and internal docid
				final String paragraphid = doc.getField("paragraphid").stringValue();
				final String paragraph = doc.getField("text").stringValue();
				final float searchScore = score.score;
				final int searchRank = i + 1;

				double res = classifier.classifyInstance(data.instance(instance));
				

				System.out.println(".");
				
				if(!(uniquePara.contains(data.classAttribute().value((int) res))))
				{
				writer.write(queryId + " Q0 " +data.classAttribute().value((int) res) + " " + searchRank + " " + searchScore + " Lucene-BM25\n");
				}
				uniquePara.add(data.classAttribute().value((int) res));
				count++;
				instance++;
			}

		}

		writer.flush();// why flush?
		writer.close();

		System.out.println("Write " + count + " results\nQuery Done!");

	}

	private void classifySectionSearch(String outputPath, String indexPath, String pagesFile, Classifier classifier, Instances data, String classifierName) throws Exception {
		
		data.setClassIndex(data.numAttributes() - 1);
		
		outputPath = outputPath + "/" +classifierName;
		File runfile = new File(outputPath + "/runfile_sectionPr2");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);

		List<String> uniquePara;
		// paragraphs-run-sections
		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");
		searcher.setSimilarity(new BM25Similarity());
		final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());
		final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));

		System.out.println("starting searching for sections ...");

		int count = 0;
		int instance = 0;
		for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
			for (List<Data.Section> sectionPath : page.flatSectionPaths()) {
				uniquePara = new ArrayList<String>();
				final String queryId = Data.sectionPathId(page.getPageId(), sectionPath);
				String queryStr = buildSectionQueryStr(page, sectionPath);
				TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 5);
				ScoreDoc[] scoreDoc = tops.scoreDocs;

				for (int i = 0; i < scoreDoc.length; i++) {
					ScoreDoc score = scoreDoc[i];
					final Document doc = searcher.doc(score.doc); // to access
																	// stored
																	// content
					// print score and internal docid
					final String paragraphid = doc.getField("paragraphid").stringValue();
					final String paragraph = doc.getField("text").stringValue();
					final float searchScore = score.score;
					final int searchRank = i + 1;

					double res = classifier.classifyInstance(data.instance(instance));
					

					System.out.println(".");
					
					if(!(uniquePara.contains(data.classAttribute().value((int) res))))
					{
					writer.write(queryId + " Q0 " +data.classAttribute().value((int) res) + " " + searchRank + " " + searchScore + " Lucene-BM25\n");
					}
					uniquePara.add(data.classAttribute().value((int) res));
					count++;
					instance++;

				}

			}
		}

		writer.flush();
		writer.close();

		System.out.println("Write " + count + " results\nQuery Done!");
		stripDuplicatesFromFile(runfile.toString());

	}

	private void classifySectionSearchForLowestHeading(String outputPath, String indexPath, String pagesFile, Classifier classifier, Instances data, String classifierName)
			throws IOException {
		
		data.setClassIndex(data.numAttributes() - 1);
		
		File runfile = new File(outputPath + "/runfile_section_lowestheading");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);

		// paragraphs-run-sections
		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene");
		searcher.setSimilarity(new BM25Similarity());
		final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());
		final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));

		System.out.println("starting searching for sections ...");

		int count = 0;

		for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
			for (List<Data.Section> sectionPath : page.flatSectionPaths()) {

				final String queryId = Data.sectionPathId(page.getPageId(), sectionPath);
				String queryStr = buildSectionQueryStr(sectionPath); // get the
																		// lowest
																		// heading
				TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 5);
				ScoreDoc[] scoreDoc = tops.scoreDocs;
				for (int i = 0; i < scoreDoc.length; i++) {
					ScoreDoc score = scoreDoc[i];
					final Document doc = searcher.doc(score.doc); // to access
																	// stored
																	// content
					// print score and internal docid
					final String paragraphid = doc.getField("paragraphid").stringValue();
					final String paragraph = doc.getField("text").stringValue();
					final float searchScore = score.score;
					final int searchRank = i + 1;

					System.out.println(".");
					writer.write(
							queryId + " Q0 " + paragraphid + " " + searchRank + " " + searchScore + " Lucene-BM25\n");
					count++;

				}

			}
		}

		writer.flush();
		writer.close();

		System.out.println("Write " + count + " results\nQuery Done!");
		stripDuplicatesFromFile(runfile.toString());

	}

	// Remove Duplicates from the runfile for sections
	public static void stripDuplicatesFromFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		Set<String> lines = new HashSet<String>(); // maybe should be bigger
		String line;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		reader.close();
		System.out.println("Removing Duplicates");
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		for (String unique : lines) {
			writer.write(unique);
			writer.newLine();
		}
		writer.close();
	}

	// Author: Laura dietz
	static class MyQueryBuilder {

		private final StandardAnalyzer analyzer;
		private List<String> tokens;

		public MyQueryBuilder(StandardAnalyzer standardAnalyzer) {
			analyzer = standardAnalyzer;
			tokens = new ArrayList<>(128);
		}

		public BooleanQuery toQuery(String queryStr) throws IOException {

			TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(queryStr));
			tokenStream.reset();
			tokens.clear();
			while (tokenStream.incrementToken()) {
				final String token = tokenStream.getAttribute(CharTermAttribute.class).toString();
				tokens.add(token);
			}
			tokenStream.end();
			tokenStream.close();
			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
			for (String token : tokens) {
				booleanQuery.add(new TermQuery(new Term("text", token)), BooleanClause.Occur.SHOULD);
			}
			return booleanQuery.build();
		}
	}

	// Author: Laura dietz
	private static IndexSearcher setupIndexSearcher(String indexPath, String typeIndex) throws IOException {
		Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
		Directory indexDir = FSDirectory.open(path);
		IndexReader reader = DirectoryReader.open(indexDir);
		return new IndexSearcher(reader);
	}

	// Author: Laura dietz
	private static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath) {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append(page.getPageName());
		for (Data.Section section : sectionPath) {
			queryStr.append(" ").append(section.getHeading());
		}

		// System.out.println("queryStr = " + queryStr);
		return queryStr.toString();
	}

	// Author: Laura dietz, modified by Nithin for lowest heading in each
	// section
	private static String buildSectionQueryStr(List<Data.Section> sectionPath) {
		String queryStr = " ";
		List<PageSkeleton> child;

		for (Data.Section section : sectionPath) {

			child = section.getChildren();
			if (!(child.isEmpty())) {
				Section s = (Section) child.get(child.size() - 1);
				queryStr = s.getHeading();

			} else {
				queryStr = section.getHeading();
			}

		}
		return queryStr;
	}

}

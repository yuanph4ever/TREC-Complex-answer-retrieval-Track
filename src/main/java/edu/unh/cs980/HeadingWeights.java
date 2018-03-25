package edu.unh.cs980;

import edu.unh.cs.treccar_v2.Data;

import edu.unh.cs.treccar_v2.Data.PageSkeleton;
import edu.unh.cs.treccar_v2.Data.Section;
import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs980.Classifier.ClassifyPassageHeadings;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;
import org.netlib.util.doubleW;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.PasswordAuthentication;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.FileWriter;

public class HeadingWeights {

	static List<String> paragraphs;
	static Map<String, List<String>> passageHeadings = new HashMap<String, List<String>>();

	public static void main(String[] args) throws Exception {

		System.setProperty("file.encoding", "UTF-8");

		String pagesFile = args[0];
		String indexPath = args[1];
		String outputPath = args[2];
		computeHeadingWeights(outputPath, indexPath, pagesFile);
		// System.out.println(" Results written to the runFile");
		// ClassifyPassageHeadings cph = new ClassifyPassageHeadings();
		// cph.trainclassifier(passageHeadings);

		createArffDataset(passageHeadings, outputPath + "/pageAr");
		//sampleArff(outputPath);

	}

	private static void sampleArff(String path) throws ParseException, IOException {
		// TODO Auto-generated method stub
		FastVector atts;
		FastVector attsRel;
		FastVector attVals;
		FastVector attValsRel;
		Instances data;
		Instances dataRel;
		double[] vals;
		double[] valsRel;
		int i;

		// 1. set up attributes
		atts = new FastVector();
		// - numeric
		atts.addElement(new Attribute("att1"));
		// - nominal
		attVals = new FastVector();
		for (i = 0; i < 5; i++)
			attVals.addElement("val" + (i + 1));
		atts.addElement(new Attribute("att2", attVals));
		// - string
		atts.addElement(new Attribute("att3", (FastVector) null));
		// - date
		atts.addElement(new Attribute("att4", "yyyy-MM-dd"));
		// - relational
		attsRel = new FastVector();
		// -- numeric
		attsRel.addElement(new Attribute("att5.1"));
		// -- nominal
		attValsRel = new FastVector();
		for (i = 0; i < 5; i++)
			attValsRel.addElement("val5." + (i + 1));
		attsRel.addElement(new Attribute("att5.2", attValsRel));
		dataRel = new Instances("att5", attsRel, 0);
		atts.addElement(new Attribute("att5", dataRel, 0));

		// 2. create Instances object
		data = new Instances("MyRelation", atts, 0);

		// 3. fill with data
		// first instance
		vals = new double[data.numAttributes()];
		// - numeric
		vals[0] = Math.PI;
		// - nominal
		vals[1] = attVals.indexOf("val3");
		// - string
		vals[2] = data.attribute(2).addStringValue("This is a string!");
		// - date
		vals[3] = data.attribute(3).parseDate("2001-11-09");
		// - relational
		dataRel = new Instances(data.attribute(4).relation(), 0);
		// -- first instance
		valsRel = new double[2];
		valsRel[0] = Math.PI + 1;
		valsRel[1] = attValsRel.indexOf("val5.3");
		dataRel.add(new Instance(1.0, valsRel));
		// -- second instance
		valsRel = new double[2];
		valsRel[0] = Math.PI + 2;
		valsRel[1] = attValsRel.indexOf("val5.2");
		dataRel.add(new Instance(1.0, valsRel));
		vals[4] = data.attribute(4).addRelation(dataRel);
		// add
		data.add(new Instance(1.0, vals));

		// second instance
		vals = new double[data.numAttributes()]; // important: needs NEW array!
		// - numeric
		vals[0] = Math.E;
		// - nominal
		vals[1] = attVals.indexOf("val1");
		// - string
		vals[2] = data.attribute(2).addStringValue("And another one!");
		// - date
		vals[3] = data.attribute(3).parseDate("2000-12-01");
		// - relational
		dataRel = new Instances(data.attribute(4).relation(), 0);
		// -- first instance
		valsRel = new double[2];
		valsRel[0] = Math.E + 1;
		valsRel[1] = attValsRel.indexOf("val5.4");
		dataRel.add(new Instance(1.0, valsRel));
		// -- second instance
		valsRel = new double[2];
		valsRel[0] = Math.E + 2;
		valsRel[1] = attValsRel.indexOf("val5.1");
		dataRel.add(new Instance(1.0, valsRel));
		vals[4] = data.attribute(4).addRelation(dataRel);
		// add
		data.add(new Instance(1.0, vals));

		// 4. output data
		System.out.println(data);
		
		File f = new File(path+"/Sample" + ".arff");
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data.toString());
		bw.close();
		System.out.println("check for arff file");

	}

	private static void createArffDataset(Map<String, List<String>> passageHeadings2, String path) throws IOException {
		// TODO Auto-generated method stub
		FastVector attr = getAttributes(passageHeadings2);
		double[] vals;
		Instances data = new Instances("trainingFiles", attr, 0);
		vals = new double[data.numAttributes()];
		int index = 0;
		for(Entry<String, List<String>> entry : passageHeadings2.entrySet())
		{
			vals[index] = data.attribute(index).addStringValue(entry.getValue().toString());
			index++;
			data.add(new Instance(1.0, vals));
		}

		

		File f = new File(path + ".arff");
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data.toString());
		bw.close();
		System.out.println("check for arff file");
	}

	private static FastVector getAttributes(Map<String, List<String>> passageHeadings2) {

		FastVector attr;
		FastVector classValues;
		attr = new FastVector(passageHeadings2.size());
		StringBuffer s = new StringBuffer();
		List<String> classValueList = new ArrayList<String>();
		ArrayList<List<String>> listOfList = new ArrayList<List<String>>();
		for (Entry<String, List<String>> entry : passageHeadings2.entrySet()) {
			attr.addElement(new Attribute(entry.getKey().toString(), (FastVector) null));
			listOfList.add(entry.getValue());
		}

		classValues = new FastVector(listOfList.size());
		for (List<String> l : listOfList) {
			for (String e : l) {
				if (classValues.contains(e)) {
					continue;
				} else {
					classValues.addElement(e);
				}
			}

		}
		attr.addElement(new Attribute("Class", classValues));

		return attr;
	}

	private static void computeHeadingWeights(String outputPath, String indexPath, String pagesFile)
			throws IOException {
		// TODO Auto-generated method stub
		PageSearch(outputPath, indexPath, pagesFile);
		// SectionSearch(outputPath, indexPath, pagesFile);
		// SectionSearchForLowestHeading(outputPath, indexPath, pagesFile);

	}

	// Author : Nithin
	private static void PageSearch(String outputPath, String indexPath, String pagesFile) throws IOException {
		File runfile = new File(outputPath + "/runfile_page");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);

		// paragraphs-run-sections
		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene");
		searcher.setSimilarity(new BM25Similarity());
		final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());
		final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));

		System.out.println("starting searching for pages ...");

		int count = 0;

		for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
			final String queryId = page.getPageId();

			String queryStr = buildSectionQueryStr(page, Collections.<Data.Section>emptyList());

			TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 1);
			ScoreDoc[] scoreDoc = tops.scoreDocs;
			paragraphs = new ArrayList<String>();
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

				paragraphs.add(paragraph);

				System.out.println(".");
				// writer.write(queryStr + " - " + paragraph + "\n");
				writer.write(queryId + " Q0 " + paragraphid + " " + searchRank + " " + searchScore + " Lucene-BM25\n");
				count++;
			}
			passageHeadings.put(queryStr, paragraphs);

		}

		writer.flush();// why flush?
		writer.close();

		System.out.println("Write " + count + " results\nQuery Done!");
	}

	// Author : Nithin
	private static void SectionSearch(String outputPath, String indexPath, String pagesFile) throws IOException {
		File runfile = new File(outputPath + "/runfile_section");
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
				String queryStr = buildSectionQueryStr(page, sectionPath);
				TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
				ScoreDoc[] scoreDoc = tops.scoreDocs;
				for (int i = 0; i < scoreDoc.length; i++) {
					ScoreDoc score = scoreDoc[i];
					final Document doc = searcher.doc(score.doc); // to access
																	// stored
																	// content
					// print score and internal docid
					final String paragraphid = doc.getField("paragraphid").stringValue();
					final String paragraph = doc.getField("content").stringValue();
					final float searchScore = score.score;
					final int searchRank = i + 1;
					// System.out.println(page.getPageName() + " " + queryStr);
					// System.out.println(queryId+" Q0 "+paragraphid+"
					// "+searchRank + " "+searchScore+" Lucene-BM25");
					// passageHeadings.put(queryStr, paragraph);
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

	// Author : Nithin
	private static void SectionSearchForLowestHeading(String outputPath, String indexPath, String pagesFile)
			throws IOException {
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
				TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
				ScoreDoc[] scoreDoc = tops.scoreDocs;
				for (int i = 0; i < scoreDoc.length; i++) {
					ScoreDoc score = scoreDoc[i];
					final Document doc = searcher.doc(score.doc); // to access
																	// stored
																	// content
					// print score and internal docid
					final String paragraphid = doc.getField("paragraphid").stringValue();
					final float searchScore = score.score;
					final int searchRank = i + 1;
					// System.out.println(page.getPageName() + " " + queryStr );
					// System.out.println(queryId+" Q0 "+paragraphid+"
					// "+searchRank + " "+searchScore+" Lucene-BM25");
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

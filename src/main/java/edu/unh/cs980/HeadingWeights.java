package edu.unh.cs980;

import edu.unh.cs.treccar_v2.Data;

import edu.unh.cs.treccar_v2.Data.PageSkeleton;
import edu.unh.cs.treccar_v2.Data.Section;
import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.FileWriter;

public class HeadingWeights {

//	public static void main(String[] args) throws IOException {
//
//		System.setProperty("file.encoding", "UTF-8");
//
//		String pagesFile = args[0];
//		String indexPath = args[1];
//		String outputPath = args[2];
//		System.out.println(" Results written to the runFile");
//
//	}
	
	public HeadingWeights(String pagesFile, String indexPath, String outputPath) throws IOException
	{
		computeHeadingWeights(outputPath, indexPath, pagesFile);
	}

	private static void computeHeadingWeights(String outputPath, String indexPath, String pagesFile) throws IOException {
		// TODO Auto-generated method stub
		PageSearch(outputPath, indexPath, pagesFile);
		SectionSearch(outputPath, indexPath, pagesFile);
		SectionSearchForLowestHeading(outputPath, indexPath, pagesFile);

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

				System.out.println(".");
				writer.write(queryId + " Q0 " + paragraphid + " " + searchRank + " " + searchScore + " Lucene-BM25\n");
				count++;
			}

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
					final float searchScore = score.score;
					final int searchRank = i + 1;
					System.out.println(page.getPageName() + " " + queryStr);
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
				String queryStr = buildSectionQueryStr(sectionPath); // get the lowest heading
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

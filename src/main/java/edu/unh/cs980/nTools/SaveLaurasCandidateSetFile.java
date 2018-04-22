package edu.unh.cs980.nTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
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
import edu.unh.cs980.Classifier.ReadRunFileAndClassify.MyQueryBuilder;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class SaveLaurasCandidateSetFile {

	public static void main(String[] args) throws IOException {
		String indexPath = "/Users/Nithin/Desktop/ParagraphIndex";
		String outputPath = "/Users/Nithin/Desktop/LAURA_CANDIDATE_SET";

		String paraPageTrain = "/Users/Nithin/Desktop/Lauras Runs/lucene1--paragraph-page--title-ql-none--Text-std-k1000-benchmarkY1train.v201.cbor.outlines.run";
		String paraSectionTrain = "/Users/Nithin/Desktop/Lauras Runs/lucene1--paragraph-section--sectionPath-ql-none--Text-std-k1000-benchmarkY1train.v201.cbor.outlines.run";

		saveTop100(paraPageTrain, indexPath, outputPath, "para");
		saveTop100(paraSectionTrain, indexPath, outputPath, "section");

		System.out.println("New Candiate Set written to file ");

	}

	public static void saveTop100(String runFile, String indexPath, String outputPath, String type) throws IOException {

		System.out.println("Making " + type + " Candidate Set");
		// time being give path for traindata

		if (type == "para")
			outputPath = outputPath + "/" + "CandidateSet" + "_Para_";

		if (type == "section")
			outputPath = outputPath + "/" + "CandidateSet_Section_";

		// outputPath = outputPath + "/" + classifierName;
		File runfile = new File(outputPath + "Set_Pr3");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);

		File file = new File(runFile);

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		int i = 1;
		while (((st = br.readLine()) != null)) {
			String[] tokens = st.split(" ");

//			String paragraph = getParagraphForId(indexPath, tokens[2]);
			System.out.print(".");
			writer.write(
					tokens[0] + " Q0 " + tokens[2] + " " + tokens[3] + " " + tokens[4] + " Team5_Laura_Combined\n");

			if ((i == 100) ) {
				System.out.println(i);
				int counter = 0;
				while (counter < 900) {
					br.readLine();
					counter++;
					i++;
				}
			}
			
			if(i > 1000)
			{
				String hundred = "100";
				String lineNumber = new Integer(i).toString();
				if (((hundred.equals(lineNumber.substring(lineNumber.length() - 3))))) {
					System.out.println("Reached next ID");
					System.out.print(i);
					int counter = 0;
					while (counter < 900) {
						br.readLine();
						counter++;
						i++;
					}
				}
			}

			i++;

		}
		writer.flush();
		writer.close();

	}
	
	// public static void main(String[] args) throws Exception {
		//
		//
		//
		// System.setProperty("file.encoding", "UTF-8");
		//
		//
		//
		// String pagesFile = args[0];
		// String indexPath = args[1];
		// String outputPath = args[2];
		//
		// // Nithin - methods
		// /**********************************************************************************************************/
		//
		// System.out.println("======================= BaseLine Candidate
		// set=====================================");
		// BM25 bm25 = new BM25(pagesFile, indexPath, outputPath);
		//
		// /**********************************************************************************************************/
		//
		// System.out.println("======================= Classifying BM25 Set
		// =====================================");
		// Classify classifyPage = new Classify(outputPath, pagesFile, indexPath);
		//
		// /**********************************************************************************************************/
		//
		// // Train
		// String paraPageTrain =
		// "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-page--title-ql-none--Text-std-k1000-benchmarkY1train.v201.cbor.outlines.run";
		// String paraSectionTrain =
		// "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-section--sectionPath-ql-none--Text-std-k1000-benchmarkY1train.v201.cbor.outlines.run";
		// // Test
		// String paraPageTest =
		// "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-page--title-ql-none--Text-std-k1000-benchmarkY1test.v201.cbor.outlines.run";
		// String paraSectionTest =
		// "/home/dietz/candidate-runs-all/benchmarkY1train-lucene-runs/lucene1--paragraph-section--sectionPath-ql-none--Text-std-k1000-benchmarkY1test.v201.cbor.outlines.run";
		//
		// System.out.println("======================= Classifying Laura Candidate
		// Set ===========================");
		// ReadRunFileAndClassify rrfc = new ReadRunFileAndClassify(paraPageTrain,
		// indexPath, outputPath, "para");
		// ReadRunFileAndClassify rrfcSec = new
		// ReadRunFileAndClassify(paraSectionTrain, indexPath, outputPath,
		// "section");
		// System.out.println(" =================All works done
		// ==================================================");
		//
		// /**********************************************************************************************************/

		// String trainFileCorpus = args[0];
		// String outputPath = args[1];
		// String modelPath = args[2];
		//
		// TrainSet ts = new TrainSet(trainFileCorpus, outputPath, 5000);
		//
		// String arffFile = outputPath+"/TrainingData5000.arff";
		// ClassifierModel cm = new ClassifierModel(arffFile, modelPath);
		// cm.buildRandomForestClassifier(arffFile, modelPath);
		// cm.buildNaiveBayesClassifier(arffFile, modelPath);
		// cm.buildJ48Classifier(arffFile, modelPath);

		// String para = "also because the 2:3:3:2 pulldown scheme was devised in
		// order to make pulldown removal for editing in native 24p more efficient,
		// the pulldown arrangement is not ideal for watching footage. there can be
		// exaggerated stutters in motion, because the frames which are split into
		// three fields are not only onscreen for 50 longer than the other frames,
		// they are back-to-back. as such, 2:3:3:2 pulldown should be used only when
		// a native 24p edit is planned, and not for final viewing. this includes
		// when shooting the footage initially, and also when printing back to tape
		// from an nle.',";
		//
		//
		// sampleClassify sc = new sampleClassify(para);

		// Complete Run

		// String File = "/Users/Nithin/Desktop/Prototype3/runfile/runfile_page";
		// ReadRunFileAndClassify rrfc = new ReadRunFileAndClassify(File);

		// Map<String, String> pageHeading = bm25.getPageHeadingMap();
		//

	private static String getParagraphForId(String indexPath, String paraId) throws IOException {
		// TODO Auto-generated method stub
		String paragraph = null;
		Analyzer analyzer = new StandardAnalyzer();
		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");
		searcher.setSimilarity(new BM25Similarity());
		final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());

		QueryParser qp = new QueryParser("paraid", analyzer);
		TopDocs tops = searcher.search(queryBuilder.toQuery(paraId), 1);
		ScoreDoc[] scoreDoc = tops.scoreDocs;
		for (int i = 0; i < 0; i++) {
			ScoreDoc score = scoreDoc[i];
			final Document doc = searcher.doc(score.doc);
			paragraph = doc.getField("text").stringValue();

		}

		String paraText = searcher.doc(searcher.search(queryBuilder.toQuery(paraId), 1).scoreDocs[0].doc)
				.getField("text").stringValue();

		return paraText;
	}

	// Author: Laura dietz
	public static class MyQueryBuilder {

		private final StandardAnalyzer analyzer;
		private List<String> tokens;

		public MyQueryBuilder(StandardAnalyzer standardAnalyzer) {
			analyzer = standardAnalyzer;
			tokens = new ArrayList<>(128);
		}

		public BooleanQuery toQuery(String queryStr) throws IOException {

			TokenStream tokenStream = analyzer.tokenStream("paragraphid", new StringReader(queryStr));
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
				booleanQuery.add(new TermQuery(new Term("paragraphid", token)), BooleanClause.Occur.SHOULD);
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

package edu.unh.cs980.yTools;

import edu.unh.cs.treccar_v2.Data;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.io.FileWriter;

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

public class sectionQuery {
	
	private static void usage() {
        System.out.println("Command line parameters: Outline_CBOR Lucene_INDEX Output_Dir");
        System.exit(-1);
    }
	
	static String spotlightAPIurl = "http://model.dbpedia-spotlight.org/en/annotate?";
	
	//Author: Laura and Peihao
	public static void main(String[] args) throws IOException {
		
		if (args.length < 3)
			usage();
					
        System.setProperty("file.encoding", "UTF-8");
        
        String pagesFile = args[0];
        String indexPath = args[1];
        String outputPath = args[2];
        
        File runfile = new File(outputPath + "/runfile_concatenatedHeadings");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);
        
        //paragraphs-run-sections
        IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene");
        searcher.setSimilarity(new BM25Similarity());
        final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());
        final FileInputStream fileInputStream3 = new FileInputStream(new File(pagesFile));
        
        System.out.println("starting searching for sections ...");
        
        int count = 0;
        
        for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream3)) {
            for (List<Data.Section> sectionPath : page.flatSectionPaths()) {
            	
                final String queryId = Data.sectionPathId(page.getPageId(), sectionPath);
                String queryStr = buildSectionQueryStr(page, sectionPath, 1);
                TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
                ScoreDoc[] scoreDoc = tops.scoreDocs;
                for (int i = 0; i < scoreDoc.length; i++) {
                    ScoreDoc score = scoreDoc[i];
                    final Document doc = searcher.doc(score.doc); // to access stored content
                    // print score and internal docid
                    final String paragraphid = doc.getField("paragraphid").stringValue();
                    final float searchScore = score.score;
                    final int searchRank = i+1;

                    //System.out.println(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25");
                    System.out.println(".");
                    //writer.write(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25\n");
                    count ++;
           
                }

            }
        }
        
        writer.flush();
		writer.close();
	
	    stripDuplicatesFromFile(runfile.toString());
        System.out.println("Write " + count + " results\nQuery Done!");
        
	}
	
	//Author: Laura dietz
		public static class MyQueryBuilder {

	        private final StandardAnalyzer analyzer;
	        private List<String> tokens;

	        public MyQueryBuilder(StandardAnalyzer standardAnalyzer){
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
	
	//Author: Laura dietz
	private static IndexSearcher setupIndexSearcher(String indexPath, String typeIndex) throws IOException {
        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
    }
	
	/*
	//Author: Laura dietz
	private static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(page.getPageName());
        for (Data.Section section: sectionPath) {
            queryStr.append(" ").append(section.getHeading());
            //queryStr.append(" ").append(section.ge);
        }
        //System.out.println("queryStr = " + queryStr);
        return queryStr.toString();
    }
    */
	
	private static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath, int flag) {
		//For concatenated sections' headings plus page name 
		//Baseline
		if(flag == 0) {
			StringBuilder queryStr = new StringBuilder();
	        queryStr.append(page.getPageName());
	        //System.out.println("queryStr = " + queryStr);
	        for (List<Data.Section> sectionPath1 : page.flatSectionPaths()) {
        	        for (Data.Section section: sectionPath1) {
                    //System.out.println(section.getHeading());
        	            	queryStr.append(" ").append(section.getHeading());
                }
            }
	        //System.out.println("queryStr = " + queryStr);
	        return queryStr.toString();
		}
		//For just page name 
		else if(flag == 1) {
			StringBuilder queryStr = new StringBuilder();
	        queryStr.append(page.getPageName());
	        System.out.println("queryStr = " + queryStr);
	        for (Data.Section section: sectionPath) {
	            queryStr.append(" ").append(section.getHeading());
	        }
	        System.out.println("queryStr = " + queryStr);
	        return queryStr.toString();
		}
		//For concatenated sections' headings
		else if(flag == 2) {
			StringBuilder queryStr = new StringBuilder();
	        //queryStr.append(page.getPageName());
	        //System.out.println("queryStr = " + queryStr);
	        for (List<Data.Section> sectionPath1 : page.flatSectionPaths()) {
        	        for (Data.Section section: sectionPath1) {
                    System.out.println(section.getHeading());
        	        	    queryStr.append(section.getHeading());
        	        	    queryStr.append(" ");
                }
            }
	        //System.out.println("queryStr = " + queryStr);
	        return queryStr.toString();
		}
		//For just the lowest section heading
		else {
			//StringBuilder queryStr = new StringBuilder();
			String queryStr = " ";
			for (List<Data.Section> sectionPath1 : page.flatSectionPaths()) {
    	            for (Data.Section section: sectionPath1) {
                    //System.out.println(section.getHeading());
    	        	        queryStr = section.getHeading();
                }
            }
			System.out.println(queryStr);
	        return queryStr;
		}
    }
	
	//Author: Nithin
	public static void stripDuplicatesFromFile(String filename) throws IOException {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    Set<String> lines = new HashSet<String>(100000); // maybe should be bigger
	    String line;
	    while ((line = reader.readLine()) != null) {
	        lines.add(line);
	    }
	    reader.close();
	    //System.out.println("Removing Duplicates");
	    BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	    for (String unique : lines) {
	        writer.write(unique);
	        writer.newLine();
	    }
	    writer.close();
	}
	
}
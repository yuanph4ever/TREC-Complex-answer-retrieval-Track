package edu.unh.cs980.entitiesExpansion;

import edu.unh.cs.treccar_v2.Data;

import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs980.yTools.sectionQuery.MyQueryBuilder;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.FileWriter;

import org.w3c.dom.*;

public class QueryExpansionWithEntities {
	
	private static void usage() {
        System.out.println("Command line parameters: Outline_Cbor Lucene_INDEX Output_Dir");
        System.exit(-1);
    }
	
	static String spotlightAPIurl = "http://model.dbpedia-spotlight.org/en/annotate?";
	
	public QueryExpansionWithEntities(String page_file, String index_Dir, String output_Dir, int top) throws IOException{
		
		System.setProperty("file.encoding", "UTF-8");

		/*
        if (args.length < 3)
            usage();

        final String pagesFile = args[0];
        final String indexPath = args[1];
        final String output = args[2] + "/runfile_query_expansion";
        */
		
		final String pagesFile = page_file;
        final String indexPath = index_Dir;
        final String output = output_Dir + "/runfile_query_expansion_" + top;
        
        File runfile = new File(output);
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);

		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");
        searcher.setSimilarity(new BM25Similarity());
        final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());

        final FileInputStream fileInputStream = new FileInputStream(new File(pagesFile));
        for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream)) {
        	
            final String queryId = page.getPageId();
            String queryStr = buildSectionQueryStr(page, Collections.<Data.Section>emptyList());
            //System.out.println("Initial query: " + queryStr);
            
            //---First Round Query---
            TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), top);
            ScoreDoc[] scoreDoc = tops.scoreDocs;
            String expStr = ""; 
            for (int i = 0; i < scoreDoc.length; i++) {
                ScoreDoc score = scoreDoc[i];
                final Document doc = searcher.doc(score.doc); // to access stored content
                // print score and internal docid
                final String para_text = doc.getField("text").stringValue();
                //System.out.println(para_text);
                String httpUrl = spotlightAPIurl + "text=" + para_text.replaceAll("[^A-Za-z0-9]", "%20");
    				String responseStr = getHttpResponse(httpUrl);
    				Pattern pattern = Pattern.compile("http://dbpedia.org/resource/(.*?)\",\"@support");
    				Matcher matcher = pattern.matcher(responseStr);
    				while (matcher.find()) {	
    					//System.out.println(matcher.group(1));
    					//System.out.println(matcher.group(1).replaceAll("[^A-Za-z0-9]", ""));
    					expStr += " " + matcher.group(1);
    				}   
            }
            
            String newExpQuery = getUniqueStr(queryStr + expStr);
            newExpQuery = newExpQuery.replaceAll("[^a-zA-Z0-9]", " ");
            //System.out.println("Expanded Query: " + newExpQuery);
            //newExpQuery = queryStr;
            
            //---Second Round Query---
            tops = searcher.search(queryBuilder.toQuery(newExpQuery), 100);
            scoreDoc = tops.scoreDocs;
            for (int i = 0; i < scoreDoc.length; i++) {
                ScoreDoc score = scoreDoc[i];
                final Document doc = searcher.doc(score.doc); // to access stored content
                final String paragraphid = doc.getField("paragraphid").stringValue();
                final float searchScore = score.score;
                final int searchRank = i+1;

                //System.out.println(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25");
                //System.out.println(".");
                writer.write(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25\n");
                
            }
			
        }
        
        writer.flush();//why flush?
		writer.close();
		
		System.out.println("Query Expansion with Top " + top + " Entities Done!");
		
	}
	
	private static String getUniqueStr(String str) {
		String newStr = "";
		String[] add_terms = str.split(" ");
		//System.out.println("add_terms len: " + add_terms.length);
		ArrayList<String> uniqueWords = new ArrayList<>();
		for(int k = 0; k < add_terms.length; k ++) {
			String w = add_terms[k];
			if(!uniqueWords.contains(w)) {
				uniqueWords.add(w);
				newStr += w + " ";
			}
		}
		return newStr;
	}
	
	//Author: Laura dietz
			static class MyQueryBuilder {

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
	
	//Author: Laura Dietz
	private static IndexSearcher setupIndexSearcher(String indexPath, String typeIndex) throws IOException {
        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
    }
	
	//Author: Laura Dietz
	private static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(page.getPageName());
        for (Data.Section section: sectionPath) {
            queryStr.append(" ").append(section.getHeading());
        }
        //System.out.println("queryStr = " + queryStr);
        return queryStr.toString();
    }
	
	//Author: Kaixin Zhang
		private static String getHttpResponse(String urlStr) {
			try {

				URL url = new URL(urlStr);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");

				conn.setRequestProperty("Accept", "application/json");
				// conn.setReadTimeout(httpRequest_timeout);
				if (conn.getResponseCode() != 200) {
					System.out.println("Failed to connect to " + urlStr + " with HTTP error code: " + conn.getResponseCode());
					if (conn.getResponseCode() == 401) {
					}
					return null;
				}

				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

				String output = "";// br.readLine();
				String line = "";
				while ((line = br.readLine()) != null) {
					output += line;
				}

				conn.disconnect();
				return output;
			} catch (Exception e) {
				return null;
			}
		}
	

}

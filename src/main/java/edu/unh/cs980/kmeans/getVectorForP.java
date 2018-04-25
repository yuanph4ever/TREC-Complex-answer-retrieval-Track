package edu.unh.cs980.kmeans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;	
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import edu.unh.cs980.yTools.sectionQuery.MyQueryBuilder;

public class getVectorForP {
	
	public static void main(String[] args) throws IOException {
					
        System.setProperty("file.encoding", "UTF-8");
        
        String indexPath = args[0];
        String outputPath = args[1];
        
        File runfile = new File(outputPath + "/para_vectors_tfidf_entities_bias_10k");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);
		
	    Path path = FileSystems.getDefault().getPath(indexPath, "paragraph.lucene.vectors");
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
		
        System.out.println("starting reading ..."); 
        
        int count = 0;
        
        int num_of_entities = 0;
        
        int num_of_trash = 0;
   
        for(int i = 0; i < reader.maxDoc(); i = i + 100) {
        	
        		final Terms terms = reader.getTermVector(i, "body");
        		
        		if (terms != null) {       
        			
        			final Document doc = reader.document(i);
            		
            		String doc_id = doc.getField("paragraphid").stringValue();
            		
            		//System.out.println(doc.getField("paragraphid").stringValue());
            		
            		String doc_para = doc.getField("text").stringValue();
            		
            		//System.out.println(doc_para + "\n");
            		
            		List<String> entities = new ArrayList<String>();
            		
            		entities = getEntities(doc_para);
            		
            		//System.out.println(entities);
            		
            		/*
    				 * just use paragraphs which contain entities, which means see paragraph which has no entity as trash
    				 */
            		if(entities != null) {
            			
            			writer.write("doc:" + doc_id);
            			
            			TermsEnum termsEnum = terms.iterator();
            			BytesRef term = null;
            			while ((term = termsEnum.next()) != null) {
            				//System.out.println("term: " + term.utf8ToString());
            				//System.out.println("doc fre: " + termsEnum.docFreq());
            				//System.out.println("term fre: " + termsEnum.totalTermFreq()); 
            				
            				String word = term.utf8ToString();
            			
            				/*
            				if(isEntity(word)) {
            					float value = 2 * ( (float)termsEnum.totalTermFreq()/termsEnum.docFreq() );
            					writer.write("--->" + word + ":" + value );
            					num_of_entities ++;
            					
            				}else {
            					float value = (float)termsEnum.totalTermFreq()/termsEnum.docFreq();
            					writer.write("--->" + word + ":" + value );
            				}
            				*/
            				
            					/*
                				 * if term is entity, then use 2*tfidf as the value of it in vector
                				 * otherwise, use tfidf as the value of it in vector
                				 */
            					if(entities.contains(word)) {
            						//System.out.println("Entitiy: " + word);
                					float value = 2 * ( (float)termsEnum.totalTermFreq()/termsEnum.docFreq() );
                					writer.write("--->" + word + ":" + value );
                					num_of_entities ++;
                				}else{
                					//System.out.println("Non_Entitiy: " + word);
                					float value = (float)termsEnum.totalTermFreq()/termsEnum.docFreq();
                					writer.write("--->" + word + ":" + value );
                				}
            					
            				
            				
            			}
            			
            			writer.write("\n");
            			
            			count ++;
            			
            		}else {
            			num_of_trash ++;
            		}
            					
        			      			
        		}
        		
        		if(count % 1000 == 0) {
        			System.out.println("write " + count + " paragraphs");
        		}
        		
        		if(count == 10000) {
        			break;
        		}
        	
        }
        
        writer.flush();
		writer.close();
	
		System.out.println("Work Done");
        System.out.println("Write " + count + " paragraphs out of " + reader.maxDoc()); 
        System.out.println("Detect " + num_of_entities + " entities from paragraphs");
        System.out.println("Detect " + num_of_trash + " junk paragraphs");
        System.out.println("Find output " + outputPath + "/para_vectors_tfidf_20k");
	}
	
	static String spotlightAPIurl = "http://model.dbpedia-spotlight.org/en/annotate?";
	
	private static List<String> getEntities(String content){
		String httpUrl = spotlightAPIurl + "text=" + content.replaceAll(" ", "%20");
		String responseStr = getHttpResponse(httpUrl);
		//System.out.println(responseStr);
		if(responseStr == null) {
			return null;
		}else {
			//System.out.println(responseStr);
			//Pattern pattern = Pattern.compile("http://dbpedia.org/resource/(.*?)\",\"@support");
			Pattern pattern = Pattern.compile("\"@surfaceForm\":\"(.*?)\",\"@offset\"");
			Matcher matcher = pattern.matcher(responseStr);
			List<String> entities = new ArrayList<String>();
			while (matcher.find()) {	
				//System.out.println(matcher.group(1));
				//System.out.println(matcher.group(1).replaceAll("[^A-Za-z0-9]", ""));
				entities.add(matcher.group(1));
			}   	
			return entities;
		}
	}
	
	
	private static boolean isEntity(String term) {
		String httpUrl = spotlightAPIurl + "text=" + term.replaceAll("[^A-Za-z0-9]", "%20");
		String responseStr = getHttpResponse(httpUrl);
		//Pattern pattern = Pattern.compile("http://dbpedia.org/resource/(.*?)\",\"@support");
		Pattern pattern = Pattern.compile("\"@surfaceForm\":\"(.*?)\",\"@offset\"");
		Matcher matcher = pattern.matcher(responseStr);
		while (matcher.find()) {	
			System.out.println(matcher.group(1));
			//System.out.println(matcher.group(1).replaceAll("[^A-Za-z0-9]", ""));
			return true;
		}   
		return false;
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
			//System.out.println("Failed to connect to " + urlStr + " with HTTP error code: " + conn.getResponseCode());
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


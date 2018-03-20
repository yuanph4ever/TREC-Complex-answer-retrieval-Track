package edu.unh.cs980.peihao;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import edu.unh.cs980.sectionQuery.MyQueryBuilder;

public class getVectorForP {
	
public static void main(String[] args) throws IOException {
					
        System.setProperty("file.encoding", "UTF-8");
        
        String indexPath = args[0];
        String outputPath = args[1];
        
        File runfile = new File(outputPath + "/para_vectors_10000");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);
		
	    Path path = FileSystems.getDefault().getPath(indexPath, "paragraph.lucene.vectors");
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
		
        System.out.println("starting reading ..."); 
        
        int count = 0;//count for non terms vector
   
        for(int i =0; i < reader.maxDoc(); i ++) {
        	
        		final Terms terms = reader.getTermVector(i, "body");
        			
        		final Document doc = reader.document(i);
        		
        		String doc_id = doc.getField("paragraphid").stringValue();
        		
        		//System.out.println(doc.getField("paragraphid").stringValue());
        				
        		writer.write("doc:" + doc_id);
        		
        		if (terms != null) {       			
        			
        			TermsEnum termsEnum = terms.iterator();
        			BytesRef term = null;
        			while ((term = termsEnum.next()) != null) {
        				//System.out.println("term: " + term.utf8ToString());
        				//System.out.println("doc fre: " + termsEnum.docFreq());
        				//System.out.println("term fre: " + termsEnum.totalTermFreq());                    
        				
        				writer.write("--->" + term.utf8ToString() + ":" + termsEnum.totalTermFreq());
        				
        			}
        			      			
        		}
        		else {
        			count ++;
        		}
        		
        		writer.write("\n");
        		
        	        	
        }
        
        writer.flush();
		writer.close();
	
		System.out.println("Number of non terms doc: " + count);
        System.out.println("Number of doc: " + reader.maxDoc());       
	}
	
}

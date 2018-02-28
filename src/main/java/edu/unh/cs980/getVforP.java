package edu.unh.cs980;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class getVforP {
	
	public getVforP(String indexPath, String outputPath) throws IOException{
		
		System.setProperty("file.encoding", "UTF-8");
        
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

package edu.unh.cs980.classifier;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;

import edu.unh.cs980.wikiObjects.ArticleObjects;
import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class ArticleDao {

	private static List<ArticleObjects> createArticleObjectList(String paraCorpus) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(new File(paraCorpus));
		//List<ArticleObjects> articleDaoList = new ArrayList<ArticleObjects>();

		
		

	    
	        final String outputPath = "/Users/Nithin/Desktop";
	       
	        Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fileInputStream);
	        File paraText = new File(outputPath + "/paraText");
	        paraText.createNewFile();
	        FileWriter writer = new FileWriter(paraText);
	        for (int i=1; paragraphIterator.hasNext(); i++){
	       //check for text
	        	System.out.println(i);
	        writer.write( paragraphIterator.next().getTextOnly());
	        
	    }
	    
	    writer.flush();
	    writer.close();
		

		return null;

	}
	
	public static void main(String args[]) throws IOException
	{
		System.out.println("started");
		System.out.println(createArticleObjectList("/Users/Nithin/Desktop/paragraphCorpus/dedup.articles-paragraphs.cbor"));
		System.out.println("done");
	}
}

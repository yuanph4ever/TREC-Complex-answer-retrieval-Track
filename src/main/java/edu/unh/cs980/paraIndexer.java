package edu.unh.cs980;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.CborFileTypeException;
import edu.unh.cs.treccar_v2.read_data.CborRuntimeException;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;

public class paraIndexer {
	
	//Author: Laura and Peihao
	public static void main(String[] args) throws IOException {
		
        System.setProperty("file.encoding", "UTF-8");

        String paragraphsFile = args[0];
        String indexPath = args[1];
        
        FileInputStream fileInputStream2 = new FileInputStream(new File(paragraphsFile));

        System.out.println("Creating paragraph index in " + indexPath);
        
        IndexWriter indexWriter = setupIndexWriter(indexPath, "paragraph.lucene");
        
        Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fileInputStream2);

        for (int i=1; paragraphIterator.hasNext(); i++){
        	
            Document doc = paragraphToLuceneDoc(paragraphIterator.next());
         
            indexWriter.addDocument(doc);
            if (i % 10000 == 0) {
                System.out.println("Index done for " + i + "k paragraphs");
                indexWriter.commit();
            }
            
        }

        System.out.println("\n Done indexing.");

        indexWriter.commit();
        indexWriter.close();
    }
	
	//Author: Laura 
	private static IndexWriter setupIndexWriter(String indexPath, String typeIndex) throws IOException {
        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
        Directory indexDir = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        return new IndexWriter(indexDir, config);
    }
	
	//Author: Laura 
	private static Document paragraphToLuceneDoc(Data.Paragraph p) {
        final Document doc = new Document();
        final String content = p.getTextOnly(); // <-- Todo Adapt this to your needs!
        doc.add(new TextField("text", content, Field.Store.YES));
        doc.add(new StringField("paragraphid", p.getParaId(), Field.Store.YES));  // don't tokenize this!
        return doc;
    }

}

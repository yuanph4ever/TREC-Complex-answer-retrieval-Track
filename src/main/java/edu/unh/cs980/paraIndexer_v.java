package edu.unh.cs980;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

/* 
 * This program works for getting index files with term vectors 
 */

public class paraIndexer_v {
	
	private static void usage() {
        System.out.println("Command line parameters: paragraphCBOR LuceneINDEX");
        System.exit(-1);
    }
	
		public static void main(String[] args) throws IOException {
			
			if (args.length < 2)
	            usage();
			
	        System.setProperty("file.encoding", "UTF-8");

	        String paragraphsFile = args[0];
	        String indexPath = args[1];
	        
	        FileInputStream fileInputStream = new FileInputStream(new File(paragraphsFile));

	        System.out.println("Creating paragraph index in " + indexPath);
	        
	        IndexWriter indexWriter = setupIndexWriter(indexPath, "paragraph.lucene.vectors");
	        
	        Iterator<Data.Paragraph> paragraphIterator = DeserializeData.iterParagraphs(fileInputStream);

	        for (int i=1; paragraphIterator.hasNext(); i++){
	        	
	            Document doc = paragraphToLuceneDoc(paragraphIterator.next());
	         
	            indexWriter.addDocument(doc);
	            if (i % 10000 == 0) {
	                System.out.println("Index done for " + i + "k paragraphs");
	                indexWriter.commit();
	                //break;
	            }
	            
	        }

	        System.out.println("\n Done indexing.");

	        indexWriter.commit();
	        indexWriter.close();
	    }
		
		private static IndexWriter setupIndexWriter(String indexPath, String typeIndex) throws IOException {
	        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
	        Directory indexDir = FSDirectory.open(path);
	        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	        return new IndexWriter(indexDir, config);
	    }
		
		private static Document paragraphToLuceneDoc(Data.Paragraph p) {
	        final Document doc = new Document();
	        FieldType type = new FieldType();
	        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
	        type.setStored(true);
	        type.setStoreTermVectors(true);
	        final String content = p.getTextOnly(); // <-- Todo Adapt this to your needs!
	        //Field field = new Field("body", content, type);
	        doc.add(new StoredField("body", content, type));
	        doc.add(new TextField("text", content, Field.Store.YES));
	        doc.add(new StringField("paragraphid", p.getParaId(), Field.Store.YES));  // don't tokenize this!   
	        return doc;
	    }

}

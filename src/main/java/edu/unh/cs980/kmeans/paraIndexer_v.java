package edu.unh.cs980.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
	                System.out.println("Index done for " + i + " paragraphs");
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

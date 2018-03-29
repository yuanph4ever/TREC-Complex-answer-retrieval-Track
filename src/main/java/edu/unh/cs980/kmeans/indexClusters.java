package edu.unh.cs980.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class indexClusters {
	
	private static void usage() {
        System.out.println("Command line parameters: input_Clusters_Path output_Index_Path");
        System.exit(-1);
    }
	
		public static void main(String[] args) throws IOException {
			
			if (args.length < 2)
	            usage();
			
	        System.setProperty("file.encoding", "UTF-8");

	        String clusterPath = args[0];
	        String indexPath = args[1];
	        
	        File folder = new File(clusterPath);
	        File[] listOfFiles = folder.listFiles();
	        
	        System.out.println("Creating cluster index in " + indexPath);
	        
	        IndexWriter indexWriter = setupIndexWriter(indexPath, "entites.cluster.lucene.index");

	        int num_of_cluster = 0;
	        for (File file : listOfFiles) {
	            if (file.isFile()) {
	            		/*
	            		if(file.getName().startsWith("Cluster")) {
	            			System.out.println("Creating index for " + file.getName());
	            			Document doc = clusterToLuceneDoc(file, file.getName());
	            			indexWriter.addDocument(doc);
	            			num_of_cluster += 1;
	            		}	
	            		*/
	            		System.out.println("Creating index for " + file.getName());
            			Document doc = clusterToLuceneDoc(file, file.getName());
            			indexWriter.addDocument(doc);
            			num_of_cluster += 1;
	            }
	        }
	        
	        System.out.println("\nDone indexing for " + num_of_cluster + " clusters.\n");

	        indexWriter.commit();
	        indexWriter.close();

	    }
		
		private static IndexWriter setupIndexWriter(String indexPath, String typeIndex) throws IOException {
	        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
	        Directory indexDir = FSDirectory.open(path);
	        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
	        return new IndexWriter(indexDir, config);
	    }
		
		private static Document clusterToLuceneDoc(File file, String cluster_id) throws IOException {
			final Document doc = new Document();
	        FieldType type = new FieldType();
	        type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
	        type.setStored(true);
	        type.setStoreTermVectors(true);
	        BufferedReader br = new BufferedReader(new FileReader(file));
	        String line;
	        String content = "";
	        while ((line = br.readLine()) != null) {
	          //System.out.println(line);
	        	  content += line + " ";
	        }
	        doc.add(new StoredField("body", content, type));
	        doc.add(new TextField("text", content, Field.Store.YES));
	        doc.add(new StringField("clusterid", cluster_id, Field.Store.YES));  // don't tokenize this!   
	        return doc;
		}

}

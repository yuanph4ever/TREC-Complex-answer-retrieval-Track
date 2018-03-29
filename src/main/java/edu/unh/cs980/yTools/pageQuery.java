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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.FileWriter;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class pageQuery {
	
	private static void usage() {
        System.out.println("Command line parameters: flag Lucene_INDEX Output_Dir *Outline_CBOR *Clusters_info");
        System.exit(-1);
    }
	
	public static void main(String[] args) throws IOException {
		
		if (args.length < 3)
			usage();
		
        System.setProperty("file.encoding", "UTF-8");
        
        String flag = args[0];
        /*
        if(flag.equalsIgnoreCase("-hw")) {
        	    System.out.println("Query start...");
            String pagesFile = args[3];
            String indexPath = args[1];
            String outputPath = args[2];
        		HeadingWeights hW = new HeadingWeights(pagesFile, indexPath, outputPath);
        }
        */
        
        if (flag == "-cluster") {
            String pagesFile = args[3];
            String indexPath = args[1];
            String outputPath = args[2];
        		String clusters = args[4];
        		File rf = new File(outputPath + "/runfile_page_with_clusters");
        		rf.createNewFile();
        		FileWriter wtr = new FileWriter(rf); 	
        		String[] dim = new String[56341];
        		ArrayList<double[]> al = new ArrayList<double[]>();
        		
        		try {
        			
        			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        			org.w3c.dom.Document doc = docBuilder.parse (new File(clusters));
        			
        			// normalize text representation
        			doc.getDocumentElement().normalize ();
        			System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
        			
        			NodeList listOfDimensions = doc.getElementsByTagName("dimension");
        			int total_dimensions = listOfDimensions.getLength();
        			System.out.println("Total no of dimensions : " + total_dimensions);
        			
        			Node dimNode = listOfDimensions.item(0);
        			
        			Element dimElement = (Element)dimNode; 

        			//-------
        			NodeList dimList = dimElement.getElementsByTagName("description");
        			Element dim_e = (Element)dimList.item(0);

        			NodeList textFNList_d = dim_e.getChildNodes();
        			//System.out.println("Dimensions : " + ((Node)textFNList_d.item(0)).getNodeValue().trim());
        			
        			String dim_str = ((Node)textFNList_d.item(0)).getNodeValue().trim();
        			
        			dim = dim_str.split(" ");
        			
        			/*
        			int count = 0;
                    for (String temp: dim){
                    	    count += 1;
                    	    if (count == 10)
                    	    		break;
                        System.out.println(temp);
                     }
        	        */
        			
        			
        			NodeList listOfCentroids = doc.getElementsByTagName("centroid");
        			int total_centroids = listOfCentroids.getLength();
        			System.out.println("Total no of centroids : " + total_centroids);
        			
        			for(int s=0; s<listOfCentroids.getLength() ; s++){


        				Node CentroidNode = listOfCentroids.item(s);
        				if(CentroidNode.getNodeType() == Node.ELEMENT_NODE){


        					Element CentroidElement = (Element)CentroidNode; 

        					//-------
        					NodeList CentroidList = CentroidElement.getElementsByTagName("description");
        					Element Centroid = (Element)CentroidList.item(0);

        					NodeList textFNList = Centroid.getChildNodes();
        					System.out.println("Centroid : " + ((Node)textFNList.item(0)).getNodeValue().trim());
        					
        					String vec_str = ((Node)textFNList.item(0)).getNodeValue().trim();
        					
        					String[] vec_s = new String[56341];
        					double[] vec_d = new double[vec_s.length];
        					for (int i = 0; i<vec_s.length; i++) 
        						vec_d[i] = Double.valueOf(vec_s[i]);
        					
        					al.add(vec_d);


        				}//end of if clause


        			}//end of for loop with s var
        			
        			
        			
        		}catch (SAXParseException err) {
        			System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
        			System.out.println(" " + err.getMessage ());

        			}catch (SAXException e) {
        			Exception x = e.getException ();
        			((x == null) ? e : x).printStackTrace ();

        			}catch (Throwable t) {
        			t.printStackTrace ();
        			}

            
        		for(int i = 0; i < al.size(); i ++) {
        			double[] cen_vec = al.get(i);
        			for(int j = 0; j < cen_vec.length; j++) {
        				System.out.println(cen_vec[j] + "\n");
        			}
        		}
             
        }
        
        /*
        if(flag.equalsIgnoreCase("-v")) {
        	    System.out.println("Start getting vector for paragraph");
        	    String indexPath = args[1];
            String outputPath = args[2];
        		getVforP v = new getVforP(indexPath, outputPath);
        }
        */
        
        if(flag == "r+") {
        	    String indexPath = args[1];
            String outputPath = args[2];
            String pagesFile = args[3];
        		File runfile = new File(outputPath + "/runfile_page_vectors");
        		runfile.createNewFile();
        		FileWriter writer = new FileWriter(runfile);		
            
            //paragraphs-run-pages
        		IndexSearcher searcher = setupIndexSearcher(indexPath, "paragraph.lucene.vectors");
            searcher.setSimilarity(new BM25Similarity());
            final MyQueryBuilder queryBuilder = new MyQueryBuilder(new StandardAnalyzer());
            final FileInputStream fileInputStream = new FileInputStream(new File(pagesFile));
            
            System.out.println("starting searching for pages ...");
            
            int count = 0;
            
            for (Data.Page page : DeserializeData.iterableAnnotations(fileInputStream)) {
            	
                final String queryId = page.getPageId();

                String queryStr = buildSectionQueryStr(page, Collections.<Data.Section>emptyList(), 1);

                TopDocs tops = searcher.search(queryBuilder.toQuery(queryStr), 100);
                ScoreDoc[] scoreDoc = tops.scoreDocs;
                for (int i = 0; i < scoreDoc.length; i++) {
                    ScoreDoc score = scoreDoc[i];
                    final Document doc = searcher.doc(score.doc); // to access stored content
                    // print score and internal docid
                    final String paragraphid = doc.getField("paragraphid").stringValue();
                    final float searchScore = score.score;
                    final int searchRank = i+1;
                    
                    //---
                    final String para_text = doc.getField("text").stringValue();

                    //System.out.println(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25");
                    System.out.println(".");
                    writer.write(queryId+" Q0 "+paragraphid+" "+searchRank + " "+searchScore+" Lucene-BM25" + " // " + queryStr + 
                    		" // " + para_text + "\n");
                    count ++;
                }
                
                writer.write("---\n");

            }
            
            writer.flush();//why flush?
    			writer.close();
    			
    			System.out.println("Write " + count + " results\nQuery Done!");  
        	
        }
        
            
        
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
	
	//Author: Laura dietz
	private static IndexSearcher setupIndexSearcher(String indexPath, String typeIndex) throws IOException {
        Path path = FileSystems.getDefault().getPath(indexPath, typeIndex);
        Directory indexDir = FSDirectory.open(path);
        IndexReader reader = DirectoryReader.open(indexDir);
        return new IndexSearcher(reader);
    }
	
	private static String buildSectionQueryStr(Data.Page page, List<Data.Section> sectionPath, int flag) {
		//For page name plus all sections' headings
		if(flag == 0) {
			StringBuilder queryStr = new StringBuilder();
	        queryStr.append(page.getPageName());
	        System.out.println("queryStr = " + queryStr);
	        for (List<Data.Section> sectionPath1 : page.flatSectionPaths()) {
        	        for (Data.Section section: sectionPath1) {
                    //System.out.println(section.getHeading());
        	        	    queryStr.append(" ").append(section.getHeading());
                }
            }
	        System.out.println("queryStr = " + queryStr);
	        return queryStr.toString();
		}
		//For just page name 
		else if(flag == 1) {
			StringBuilder queryStr = new StringBuilder();
	        queryStr.append(page.getPageName());
	        for (Data.Section section: sectionPath) {
	            queryStr.append(" ").append(section.getHeading());
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
	
	/*
	private static String[] xmlReader_dim(String filename) {
		
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = docBuilder.parse (new File(filename));
			
			// normalize text representation
			doc.getDocumentElement().normalize ();
			System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
			
			NodeList listOfDimensions = doc.getElementsByTagName("dimension");
			int total_dimensions = listOfDimensions.getLength();
			System.out.println("Total no of dimensions : " + total_dimensions);
			
			Node dimNode = listOfDimensions.item(0);
			
			Element dimElement = (Element)dimNode; 

			//-------
			NodeList dimList = dimElement.getElementsByTagName("description");
			Element dim_e = (Element)dimList.item(0);

			NodeList textFNList_d = dim_e.getChildNodes();
			//System.out.println("Dimensions : " + ((Node)textFNList_d.item(0)).getNodeValue().trim());
			
			String dim_str = ((Node)textFNList_d.item(0)).getNodeValue().trim();
			
			String[] dim = new String[56341];
			
			dim = dim_str.split(" ");
			
			return dim;
			
			
			int count = 0;
            for (String temp: dim){
            	    count += 1;
            	    if (count == 10)
            	    		break;
                System.out.println(temp);
             }
             
			
			
	        
			
			NodeList listOfCentroids = doc.getElementsByTagName("centroid");
			int total_centroids = listOfCentroids.getLength();
			System.out.println("Total no of centroids : " + total_centroids);
			
			for(int s=0; s<listOfCentroids.getLength() ; s++){


				Node CentroidNode = listOfCentroids.item(s);
				if(CentroidNode.getNodeType() == Node.ELEMENT_NODE){


					Element CentroidElement = (Element)CentroidNode; 

					//-------
					NodeList CentroidList = CentroidElement.getElementsByTagName("description");
					Element Centroid = (Element)CentroidList.item(0);

					NodeList textFNList = Centroid.getChildNodes();
					System.out.println("Centroid : " + ((Node)textFNList.item(0)).getNodeValue().trim());


				}//end of if clause


			}//end of for loop with s var
			
			
			
		}catch (SAXParseException err) {
			System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());

			}catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();

			}catch (Throwable t) {
			t.printStackTrace ();
			}

	}
	*/
	
	
}

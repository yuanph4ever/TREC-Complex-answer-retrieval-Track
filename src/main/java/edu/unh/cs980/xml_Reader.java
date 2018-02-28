package edu.unh.cs980;

import java.io.File;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class xml_Reader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File("/Users/paul.yuan/Desktop/kmeans_result.xml"));
			
			// normalize text representation
			doc.getDocumentElement ().normalize ();
			System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
			
			NodeList listOfDimensions = doc.getElementsByTagName("dimension");
			int total_dimensions = listOfDimensions.getLength();
			System.out.println("Total no of dimensions : " + total_dimensions);
			
			Node dimNode = listOfDimensions.item(0);
			
			Element dimElement = (Element)dimNode; 

			//-------
			NodeList dimList = dimElement.getElementsByTagName("description");
			Element dim = (Element)dimList.item(0);

			NodeList textFNList_d = dim.getChildNodes();
			System.out.println("Centroid : " + ((Node)textFNList_d.item(0)).getNodeValue().trim());
		
			
			/*
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
			*/
			
			
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

}

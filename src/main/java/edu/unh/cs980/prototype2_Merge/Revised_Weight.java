package example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.Data.Page.SectionPathParagraphs;
import edu.unh.cs.treccar_v2.Data.ParaBody;
import edu.unh.cs.treccar_v2.Data.Paragraph;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
//import lucene.dbpedia.Index.ValueComparator;

public class Revised_Weight {
	
	static class ValueComparator implements Comparator<String> {
	    Map<String, Integer> base;

	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	static class ValueComparatorF implements Comparator<String> {
	    Map<String, Float> base;

	    public ValueComparatorF(Map<String, Float> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	public static String url = "http://localhost:2222/rest/annotate";
	public static String fileName = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Prototype3\\HW_section_test\\runfile_section_Lowest_Pr2";
	
	public static Map<String, Integer> paraidtoentities = new HashMap<>();
	public static Map<String, List<String>> paraidtoentities1 = new HashMap<>();
	public static Map<String, List<String>> cattoparaidlist = new HashMap<>();
	public static Map<String, String> paraidcattoline = new HashMap<>();
	public static Set<String> st = new HashSet<>();

	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		// Below code will read para_wise.txt and put all categories and list of paraids
		// for each categories into a hashmap
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine();
		
		while(line != null) {
			
			String[] linedetails = line.trim().split("\\s+");
			
			if(!st.contains(linedetails[2])) {
				st.add(linedetails[2]);
			}
			
			if(!cattoparaidlist.containsKey(linedetails[0])) {		
				List<String> ls = new ArrayList<>();
				ls.add(linedetails[2]);
				cattoparaidlist.put(linedetails[0], ls);
			}				
			else {
				List<String> ls = cattoparaidlist.get(linedetails[0]);
				ls.add(linedetails[2]);
				cattoparaidlist.put(linedetails[0], ls);
				
			}
			
			if(!paraidcattoline.containsKey(linedetails[0]+linedetails[2])) {
				paraidcattoline.put(linedetails[0]+linedetails[2], line);
			}
		
			line = br.readLine();
		}
		
		br.close();
		System.out.println("Sucessfully created data structures");
		System.out.println(st.size()+" "+cattoparaidlist.size()+" "+ paraidcattoline.size());
		//System.exit(-1);
		
		
		// Put the file dedup.articles-paragraphs.cbor ,16GB size, in the location mentioned in
		// folderLocation variable mentioned below. The below program will look for the files
		// in the location paragraphCorpus folder and process one file at a time.
		
		String folderLocation = "C:\\Users\\ddash\\Documents\\Spring2018\\dedup";
		File folder = new File(folderLocation);
		
		long startTime1 = System.currentTimeMillis();
		File[] lisOfFiles = folder.listFiles();
		for(File f: lisOfFiles) {
			if(f.isFile()) {
				System.out.println("Now reading file - " + f);
				
				InputStream is = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(is);
				
				Iterator<Data.Paragraph> ip = DeserializeData.iterParagraphs(bis);
				
				while(ip.hasNext() && st.size()>0) {
					String paraid = ip.next().getParaId();
					
					if(st.contains(paraid)) {
						
						System.out.println("Found Para: "+ paraid);
						String paraContent = ip.next().getTextOnly();
						ArrayList<String> entities = getAnchors(paraContent);
					    
					    paraidtoentities.put(paraid, entities.size());
					    paraidtoentities1.put(paraid, entities);
					    st.remove(paraid);
					}  
					
				}
				
				long endTime1   = System.currentTimeMillis();
	        	long totalTime1 = endTime1 - startTime1;
	        	System.out.println("Time to build hashmap: "+ totalTime1 +". Now writing hashmap to disk");
				
	        	// Below code will save the hashmap created with the paraids and the count of entities in the paraid
	        	// to disk in the fileName mentioned in "fileName1" below.
	        	
	        	long startTime2 = System.currentTimeMillis();
				String file1 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\curl";
				FileWriter fw1 = new FileWriter(file1);
				StringBuilder builder1 = new StringBuilder();
				if(paraidtoentities.size()>0) {
					for(Map.Entry<String, Integer> e: paraidtoentities.entrySet()) {
						builder1.append(e.getKey() +" "+e.getValue());
		                builder1.append(System.getProperty("line.separator"));
					}
				}
				fw1.write(builder1.toString());
				fw1.close();
				builder1 = null;

				
				// Below code will save the hashmap created with the paraids and the list of entities present in paraid
	        	// to disk in the fileName mentioned in "fileName2" below. 
	   
				String file2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\paraidtoentity";
				FileWriter fw2 = new FileWriter(file2);
				StringBuilder builder2 = new StringBuilder();
				if(paraidtoentities1.size()>0) {
					for(Map.Entry<String, List<String>> e: paraidtoentities1.entrySet()) {
						builder2.append(e.getKey() +" "+e.getValue());
		                builder2.append(System.getProperty("line.separator"));
					}
				}
				fw2.write(builder2.toString());
				fw2.close();
				builder2 = null;
	        	
				
				long endTime2   = System.currentTimeMillis();
	        	long totalTime2 = endTime2 - startTime2;
				System.out.println("Time to save hashmap to disk: "+ totalTime2);
				
			}
		}
		
		// Below code will rearrange the input para_wise.txt to sort 
		// it by the paraids with maximum entities
    	
    	String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\output1.txt"; // Output File
    	String fileName4 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\output2.txt";
    	String fileName6 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\output3.txt"; // rank+BM25 score
		FileWriter fw2 = new FileWriter(fileName2,true);
		FileWriter fw4 = new FileWriter(fileName4,true);
		FileWriter fw6 = new FileWriter(fileName6,true);
		StringBuilder builder2 = new StringBuilder();
		StringBuilder builder4 = new StringBuilder();
		StringBuilder builder6 = new StringBuilder();
		try {
			for(Map.Entry<String, List<String>> e: cattoparaidlist.entrySet()) {
	    		List<String> lst = e.getValue();
	    		int i = e.getValue().size();
	    		Map<String, Integer> hm = new HashMap<>();
	    		for(String s: lst) {
	    			hm.put(s, paraidtoentities.get(s)==null? 0: paraidtoentities.get(s));
	    		}
	    		Map<String, Float> hm1 = new HashMap<>();
	    		
	    		ValueComparator bvc1 = new ValueComparator(hm);
	            TreeMap<String, Integer> tm1 = new TreeMap<>(bvc1);
	    		tm1.putAll(hm);

	    		//String line2 = null;
	    		int pos = 1;
	    		if(tm1.size()>0) {
	    			StringBuilder b2 = new StringBuilder();
	    			for(Map.Entry<String, Integer> e1: tm1.entrySet()) {
	    				//line2 = e.getKey() + e1.getKey();
	    				//String sl = paraidcattoline.get(line2);
	    				//String[] sln = sl.split("\\s+");
	    				
	    				String[] sln = paraidcattoline.get(e.getKey() + e1.getKey()).split("\\s+");
	    				
	    				sln[3] = Integer.toString(pos);
	    				
	    				for(String s: sln) {
	    					b2.append(s);
	    					b2.append(" ");
	    				}
	    				
	    				
	    				float f = (float) (i+1-pos)/i;
	    				//float f = (float) (1/(pos));
	    				float f1 = Float.parseFloat(sln[4]) + f;

	    				hm1.put(e1.getKey(), f1);
	    				
	    				builder2.append(b2.toString());
	                    builder2.append(System.getProperty("line.separator"));
	                    pos++;
	                    b2.setLength(0);
	    			}
	    		}
	    		fw2.write(builder2.toString());
	    		
	    		builder2.setLength(0);
	    		
	    		ValueComparatorF bvc2 = new ValueComparatorF(hm1);
	            TreeMap<String, Float> tm2 = new TreeMap<>(bvc2);
	    		tm2.putAll(hm1);
	    		//String line4 = null;
	    		int pos1 = 1;
	    		
	    		if(tm2.size()>0) {
	    			StringBuilder b2 = new StringBuilder();
    	    		StringBuilder b6 = new StringBuilder();
	    			for(Map.Entry<String, Float> e1: tm2.entrySet()) {
	    				//line4 = e.getKey() + e1.getKey();
	    				//String sl = paraidcattoline.get(line4);
	    				//String[] sln = sl.split("\\s+");
	    				
	    				
	    	    		
	    				String[] sln = paraidcattoline.get(e.getKey() + e1.getKey()).split("\\s+");
	    				sln[3] = Integer.toString(pos1);
	    				for(String s: sln) {
	    					b2.append(s);
	    					b2.append(" ");
	    				}
	    				
	    				String[] sln1 = sln;
	    				sln1[4] =  Float.toString(pos1 + Float.parseFloat(sln[4]));
	    				for(String s: sln1) {
	    					b6.append(s);
	    					b6.append(" ");
	    				}
	    				
	    				builder6.append(b6.toString());
	                    builder6.append(System.getProperty("line.separator"));
	    				
	    				builder4.append(b2.toString());
	                    builder4.append(System.getProperty("line.separator"));
	                    
	                    b6.setLength(0);
	                    b2.setLength(0);
	                    pos1++;
	    			}
	    		}
	    		
	    		fw6.write(builder6.toString());
	    		
	    		builder6.setLength(0);
	    		
	    		fw4.write(builder4.toString());
	    		
	    		builder4.setLength(0);
	    		
	    		
			}
			
			fw2.close();
			fw6.close();
			fw4.close();
    	}catch(Exception e) {
    		System.out.println(e);
    	}
    	
		
		long endTime   = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	
    	System.out.println("Total time taken - " + totalTime);

	}
	
	private static ArrayList<String> getAnchors(String data) {
        ArrayList<String> entities = new ArrayList<>();

        try {
            // Connect to database, retrieve entity-linked urls
            Document doc = Jsoup.connect(url)
                    .data("text", data)
                    .post();
            Elements links = doc.select("a[href]");

            // Parse urls, returning only the last word of the url (after the last /)
            for (Element e : links) {
                String title = e.attr("title");
                title = title.substring(title.lastIndexOf("/") + 1);
                entities.add(title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }


}

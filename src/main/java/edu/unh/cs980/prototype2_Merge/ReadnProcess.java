package prototype2_Merge;

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
import prototype2_Merge.Index.ValueComparator;

public class ReadnProcess {
	
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
	
	public static String url = "http://localhost:2222/rest/annotate";
	public static String fileName = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\page_wise.txt";
	
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
					    
					    if(!paraidtoentities1.containsKey(paraid))
					    	paraidtoentities1.put(paraid, entities);
					    st.remove(paraid);
					}  	
					
				}
				
				long endTime1   = System.currentTimeMillis();
	        	long totalTime1 = endTime1 - startTime1;
	        	
	        	System.out.println("Time to build hashmap: "+ totalTime1 +". Now writing hashmap to disk");
				
	        	// Below code will save the hashmap created with the paraids and the count of entities
	        	// to disk in the folder mentioned in "fileName1" below. The filename in which it will saved
	        	// is called "curl".
	        	
	        	long startTime2 = System.currentTimeMillis();
				String fileName1 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Individual task\\Final\\curl";
				FileWriter fw1 = new FileWriter(fileName1);
				StringBuilder builder1 = new StringBuilder();
				String line1 = null;
				if(paraidtoentities.size()>0) {
					for(Map.Entry<String, Integer> e: paraidtoentities.entrySet()) {
						line1 = e.getKey() +" "+e.getValue();
						builder1.append(line1);
		                builder1.append(System.getProperty("line.separator"));
					}
				}
				fw1.write(builder1.toString());
				fw1.close();

				
				// Below code will save the hashmap created with the paraids and the list of entities
	        	// to disk in the folder mentioned in "fileName2" below. The filename in which it will saved
	        	// is called "paraidtoentity".
	   
				String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Individual task\\Final\\paraidtoentity";
				FileWriter fw2 = new FileWriter(fileName2);
				StringBuilder builder2 = new StringBuilder();
				String line2 = null;
				if(paraidtoentities1.size()>0) {
					for(Map.Entry<String, List<String>> e: paraidtoentities1.entrySet()) {
						line2 = e.getKey() +" "+e.getValue();
						builder2.append(line2);
		                builder2.append(System.getProperty("line.separator"));
					}
				}
				fw2.write(builder2.toString());
				fw2.close();
	        	
				
				long endTime2   = System.currentTimeMillis();
	        	long totalTime2 = endTime2 - startTime2;
				System.out.println("Time to save hashmap to disk: "+ totalTime2);
				
			}
		}
		
		// Below code will rearrange the input para_wise.txt to sort 
		// it by the paraids with maximum entities
    	
    	String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Individual task\\Final\\output.txt"; 
		FileWriter fw2 = new FileWriter(fileName2,true);
		StringBuilder builder2 = new StringBuilder();

    	for(Map.Entry<String, List<String>> e: cattoparaidlist.entrySet()) {		
    		List<String> ls = e.getValue();
    		Map<String, Integer> hm = new HashMap<>();
    		for(String s: ls) {
    			hm.put(s, paraidtoentities.get(s));
    		}
    		
    		ValueComparator bvc1 = new ValueComparator(hm);
            TreeMap<String, Integer> tm1 = new TreeMap<>(bvc1);
    		tm1.putAll(hm);
    		
    		String line2 = null;
    		int pos = 1;
    		if(tm1.size()>0) {
    			for(Map.Entry<String, Integer> e1: tm1.entrySet()) {
    				line2 = e.getKey() + e1.getKey();
    				String sl = paraidcattoline.get(line2);
    				String[] sln = sl.split("\\s+");
    				sln[3] = Integer.toString(pos);
    				StringBuilder b2 = new StringBuilder();
    				for(String s: sln) {
    					b2.append(s);
    					b2.append(" ");
    				}
    				builder2.append(b2.toString());
                    builder2.append(System.getProperty("line.separator"));
                    pos++;
                    if(pos>100)
                    	pos=1;
    			}
    		}	
		}
    	
    	fw2.write(builder2.toString());
		fw2.close();
		
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

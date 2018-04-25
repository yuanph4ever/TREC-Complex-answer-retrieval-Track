



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;



public class TagMe_Main {
	static int errored_paras = 0;
	static class ValueComparator 
				implements Comparator<String> 
	{
	    Map<String, Integer> base;

	    public ValueComparator(Map<String, Integer> base) 
	    {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(String a, String b) 
	    {
	        if (base.get(a) >= base.get(b)) 
	        {
	            return -1;
	        } else 
	        {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	static class ValueComparatorF 
				implements Comparator<String> 
	{
	    Map<String, Float> base;

	    public ValueComparatorF(Map<String, Float> base) 
	    {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with
	    // equals.
	    public int compare(String a, String b) 
	    {
	        if (base.get(a) >= base.get(b)) 
	        {
	            return -1;
	        } else 
	        {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	
	static class ValueComparatorD 
				implements Comparator<String> 
	{
		Map<String, Double> base;

		public ValueComparatorD(Map<String, Double> base) 
		{
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) 
		{
			if (base.get(a) >= base.get(b)) 
			{
				return -1;
			} 
			else 
			{
				return 1;
			} // returning 0 would merge keys
		}
	}
	
	static class ValueComparatorL
				implements Comparator<String> 
	{
		Map<String, Long> base;

		public ValueComparatorL(Map<String, Long> base) 
		{
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) 
		{
			if (base.get(a) >= base.get(b)) 
			{
				return -1;
			} 
			else 
			{
				return 1;
			} // returning 0 would merge keys
		}
	}

	

	public static String url = "https://tagme.d4science.org/tagme/tag";
	
	public static String fileName = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\runfile_baseline.txt";
	
	public static Map<String, Integer> paraidtoentities = new HashMap<>();
	public static Map<String, List<String>> paraidtoentities1 = new HashMap<>();
	public static Map<String, List<String>> cattoparaidlist = new HashMap<>();
	public static Map<String, String> paraidcattoline = new HashMap<>();
	public static Set<String> st = new TreeSet<>();

    public static void main(String[] args) throws IOException 
    {

    		long startTime = System.currentTimeMillis();
		
		// Below code will read para_wise.txt and put all categories and list of paraids
		// for each categories into a hashmap
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine();
		
		while(line != null) 
		{
			
			String[] linedetails = line.trim().split("\\s+");
			
			String x = linedetails[2];
			if(!st.contains(x)) 
			{
				st.add(x);
			}
			
			String y = linedetails[0];
			if(!cattoparaidlist.containsKey(y)) 
			{
				List<String> ls = new ArrayList<>();
				ls.add(x);
				cattoparaidlist.put(y, ls);
			}				
			else 
			{
				List<String> ls = cattoparaidlist.get(y);
				ls.add(x);
				cattoparaidlist.put(y, ls);
			}
			
			if(!paraidcattoline.containsKey(y+x)) 
			{
				paraidcattoline.put(y+x, line);
			}
		
			line = br.readLine();
		}
		
		br.close();
		System.out.println("Sucessfully created data structures");
		
		// Put the file dedup.articles-paragraphs.cbor ,16GB size, in the location mentioned in
		// folderLocation variable mentioned below. The below program will look for the files
		// in the location paragraphCorpus folder and process one file at a time.
		
		String folderLocation = "C:\\Users\\ddash\\Documents\\Spring2018\\dedup";
		File folder = new File(folderLocation);
		
		long startTime1 = System.currentTimeMillis();
		
		File[] lisOfFiles = folder.listFiles();
		for(File f: lisOfFiles) 
		{
			readFiles(f);
			if(f.isFile()) 
			{
				System.out.println("Now reading file - " + f);
				
				InputStream is = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(is);
				
				Iterator<Data.Paragraph> ip = DeserializeData.iterParagraphs(bis);
				
				while(ip.hasNext() && st.size()>0) 
				{
					String paraid = ip.next().getParaId();
					
					if(st.contains(paraid)) 
					{
						
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
				
	        		// Below code will save the hashmap created with the paraids and the count of entities
	        		// to disk in the folder mentioned in "fileName1" below. The filename in which it will saved
	        		// is called "curl".
	        	
	        		long startTime2 = System.currentTimeMillis();
				String fileName1 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\curl";
				FileWriter fw1 = new FileWriter(fileName1);
				StringBuilder builder1 = new StringBuilder();
				if(paraidtoentities.size()>0) 
				{
					for(Map.Entry<String, Integer> e: paraidtoentities.entrySet()) 
					{
						String key = e.getKey();
						int val = e.getValue();
						
						builder1.append(key +" "+val);
						builder1.append(System.getProperty("line.separator"));
					}
				}
				
				String toWriteinFile = builder1.toString();
				fw1.write(toWriteinFile);
				fw1.close();
				builder1.setLength(0);

				
				// Below code will save the hashmap created with the paraids and the list of entities
	        		// to disk in the folder mentioned in "fileName2" below. The filename in which it will saved
	        		// is called "paraidtoentity".
	   
				String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\paraidtoentity";
				FileWriter fw2 = new FileWriter(fileName2);
				StringBuilder builder2 = new StringBuilder();
				if(paraidtoentities1.size()>0) 
				{
					for(Map.Entry<String, List<String>> e: paraidtoentities1.entrySet()) 
					{
						String key = e.getKey();
						List<String> val = e.getValue();
						
						builder2.append(key +" "+val);
						builder2.append(System.getProperty("line.separator"));
					}
				}
				
				String toWriteinFile1 = builder1.toString();
				
				fw2.write(toWriteinFile1);
				fw2.close();
				builder2.setLength(0);
	        	
				
				long endTime2   = System.currentTimeMillis();
	        		long totalTime2 = endTime2 - startTime2;
				System.out.println("Time to save hashmap to disk: "+ totalTime2);
				
			}
		}
		
		// Below code will rearrange the input para_wise.txt to sort 
		// it by the paraids with maximum entities
    	
		String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\output1_TagMe.txt"; // Change this
    		String fileName4 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\output2_TagMe.txt";
    		String fileName6 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\output3_TagMe.txt"; // rank+BM25 score
		FileWriter fw2 = new FileWriter(fileName2,true);
		FileWriter fw4 = new FileWriter(fileName4,true);
		FileWriter fw6 = new FileWriter(fileName6,true);
		StringBuilder builder2 = new StringBuilder();
		StringBuilder builder4 = new StringBuilder();
		StringBuilder builder6 = new StringBuilder();
		try 
		{
			for(Map.Entry<String, List<String>> e: cattoparaidlist.entrySet()) 
			{
		    		List<String> ls = e.getValue();
		    		int i = e.getValue().size();
		    		Map<String, Integer> hm = new HashMap<>();
		    		for(String s: ls) 
		    		{
		    			int val = 0;
		    			if(paraidtoentities.get(s) != null)
		    			{
		    				val = paraidtoentities.get(s);
		    			}
		    			hm.put(s, val);
		    		}
		    		Map<String, Float> hm1 = new HashMap<>();
		    		
		    		Map<String, Double> hm3 = new HashMap<>();
		    		Map<String, Long> hm4 = new HashMap<>();
		    		
		    		ValueComparatorD bv1 = new ValueComparatorD(hm3);
		    		ValueComparatorL bv2 = new ValueComparatorL(hm4);
		    		
		    		ValueComparator bvc1 = new ValueComparator(hm);
		    		TreeMap<String, Integer> tm1 = new TreeMap<>(bvc1);
		    		tm1.putAll(hm);
	
		    		String line2 = null;
		    		int pos = 1;
		    		if(tm1.size()>0) 
		    		{
		    			StringBuilder b2 = new StringBuilder();
		    			for(Map.Entry<String, Integer> e1: tm1.entrySet()) 
		    			{
		    				line2 = e.getKey() + e1.getKey();
		    				String sl = paraidcattoline.get(line2);
		    				String[] sln = sl.split("\\s+");
		    				
		    				//String[] sln = paraidcattoline.get(e.getKey() + e1.getKey()).split("\\s+");
		    				
		    				sln[3] = Integer.toString(pos);
		    				
		    				for(String s: sln) 
		    				{
		    					b2.append(s);
		    					b2.append(" ");
		    				}
		    				
		    				
		    				float f = (float) (i+1-pos)/i;
		    				float f1 = Float.parseFloat(sln[4]) + f;
	
		    				hm1.put(e1.getKey(), f1);
		    				
		    				String toWrite = b2.toString();
		    				builder2.append(toWrite);
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
		    		String line4 = null;
		    		int pos1 = 1;
		    		
		    		if(tm2.size()>0) 
		    		{
		    			StringBuilder b2 = new StringBuilder();
	    	    			StringBuilder b6 = new StringBuilder();
		    			for(Map.Entry<String, Float> e1: tm2.entrySet()) 
		    			{
		    				line4 = e.getKey() + e1.getKey();
		    				String sl = paraidcattoline.get(line4);
		    				String[] sln = sl.split("\\s+");
	
		    				//String[] sln = paraidcattoline.get(e.getKey() + e1.getKey()).split("\\s+");
		    				
		    				sln[3] = Integer.toString(pos1);
		    				for(String s: sln) 
		    				{
		    					b2.append(s);
		    					b2.append(" ");
		    				}
		    				
		    				String[] sln1 = sln;
		    				sln1[4] =  Float.toString(pos1 + Float.parseFloat(sln[4]));
		    				for(String s: sln1) 
		    				{
		    					b6.append(s);
		    					b6.append(" ");
		    				}
		    				
		    				String toWrite6 = b6.toString();
		    				builder6.append(toWrite6);
		    				builder6.append(System.getProperty("line.separator"));
		    				
		    				String toWrite4 = b2.toString();
		    				builder4.append(toWrite4);
		    				builder4.append(System.getProperty("line.separator"));
		                    
		    				b2.setLength(0);
		    				b6.setLength(0);
		                    
		    				pos1++;
		    			}
		    		}
		    		
		    		fw6.write(builder6.toString());
		    		fw4.write(builder4.toString());
		    		
		    		builder6.setLength(0);
		    		builder4.setLength(0);
	    		
	    		
			}
			
			fw2.close();
			fw4.close();
			fw6.close();
		}
		catch(Exception e) 
		{
    			System.out.println(e);
    		}
    	
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
    	
		System.out.println("Total time taken - " + totalTime);
    	
    		System.out.println("Errored Paras: " + errored_paras);
    		
    }
    
    private static ArrayList<String> getAnchors(String data)
    {
        ArrayList<String> entities = new ArrayList<>();
        
        try 
        {
        	
	        	Document doc = Jsoup.connect(url)
	    	        	.data("lang", "en")
	    	        	.data("gcube-token", "3d193770-ce77-41a7-a433-91f8074d6d28-843339462")
	    	        	.data("text", data)
	    	        	.ignoreContentType(true)
	    	        	.get();
	        	
	        	StringBuilder out = new StringBuilder(doc.text());
	        	int j = 0;
	        	int index = out.indexOf("\"title\":\"", j);
	        	while(index != -1) 
	        	{
	        		int end_index = out.indexOf(",",index);
	        		entities.add(out.substring(index+9, end_index-1));
	        		j = end_index;
	        		index = out.indexOf("\"title\":\"", j);
	        	}
	        	out.setLength(0);
	        	out=null;
        	
        }
        catch(Exception e) 
        {
        	errored_paras++;
        }
        return entities;
    }
    
    public static void readFiles(File f) throws IOException {
    		if(f.isFile()) 
		{
    			long startTime1 = System.currentTimeMillis();
			System.out.println("Now reading file - " + f);
			
			InputStream is = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(is);
			
			Iterator<Data.Paragraph> ip = DeserializeData.iterParagraphs(bis);
			
			while(ip.hasNext() && st.size()>0) 
			{
				String paraid = ip.next().getParaId();
				
				if(st.contains(paraid)) 
				{
					
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
			
        		// Below code will save the hashmap created with the paraids and the count of entities
        		// to disk in the folder mentioned in "fileName1" below. The filename in which it will saved
        		// is called "curl".
        	
        		long startTime2 = System.currentTimeMillis();
			String fileName1 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\curl_tagme_para";
			FileWriter fw1 = new FileWriter(fileName1);
			StringBuilder builder1 = new StringBuilder();
			if(paraidtoentities.size()>0) 
			{
				for(Map.Entry<String, Integer> e: paraidtoentities.entrySet()) 
				{
					String key = e.getKey();
					int val = e.getValue();
					
					builder1.append(key +" "+val);
					builder1.append(System.getProperty("line.separator"));
				}
			}
			
			String toWriteinFile = builder1.toString();
			fw1.write(toWriteinFile);
			fw1.close();
			builder1.setLength(0);

			
			// Below code will save the hashmap created with the paraids and the list of entities
        		// to disk in the folder mentioned in "fileName2" below. The filename in which it will saved
        		// is called "paraidtoentity".
   
			String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Weight\\paraentities_tagme_para";
			FileWriter fw2 = new FileWriter(fileName2);
			StringBuilder builder2 = new StringBuilder();
			if(paraidtoentities1.size()>0) 
			{
				for(Map.Entry<String, List<String>> e: paraidtoentities1.entrySet()) 
				{
					String key = e.getKey();
					List<String> val = e.getValue();
					
					builder2.append(key +" "+val);
					builder2.append(System.getProperty("line.separator"));
				}
			}
			
			String toWriteinFile1 = builder1.toString();
			
			fw2.write(toWriteinFile1);
			fw2.close();
			builder2.setLength(0);
        	
			
			long endTime2   = System.currentTimeMillis();
        		long totalTime2 = endTime2 - startTime2;
			System.out.println("Time to save hashmap to disk: "+ totalTime2);
			
		}
    }

 }



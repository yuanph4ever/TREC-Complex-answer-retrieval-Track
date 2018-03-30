package edu.unh.cs980.prototype2_Merge;

//import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Index {
	
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
	
	public static HashMap<String, Integer> hm = new HashMap<>();
	public static HashMap<String, Set<String>> cat = new HashMap<>();
	public static HashMap<String, Integer> paraID2types = new HashMap<>();
	
	public static void main(String[] args) throws IOException {
		
		String folderLocation = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Curloutput";
		File folder = new File(folderLocation);
		
		File[] lisOfFiles = folder.listFiles();
		for(File f: lisOfFiles) {
			if(f.isFile()) {
				readFile(f);
			}
		}
		
		// Below code will create a file name type.txt that will have sorted highest
		// to lowest types and their appearance in the paraID file 
		ValueComparator bvc = new ValueComparator(hm);
        TreeMap<String, Integer> tm = new TreeMap<>(bvc);
		tm.putAll(hm);
		String fileName = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Team_Output\\types.txt";
		FileWriter fw = new FileWriter(fileName);
		StringBuilder builder = new StringBuilder();
		String line = null;
		if(tm.size()>0) {
			for(Map.Entry<String, Integer> e: tm.entrySet()) {
				line = e.getKey() +"     "+e.getValue();
				builder.append(line);
                builder.append(System.getProperty("line.separator"));
			}
		}
		fw.write(builder.toString());
		fw.close();
		
		
		// Below code will list each type and all the entities that are linked with it.
		String fileName1 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Team_Output\\typeentity.txt";
		FileWriter fw1 = new FileWriter(fileName1);
		StringBuilder builder1 = new StringBuilder();
		String line1 = null;
		if(cat.size()>0) {
			for(Map.Entry<String, Set<String>> e: cat.entrySet()) {
				line1 = e.getKey() +"     "+e.getValue();
				builder1.append(line1);
                builder1.append(System.getProperty("line.separator"));
			}
		}
		fw1.write(builder1.toString());
		fw1.close();
		
		// Below code will list the types, the number of times the types appeared in paraID file and the list of entities
		// that are linked with the type.
		ValueComparator bvc1 = new ValueComparator(hm);
        TreeMap<String, Integer> tm1 = new TreeMap<>(bvc1);
		tm1.putAll(hm);
		String fileName2 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Team_Output\\typecountentities.txt";
		FileWriter fw2 = new FileWriter(fileName2);
		StringBuilder builder2 = new StringBuilder();
		String line2 = null;
		if(tm1.size()>0) {
			for(Map.Entry<String, Integer> e: tm1.entrySet()) {
				Set<String> ls = cat.get(e.getKey());
				line2 = e.getKey() +"     "+e.getValue()+ "    "+ls;
				builder2.append(line2);
                builder2.append(System.getProperty("line.separator"));
			}
		}
		fw2.write(builder2.toString());
		fw2.close();
		
		// Below code will create a file that contains the paraIDs that are present in paraID file and the number of 
		// types that are present in the paragraph represented by the paraID
		
		String fileName3 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\paraID";
		BufferedReader br3 = new BufferedReader(new FileReader(fileName3));
		String line3 = br3.readLine();
		while(line3 != null) {
			String paraID = line3.substring(0,45);
			if(line3.substring(45) != null) {
				String[] elist = line3.substring(45).split(",");
				paraID2types.put(paraID, elist.length);
			}
			else
				paraID2types.put(paraID, 0);
			
			line3 = br3.readLine();
		}
		ValueComparator bvc2 = new ValueComparator(paraID2types);
		TreeMap<String, Integer> tm2 = new TreeMap<>(bvc2);
		tm2.putAll(paraID2types);
		
		String fileName4 = "C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Team_Output\\paraIDtotypes";
		FileWriter fw3 = new FileWriter(fileName4);
		StringBuilder builder3 = new StringBuilder();
		String line4 = null;
		if(tm2.size()>0) {
			for(Map.Entry<String, Integer> e: tm2.entrySet()) {
				line4 = e.getKey() +"     "+e.getValue();
				builder3.append(line4);
                builder3.append(System.getProperty("line.separator"));
			}
		}
		fw3.write(builder3.toString());
		fw3.close(); 

	}
	
	public static void readFile(File fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

        try {
            System.out.println("Now reading file ["+ fileName + "]");
            
            String line = br.readLine();         
            while (line != null) {
            	int i = line.indexOf("types=");
            	int j = line.indexOf("\" surfaceForm=");
            	int k = line.indexOf("\" offset=");
            	if(i>0 && j>0 && k>0) {
            		//System.out.println(line.substring(i+7, j));
            		String[] types = line.substring(i+7, j).split(",");
            		String entity = line.substring(j+15, k);
            		indexAncCount(types, entity);
            	}
            	
                line = br.readLine();
            }
            
        } finally {
            br.close();
        }
	}
	
	public static void indexAncCount(String[] types, String e) {
		for(String s: types) {
			if(s.length()>1) {
				/*
				if(hm.containsKey(s)) {
					int val = hm.get(s);
					hm.put(s, val+1);
				}
				else
					hm.put(s, 1);
				*/
				if(cat.containsKey(s)) {
					Set<String> ls = cat.get(s);
					if(!ls.contains(e)) {
						ls.add(e);
						cat.put(s, ls);
						int val = hm.get(s);
						hm.put(s, val+1);
					}
				}
				else {
					Set<String> lst = new HashSet<>();
					lst.add(e);
					cat.put(s, lst);
					hm.put(s, 1);
				}
			}
		}
	}

}

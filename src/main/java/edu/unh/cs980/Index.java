package edu.unh.cs980;

//import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Index {
	
	public static HashMap<String, Integer> hm = new HashMap<>();
	
	public static void main(String[] args) throws IOException {
		String folderLocation = "C:\\Users\\ddash\\Documents\\Spring2018\\CurlToken";
		File folder = new File(folderLocation);
		
		File[] lisOfFiles = folder.listFiles();
		for(File f: lisOfFiles) {
			if(f.isFile()) {
				readFile(f);
			}
		}
	
		String fileName = "C:\\Users\\ddash\\Documents\\Spring2018\\CurlToken\\hashout.txt";
		FileWriter fw = new FileWriter(fileName);
		
		StringBuilder builder = new StringBuilder();
		String line = null;
		if(hm.size()>0) {
			for(Map.Entry<String, Integer> e: hm.entrySet()) {
				line = e.getKey() +"     "+e.getValue();
				builder.append(line);
                builder.append(System.getProperty("line.separator"));
			}
		}
		fw.write(builder.toString());
		fw.close();

	}
	
	public static void readFile(File fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));

        try {
            System.out.println("Now reading file ["+ fileName + "]");
            
            String line = br.readLine();         
            while (line != null) {
            	int i = line.indexOf("types=");
            	int j = line.indexOf("\" surfaceForm=");
            	if(i>0 && j>0) {
            		//System.out.println(line.substring(i+7, j));
            		String[] types = line.substring(i+7, j).split(",");
            		indexAncCount(types);
            	}
            	
                line = br.readLine();
            }
            
        } finally {
            br.close();
        }
	}
	
	public static void indexAncCount(String[] types) {
		for(String s: types) {
			if(s.length()>1) {
				if(hm.containsKey(s)) {
					int val = hm.get(s);
					hm.put(s, val+1);
				}
				else
					hm.put(s, 1);
			}
		}
	}

}

package edu.unh.cs980.prototype2_Merge;




import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TagMe_Entities {
	
	
	public static String url = "https://tagme.d4science.org/tagme/tag";
	
	public static void main(String[] args) throws IOException {
		String data = "Obama visted UK"; // put whatever data you want
		ArrayList<String> ls = getAnchors(data);
		// Display entities on the console
		for(String s: ls)
			System.out.print(s+" ");
		
		// Write the entities to the file
		String file = "C:\\Users\\ddash\\Documents\\Spring2018\\Tagme_Output\\tagme.txt"; // Change this file name
		FileWriter fw = new FileWriter(file);
		StringBuilder sb = new StringBuilder();
		for(String s: ls) {
			sb.append(s);
			sb.append(" ");
		}
		fw.write(sb.toString());
		fw.close();
	}
	
	private static ArrayList<String> getAnchors(String data){
        ArrayList<String> entities = new ArrayList<>();
        
        try {
        	
        	Document doc = Jsoup.connect(url)
    	        	.data("lang", "en")
    	        	.data("gcube-token", "3d193770-ce77-41a7-a433-91f8074d6d28-843339462")
    	        	.data("text", data)
    	        	.ignoreContentType(true)
    	        	.get();

    	        if(doc.text() != null) {
    	        	JSONObject json = new JSONObject(doc.text());
    	            if(json.has("annotations")) {
    	            	JSONArray entityList = json.getJSONArray("annotations");
    	    	        int i = entityList.length()-1;
    	    	        JSONObject obj;
    	    	        while(i>=0) {
    	    	        	obj = entityList.getJSONObject(i);
    	    	        	//System.out.println(obj.getString("title"));
    	    	        	if(obj.has("title"))
    	    	        		entities.add(obj.getString("title"));
    	    	        	i--;
    	    	        }
    	            }
    	        }
        }catch(Exception e) {
        	
        }
       
        	
        return entities;
    }

}

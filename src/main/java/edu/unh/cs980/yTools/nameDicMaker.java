<<<<<<< HEAD:src/main/java/edu/unh/cs980/yTools/nameDicMaker.java
package edu.unh.cs980.yTools;
=======
package edu.unh.cs980.kmeans;
>>>>>>> c03fd4c75cfa5ac4102c114f3863268116f71827:src/main/java/edu/unh/cs980/kmeans/nameDicMaker.java

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.Data.Page.SectionPathParagraphs;
import edu.unh.cs.treccar_v2.Data.ParaBody;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class nameDicMaker {
	
	private static void usage() {
        System.out.println("Command line parameters: input_cbor output_nameDic_file");
        System.exit(-1);
    }
	
	public static void main(String[] args) throws Exception{
		System.setProperty("file.encoding", "UTF-8");
		System.out.println("Parsing...\nCreating Name Dictionary...\n");
		final String pagesFile = args[0];
        final FileInputStream fileInputStream = new FileInputStream(new File(pagesFile));
        Map<String, List<String>> nameDic = new HashMap<String, List<String>>();
        int count = 0;
        for(Data.Page page: DeserializeData.iterableAnnotations(fileInputStream)) {
        		count ++;
            if(count > 1000) {
            		break;
            }
            //System.out.println(page);
            //System.out.println(page.getPageMetadata());
        		System.out.println(".");
            for (SectionPathParagraphs sectionPathPara : page.flatSectionPathsParagraphs()) {
        	    		List<ParaBody> para_bodies = sectionPathPara.getParagraph().getBodies();
        	    		//System.out.println(para_bodies);
        	    		//System.out.println("---");
        	    		for(int i = 0; i < para_bodies.size(); i ++) {
        	    			ParaBody para_body = para_bodies.get(i);
        	    			if(para_body instanceof Data.ParaLink) {
            	    			//System.out.println(para_body);
        	    				//System.out.println(((Data.ParaLink) para_body).getAnchorText() + "--->" + ((Data.ParaLink) para_body).getPage());
        	    				String entity = ((Data.ParaLink) para_body).getPage();
        	    				String anchor = ((Data.ParaLink) para_body).getAnchorText();
        	    				if(nameDic.containsKey(entity)) {
        	    					List<String> en_list = nameDic.get(entity);
        	    					if(en_list.contains(anchor) == false) {
        	    						en_list.add(anchor);
        	    						nameDic.replace(entity, en_list);
        	    					}	
        	    				}else {
        	    					List<String> new_en_list = new ArrayList<String>();
        	    					new_en_list.add(anchor);
        	    					nameDic.put(entity, new_en_list);
        	    				}
            	    		}
        	    		}
        	    		
            }
            
        }
        
        //System.out.println(nameDic);
        
        System.out.println("Name Dictionary created\nNow creating output file...");
        String outputPath = args[1];
        File runfile = new File(outputPath + "/name_dictionary_hyperlinks");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);
		
		/*
		for (Map.Entry<String,List<String>> entry : nameDic.entrySet()) {
			//System.out.println(entry.getKey() + " ---> " + entry.getValue());
			writer.write(entry.getKey() + " ---> " );
			List<String> en_list = entry.getValue();
			for(int i = 0; i < en_list.size(); i ++) {
				writer.write(en_list.get(i) + " | ");
			}
			writer.write("\n");
		}
		*/
		
		while(nameDic.isEmpty() == false) {
			System.out.println(".");
			String key = "null";
			List<String> value = new ArrayList<String>();
			int last_len = 0;
			for (Map.Entry<String,List<String>> entry : nameDic.entrySet()) {
				
				String temp_key = entry.getKey();
				List<String> temp_value = entry.getValue();
				int temp_len = temp_value.size();
				//System.out.println(temp_len);
				if(temp_len > last_len) {
					key = temp_key;
					value = temp_value;
					last_len = temp_len;
					//System.out.println(key);
				}
				
			}
			//System.out.println(key);
			writer.write(key + " ---> ");
			for(int i = 0; i < value.size(); i ++) {
				writer.write(value.get(i) + " | ");
			}
			writer.write("\n");
			nameDic.remove(key);
		}
		
		System.out.println("Output created: " + outputPath + "/name_dictionary_hyperlinks");
        System.out.println("Parser DONE\n");
	}

}

package edu.unh.cs980.yTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.text.html.HTMLDocument.Iterator;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.Data.ParaBody;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class graphMaker {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		final String pagesFile = args[0];
        final FileInputStream fileInputStream = new FileInputStream(new File(pagesFile));
        
        String outputPath = args[1];    
        File graphFile = new File(outputPath + "/wiki_graph");
		graphFile.createNewFile();
		FileWriter writer = new FileWriter(graphFile);
        
		int num_of_pages = 0;
		
        for(Data.Page page: DeserializeData.iterableAnnotations(fileInputStream)) {
        	    num_of_pages += 1;
            //System.out.println(page);
            //System.out.println(page.getPageId());
            //System.out.println("---");
        	    writer.write(page.getPageId() + " ");
            int num_of_links = page.getPageMetadata().getInlinkIds().size();
            //System.out.println(num_of_links);
            
            
            for(int i = 0; i < num_of_links; i ++) {
            		//System.out.println(page.getPageMetadata().getInlinkIds().get(i));
            		writer.write(page.getPageMetadata().getInlinkIds().get(i) + " ");
            	
            }
                       
            
            System.out.println(num_of_links + "\n");
            
            writer.write("\n");
            
        }
        
        System.out.println("\n" + num_of_pages);
        
        writer.flush();
		writer.close();

	}

}



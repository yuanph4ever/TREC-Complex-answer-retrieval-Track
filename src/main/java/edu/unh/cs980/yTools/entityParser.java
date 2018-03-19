package edu.unh.cs980.peihao;

import java.util.List;
import java.util.Iterator;

import java.io.*;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

public class entityParser {
	
	private static void usage() {
        System.out.println("Command line parameters:input_para_cbor output_ground_truth");
        System.exit(-1);
    }
	
	public static void main(String[] args) throws IOException{
		
		if(args.length < 2) {
			usage();
		}
		
		System.setProperty("file.encoding", "UTF-8");
		
		final String paragraphsFile = args[0];
		final String ground_truth_path = args[1];
		
		File runfile = new File(ground_truth_path + "/entities_ground_truth");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);
		
		final FileInputStream fileInputStream = new FileInputStream(new File(paragraphsFile));
		List<String> entities_list;
		
		int count = 0;
		for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream)) {
			count ++;
			//System.out.println("para text: ");
			//System.out.println(p.getTextOnly());
			entities_list = p.getEntitiesOnly();
			//System.out.println("entities: ");
			//System.out.println(p.getEntitiesOnly());
			writer.write(p.getParaId().toString() + " ---> ");
			Iterator<String> iterator = entities_list.iterator();
			while (iterator.hasNext()) {
				//System.out.println(iterator.next());
				writer.write(iterator.next().toString() + " | ");
			}
			writer.write("\n");
		    System.out.println("---" + count + "---");
		}
		
		writer.flush();
		writer.close();
		fileInputStream.close();
	    

	}	
}

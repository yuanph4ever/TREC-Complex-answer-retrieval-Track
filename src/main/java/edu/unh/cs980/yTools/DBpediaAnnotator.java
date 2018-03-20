package edu.unh.cs980.yTools;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

import java.util.regex.Matcher;

import net.didion.jwnl.data.Word;

public class DBpediaAnnotator {
	
	private static void usage() {
        System.out.println("Command line parameters:input_para_cbor output_entities_from_tool_path");
        System.exit(-1);
    }
	
	static String spotlightAPIurl = "http://model.dbpedia-spotlight.org/en/annotate?";
	
	public static void main(String[] args) throws IOException {
		
		if(args.length < 2) {
			usage();
		}
		
		/* test
		String input = "Obama was born in 1961 in Honolulu, Hawaii, two years after the territory was admitted to the Union as the 50th state.";
		String httpUrl = spotlightAPIurl + "text=" + input.replace(" ", "%20");
		String responseStr = getHttpResponse(httpUrl);
		System.out.println(responseStr);
		Pattern pattern = Pattern.compile("http://dbpedia.org/resource/(.*?)\",\"@support");
		Matcher matcher = pattern.matcher(responseStr);
		while (matcher.find()) {	
		    System.out.println(matcher.group(1));
		    //System.out.println(matcher.group(1).replaceAll("[^A-Za-z0-9]", ""));
			
		}
		*/
		
		System.setProperty("file.encoding", "UTF-8");
		
		final String paragraphsFile = args[0];
		final String entities_path = args[1];
		
		File runfile = new File(entities_path + "/entities_from_DBpedia_new");
		runfile.createNewFile();
		FileWriter writer = new FileWriter(runfile);
		
		final FileInputStream fileInputStream = new FileInputStream(new File(paragraphsFile));
			
		int count = 0;
		for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream)) {
			count ++;
			//System.out.println("para text: ");
			//System.out.println(p.getTextOnly());
			//entities_list = p.getEntitiesOnly();
			//System.out.println("entities: ");
			//System.out.println(p.getEntitiesOnly());
			writer.write(p.getParaId().toString() + " ---> ");
			
			String text = p.getTextOnly();
			String httpUrl = spotlightAPIurl + "text=" + text.replace(" ", "%20");
			String responseStr = getHttpResponse(httpUrl);
			Pattern pattern = Pattern.compile("http://dbpedia.org/resource/(.*?)\",\"@support");
			Matcher matcher = pattern.matcher(responseStr);
			while (matcher.find()) {	
			    //System.out.println(matcher.group(1));
			    //System.out.println(matcher.group(1).replaceAll("[^A-Za-z0-9]", ""));
				//writer.write(matcher.group(1).replaceAll("[^A-Za-z0-9]", "") + " | ");
				writer.write(matcher.group(1) + " | ");
			}
			
			writer.write("\n");
			
		    System.out.println("---" + count + "---");
		}
		
		writer.flush();
		writer.close();
		fileInputStream.close();

	}	

	//Author: Kaixin Zhang
	private static String getHttpResponse(String urlStr) {
		try {

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			conn.setRequestProperty("Accept", "application/json");
			// conn.setReadTimeout(httpRequest_timeout);
			if (conn.getResponseCode() != 200) {
				System.out.println("Failed to connect to " + urlStr + " with HTTP error code: " + conn.getResponseCode());
				if (conn.getResponseCode() == 401) {
				}
				return null;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output = "";// br.readLine();
			String line = "";
			while ((line = br.readLine()) != null) {
				output += line;
			}

			conn.disconnect();
			return output;
		} catch (Exception e) {
			return null;
		}
	}


}

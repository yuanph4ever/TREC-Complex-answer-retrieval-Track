package edu.unh.cs980.yTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getDBEntities {
	
	public static void main(String[] args) throws IOException{

		String str = "Kobe Bryant is my favorite basketball player.";
		
		String entities = getDBEntities(str);
		
		System.out.println(entities);
	}
	
	public static String getDBEntities(String str) {
		String spotlightAPIurl = "http://model.dbpedia-spotlight.org/en/annotate?";
		String httpUrl = spotlightAPIurl + "text=" + str.replaceAll("[^A-Za-z0-9]", "%20");
		String responseStr = getHttpResponse(httpUrl);
		Pattern pattern = Pattern.compile("http://dbpedia.org/resource/(.*?)\",\"@support");
		Matcher matcher = pattern.matcher(responseStr);
		String newStr = "";
		while (matcher.find()) {	
			//System.out.println(matcher.group(1));
			//System.out.println(matcher.group(1).replaceAll("[^A-Za-z0-9]", ""));
			newStr += matcher.group(1) + " ";
		}   
		return newStr.replaceAll("_", " ");
		
	}
	
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

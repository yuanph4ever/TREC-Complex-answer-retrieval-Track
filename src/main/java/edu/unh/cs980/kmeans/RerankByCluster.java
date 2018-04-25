package edu.unh.cs980.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class RerankByCluster {
	
	public RerankByCluster(String input_run, String outline_cbor, String corpus_index, String clu_index, String output_path) throws IOException {
		File file = new File(input_run);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		String last_query_id = " ";
		String temp_query_id = " ";
		String temp_para_id = " ";
		int count = 0;
		while ((line = bufferedReader.readLine()) != null) {
			String[] elements = line.split(" ");
			temp_query_id = elements[0];
			if(count < 100) {
				
			}
			
			//System.out.println(elements[2]);
			count ++;
		}
		fileReader.close();
		
	}
	
	public static void main(String[] args) throws Exception{
		
		String input_run = args[0];
		String outline_cbor = args[1];
		String corpus_index = args[2];
		String clu_index = args[3];
		String output_path = args[4];
		
		RerankByCluster rbc = new RerankByCluster(input_run, outline_cbor, corpus_index, clu_index, output_path);
	}

}

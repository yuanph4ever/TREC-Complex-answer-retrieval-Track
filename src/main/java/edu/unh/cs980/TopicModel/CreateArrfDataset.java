package edu.unh.cs980.TopicModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateArrfDataset {

	public CreateArrfDataset(String data, String path) throws IOException
	{
		File f = new File(path + ".arff");
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data.toString());
		bw.close();
		System.out.println("check for arff file");
	}
}

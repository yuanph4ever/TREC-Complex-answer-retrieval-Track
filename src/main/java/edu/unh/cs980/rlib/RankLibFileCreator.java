package edu.unh.cs980.rlib;

import java.util.ArrayList;

public class RankLibFileCreator {
	
	public RankLibFileCreator()
	{
		
	}

	private boolean isRelevant(ArrayList<ArrayList<String>> gtClusters, String q, String p){
		for(ArrayList<String> cluster:gtClusters){
			if(cluster.contains(q)){
				if(cluster.contains(p))
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	
	public void writeRankLibOutput()
	{
		
	}
}

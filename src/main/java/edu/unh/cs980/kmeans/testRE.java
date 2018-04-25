package edu.unh.cs980.kmeans;

public class testRE {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "A%20%20%20B%20%20%20C";
		String new_str = str.replaceAll("%20", " ");
		String terms[] = new_str.split(" ");
		System.out.println(terms.length);
		for(int i = 0; i < terms.length; i ++) {
			System.out.println(terms[i]);
		}
		
	}

}

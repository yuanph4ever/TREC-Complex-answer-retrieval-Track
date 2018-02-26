package edu.unh.cs980;

public class test {
	
	public static void main(String[] args) throws Exception {
		
		String str = "";
	    int i = 0 ;
	    while(i < 3) {
	    	   if(i == 0) {
	    		   str = "a";
	    	   }else if(i == 1) {
	    		   str = "b";
	    		   
	    	   }else {
	    		   str = "c";
	    	   }
	    	   i ++;
	    }
	    System.out.println(str);
	}
    
}

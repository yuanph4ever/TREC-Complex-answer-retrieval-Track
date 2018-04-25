package prototype2_Merge;


import java.io.*;

public class ReduceTop100
{
   String first;
   String last;
   String date;

  public ReduceTop100(String first, String last, String date){
       this.first = first;
       this.last = last;
       this.date = date;
  }
  
  public static void main(String args[]){
   try{
	   int count = 0;
	   FileInputStream fstream = new FileInputStream("C:\\Users\\Debajyoti\\Downloads\\lucene1_paragraph_section_train"); 
	   DataInputStream in = new DataInputStream(fstream);
	   BufferedReader br = new BufferedReader(new InputStreamReader(in));
	   String strLine;
	   StringBuilder sb = new StringBuilder();
	   System.out.println("Started");
	   String file = "C:\\Users\\Debajyoti\\Downloads\\new_file_train_100.txt"; 
	   FileWriter fw = new FileWriter(file);
	   while ((strLine = br.readLine()) != null)   {
		   String[] tokens = strLine.split(" ");
		   Reduce record = new Reduce(tokens[0],tokens[1],tokens[2]);
		   String temp = tokens[3];   
		   String str = temp;
		
		   int i = Integer.parseInt(str);

		   Integer bigInt = new Integer(str);
		   //Change bigInt to 100
		   if (bigInt < 101) {
			    String result = tokens[0] + " Q0 " + tokens[2] + " " + tokens[3] + " " + tokens[4] + " "+ "Lucene-BM25";
			    sb.append(result);
			    sb.append(System.getProperty("line.separator"));
			    //System.out.println(result);
			    count++;
			   
		   }
	   }
	   in.close();
	   fw.write(sb.toString());
	   fw.close();
	   System.out.println("Completed "+ count );
   }catch (Exception e){
     System.err.println("Error: " + e.getMessage());
   }
 }
}
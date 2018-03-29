package prototype2_Merge;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SplitFile{

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		//RandomAccessFile raf = new RandomAccessFile("C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\curloutput_2.txt", "r");
		RandomAccessFile raf = new RandomAccessFile("dedup.articles-paragraphs.cbor", "r");
        long numSplits = 500; //from user input, extract it from args
        long sourceSize = raf.length();
        long bytesPerSplit = sourceSize/numSplits ;
        long remainingBytes = sourceSize % numSplits;

        int maxReadBufferSize = 8 * 1024; //8KB
        for(int destIx=1; destIx <= numSplits; destIx++) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("C:\\Users\\ddash\\Documents\\Spring2018\\MergerCode\\Individual task\\Final\\dedup_split\\d"+destIx+".txt"));
            if(bytesPerSplit > maxReadBufferSize) {
                long numReads = bytesPerSplit/maxReadBufferSize;
                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                for(int i=0; i<numReads; i++) {
                    readWrite(raf, bw, maxReadBufferSize);
                }
                if(numRemainingRead > 0) {
                    readWrite(raf, bw, numRemainingRead);
                }
            }else {
                readWrite(raf, bw, bytesPerSplit);
            }
            bw.close();
        }
        if(remainingBytes > 0) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("split."+(numSplits+1)));
            readWrite(raf, bw, remainingBytes);
            bw.close();
        }
            raf.close(); 
        
            long endTime   = System.currentTimeMillis();
        	long totalTime = endTime - startTime;
        	System.out.println(totalTime);
        	System.out.println("File read successfull");
        
	}
	
	public static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
            bw.write(buf);
        }
    }
	
	
}
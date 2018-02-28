# TREC-Complex-answer-retrieval-Track

Files 
Indexer is up and running in the server.

It is in the location /home/ns1077/ParagraphIndex

# Installation Instructions

A precompiled jar file can be found in target/ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Here's the command line to run it. 

java -jar ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar flag Lucene_INDEX Output_Dir *Outline_CBOR *Clusters_info

flag is either "-hw" or "-v"

1. For "-hw":

   It will give you three runfiles for different heading weight. Use it by the following command line.
     
     java -jar ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar flag Lucene_INDEX Output_Dir *Outline_CBOR
     
2. For "-v":

  It will give you a file which stores information about paragraph vectors for clustring use. Use it by the following command line.
  
   java -jar ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar flag Lucene_INDEX Output_Dir
   
  Note that here you cannot use the original index files. You need to use the index files which have the vector information. You can find a sample index file on server /home/py1004/index_file_v
 
The original index file on server is here /home/ns1077/ParagraphIndex. There is also a precompiled jar file on server in /home/py1004. Feel free to test it. 

  


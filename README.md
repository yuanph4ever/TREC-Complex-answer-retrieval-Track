# TREC-Complex-answer-retrieval-Track

# Installation Instruction

# a. Working on your laptop:

1.Clone this repository

2.A precompiled jar file can be found in ./target/ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar. Run the program as

java -jar ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar Outline_Cbor Lucene_INDEX Output_Dir kmeans_clu_index_Dir types_clu_index_Dir

Outline_Cbor indicates the outline files like "train.pages.cbor-outlines.cbor". Lucene_INDEX indicates the index file for corpus. Output_Dir indicates the directory you want to store the runfiles. kmeans_clu_index_Dir is the directory which stores the index file for clusters of kmeans. types_clu_index_Dir is the directory which stores the index file for clusters of types.

You can download the index file for kmeans and types from the server. Address is "/home/py1004/project/Index_kmeans_cluster" and "/home/py1004/project/Index_DBpedia_Entities".

3.Or you can compile the project by using maven. A pom file is generated in the project for that. Please locate to the project and then do

mvn package

then a executable program called "ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar" can be found in "./target". Run the program using the same command line as showing above

# b. Working on server

A executable program called "ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar" is stored in "/home/py1004/project"

1.Locate to the directory.

2.Run this command line

java -jar ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar /home/ns1077/benchmarkY1/benchmarkY1-train/train.pages.cbor-outlines.cbor /home/ns1077/ParagraphIndex_ALL /home/py1004/project/runfiles_output /home/py1004/project/Index_kmeans_cluster /home/py1004/project/Index_DBpedia_Entities

All files you need are set up on the server. You just need to change the argument which is "/home/py1004/project/runfiles_output" to one that you want to use to store the output runfiles.

# Methods Description

# Pseudo Relevance Feedback with Entities

# Re-Rank by K-means Clustering

# Re-rank by Category Clustering

Part 1:

The indexing for this method involved sequential execution of these four methods:

1)	EntityLinking.java
2)	CurlProcess.java
3)	Split.java
4)	Index.java

The output file location : ****Peihao, Please provide path of the output file**** 

# Re-rank by DBpedia Type

Program: Readnprocess.java

Input: Run file with BM25 similarity

Location: : ******provide location for the baseline page test set (top 20)***********

Pre requisite:  1) The Dbpedia spotlight server is listening
	           2) pom file
                       3) dedup.articles-paragraphs.cbor
	           Location : ******provide location for the dedup***********


Execution procedure:
1)	Start the DBpedia server
2)	Execute the file by changing the path for input, output and dedup.articles-paragraphs.cbor

The runtime of this run file is around 10 minutes. I have provided the run file of this method for test.pages.cbor-outlines.cbor. 

Location: ******provide location for the runfile(Rerank_by_type.txt)  that I sent you***********


# Re-Rank by DBpedia Type and BM25 similarity with weight

Program: Weight.java

Input: Run file with BM25 similarity

Location: : ******provide location for the baseline page test set (top 20)***********

Pre requisite:  1) The Dbpedia spotlight server is listening
	           2) pom file
                       3) dedup.articles-paragraphs.cbor
	           Location : ******provide location for the dedup***********


Execution procedure:
1)	Start the DBpedia server
2)	Execute the file by changing the path for input, output and dedup.articles-paragraphs.cbor

The runtime of this run file is around 12-15 minutes. I have provided the run file of this method for test.pages.cbor-outlines.cbor. 

Location: ***provide location for the runfile(Rerank _by_weigth.txt) that I sent you****


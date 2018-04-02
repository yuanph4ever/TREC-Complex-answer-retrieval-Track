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

# 1.Pseudo Relevance Feedback with Entities

This task expands query by entities of first-round-answers from top1, top3, and top5. By using entities, we can remove the useless words with high term frequency and extract the words which should be given bias. We assume the result will be improved by doing this. We use DBpedia Spotlight, which is a tool for automatically annotating mentions of DBpedia resources in text, to annotate entities from first-round-answers. For searching, we use BM25 method.

# 2.Re-Rank by K-means Clustering

K-means clustering is a method of vector quantization, originally from signal processing, that is popular for cluster analysis in data mining. K-means clustering aims to partition n observations into k clusters in which each observation belongs to the cluster with the nearest mean, serving as prototype as the cluster.

This task uses K-means clusters to re-rank runfile. The basic idea is, firstly retrieve paragraphs for queries as we did before. Second, assign a cluster to query by comparing the similarity between them and generate the rank of clusters for each retrieved paragraph and see what the position of cluster for query is and use the reversed number of it as the additional score. Finally, use original score plus additional score to re-rank paragraphs. The algorithm is,

Score (query, paragraph) = BM25_score (q, p) + 10 / para_clu_rank (query’s cluster)

, where para_clu_rank (x) gives you the position of x in the rank of paragraph’s clusters. For example, if the cluster for query Q is C1 and the rank of clusters for paragraph P is C3, C1, C2 (from top to bottom). Then the final score for P based on Q is the original score (given by BM25) plus 5 (10/2). 
 
To achive this, I took 10,000 paragraphs from “dedup.articles-paragraphs.cbor” to make my corpus for clustering. I developed paraCluster.py to convert texts from clustering corpus to vectors and run K-means from “sklearn.cluster” in python to get clusters. Here I used k = 3 and “tfidf” of terms as the values in vectors. After clustering, I used the labels of each vector to assign each paragraph to its cluster. Then I have three clusters where store the text content of paragraphs. Next, I used lucene to index clusters and finally I have the index file for clusters. Since the index file for clusters is generated offline, the above process will not decreases the speed of searching.

For searching, I used “BM25” of “lucene” in java to compute the similarity between query/clusters and paragraph/clusters to assign cluster to query and generate clusters’ rank for each paragraph. And I used the above algorithm to re-rank.

# 3.Re-rank by Category Clustering

For each entity in Wikipedia, it has a category. We choose the categories which have more than 100 entities to make clusters. And we use the same methodology as K-means cluster to do re-rank for run files. 

For each paragraph Ids in the “dedup.articles-paragraphs.cbor”, entities were extracted using DBpedia variation. The extracted entities were then parsed into a java program that executes the curl command, which gives the type for each entity. The entities were then clustered according to their respective type.

# 4.Re-rank by Category Clustering

For each entity in Wikipedia, it has a category. We choose the categories which have more than 100 entities to make clusters. And we use the same methodology as K-means cluster to do re-rank for run files. 

For each paragraph Ids in the “dedup.articles-paragraphs.cbor”, entities were extracted using DBpedia variation. The extracted entities were then parsed into a java program that executes the curl command, which gives the type for each entity. The entities were then clustered according to their respective type.

# 5.Re-Rank by DBpedia Type and BM25 similarity with weight

This task uses DBpedia variation for the frequency of its type and and BM25 similarity score in each paragraph to re-rank the run file.

1)A search method was implemented, ” BM25”  similarity of “lucene” was used to compute similarity between query and paragraph to generate a baseline run file.
2) The paragraph IDs of the baseline run file were compared with the “dedup.articles-paragraphs.cbor” to get the text content.
3) For each paragraph ID, entities and its respective type was gathered using DBpedia spotlight.
4) The frequency of DBpedia type was clustered for each paragraph ID in the run file.
5) The run file was then re-ranked as per the descending order of the frequency of type in a paragraph given a query.
6) The run file generated was further processed to get a better score were both the rank and BM25 score could be used to get an appropriate ranking. New score = BM25 similarity score + 1/(Rank of the para id as per frequency of type)
7) The New score was then used to re-rank the run file. 

This method was implemented for top 20 paragraph IDs.









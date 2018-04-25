# TREC-Complex-answer-retrieval-Track

# Installation Instruction

# Steps
1. Clone this repository

2. Compile the project by using maven. A pom file is generated in the project for that. Please locate to the project and then do "mvn package"

3. Then an executable program called "ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar" can be found in "./target". Run the program using the command line 

java -jar Method_Signal Outline_Cbor Lucene_INDEX Output_Dir kmeans_clu_index_Dir types_clu_index_Dir

# Arguments Description
Methods_Signal: you want to put "-exp" which indicates run "Pseudo Relevance Feedback with Entities" and this will give you five runfiles in output path; "-kmeansClu" for "Re-Rank by K-means Clustering" to give you one runfile; "-typesClu" for "Re-rank by Category Clustering" to give you one run file; "-classify" for "Classifcation using J48 Classifier" gives you three run file;

Outline_Cbor: the outline files like "train.pages.cbor-outlines.cbor". 

Lucene_INDEX: the index file for corpus. 

Output_Dir: the directory you want to store the runfiles. 

kmeans_clu_index_Dir: the directory which stores the index file for clusters of kmeans. 

types_clu_index_Dir: the directory which stores the index file for clusters of types.

# Path on server
1. arg[0] - one of the following 
   "-exp", "-kmeansClu", "-typesClu", "-classify"
2. The path for lucene index args[2] - /home/ns1077/Prototype3/ParagraphIndexPr2/
3. The path for lucene index args[4] - /home/py1004/project/Index_kmeans_cluster
4. The path for lucene index args[5] - /home/py1004/project/Index_DBpedia_Entities

# An-Example-Run
java -jar ds_a1-0.0.1-SNAPSHOT-jar-with-dependencies.jar -classify /home/ns1077/benchmarkY1/benchmarkY1-train/train.pages.cbor-outlines.cbor /home/ns1077/ParagraphIndexPr2/ /home/ns1077/Runfile/ "/home/py1004/project/Index_kmeans_cluster" "/home/py1004/project/Index_DBpedia_Entities"

When the program is running, you can see messages from console. The messages indicate the process. 

When the program is done, you can see a message like "All works DONE. Generate [number of runfiles] runfiles in [output folder you specified]" from console.

# Test Results

The three test runs from our team are in the Path below in the server.
/home/ns1077/benchmarkY1TestRuns

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

# 4.Re-rank by DBpedia Type

DBpedia spotlight, is a system that automatically annotate text documents with DBpedia URIs. It gives a wide range of entity and type relationship. This relationship is used in this method to check the relevance of a paragraph.

This task uses DBpedia variation and the frequency of its type in each paragraph to re-rank the run file.
1)A search method was implemented, “BM25” similarity of “lucene” was used to compute similarity between query and paragraph to generate a baseline run file.
2) The paragraph IDs of the baseline run file were compared with the “dedup.articles-paragraphs.cbor” to get the text content.
3) For each paragraph ID, entities and its respective type was gathered using DBpedia spotlight.
4) The frequency of DBpedia type was clustered for each paragraph ID in the run file.
5) The run file was then re-ranked as per the descending order of the frequency of type in a paragraph given a query.

This method was implemented for top 20 paragraph IDs.


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

# 6.Naïve Bayes Classifier

In machine learning, naive Bayes classifiers are a family of simple "probabilistic classifiers "based on applying Bayes' theorem with strong (naive) independence assumptions between the features. Naive Bayes is a simple technique for constructing classifiers: models that assign class labels to problem instances, represented as vectors of feature values, where the class labels are drawn from
some finite set.

# 7.J48 Classifier

J48 is an algorithm used to generate a decision tree. J48 builds decision trees from a set of training data, using the concept of information entropy.The training data is a set of already classified samples. Each sample consists of a p-dimensional vector , where the represent attribute values or features of the sample, as well as the class in which falls.

At each node of the tree, J48 chooses the attribute of the data that most effectively splits its set of samples into subsets enriched in one class or the other. The splitting criterion is the normalized information gain (difference in entropy). The attribute with the highest normalized information gain is chosen to make the decision.

# 8.Random Forest Classifier

Random Forest are an ensemble learning method for classification, regression and other tasks, that operate by constructing a multitude of decision trees at training time and outputting the class that is the mode of the classes (classification) or mean prediction (regression) of the individual trees. Random decision forests correct for decision trees' habit of overfitting to their training set.











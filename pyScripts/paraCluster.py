#!/usr/bin/env python
import sys
import numpy as np
from matplotlib import pyplot as plt
from sklearn.cluster import KMeans
from sklearn.decomposition import PCA
import pandas as pd
from pandas.plotting import scatter_matrix

# ----------- global variables -----------------------
usageMsg = '''

Usage: createVectors.py [para_vectors_input_File] [output_Dir] [value_of_K]

'''

# -------------------------- Functions ---------------------------------------
def usage():
    if len(sys.argv) < 3 or sys.argv[1] == "-h":
        print(usageMsg)
        exit(0)

def isInt(str):
    try:
        int(str)
        return True
    except ValueError:
        return False

def callError(str):
    print("Error: " + str)
    exit(0)

def callMsg(msg):
    print("Msg: " + msg)

def makeDicforTerms(inputFile):

    ifile = open(inputFile)
    terms_dic = {}
    callMsg("reading data and making dictionary for terms...")

    ''' Here to make a dictionary of each term with its tfidf '''

    # count = 0

    for line in ifile.readlines():

        #print(".")

        '''
        if count == 10:
            break

        count += 1
        '''

        term_arr = line.split("--->")

        # print(count)

        for term in term_arr[1:]:

            # print(term)

            term_and_tf = term.split(":")
            term = term_and_tf[0]
            if isInt(term_and_tf[1]):
                tf = int(term_and_tf[1])
            else:
                tf = 0

            # print(term + "--->" + str(tf))

            if term in terms_dic:

                terms_dic[term] += tf

            else:

                terms_dic[term] = tf

    # print(terms_dic)

    ifile.close()

    callMsg("term dictionary made successfully")

    return terms_dic

def makeVecforPara(inputFile, terms_dic):

    ifile = open(inputFile)

    #a list of vector
    vec_list = []

    callMsg("getting vectors for each paragraph...")

    ''' Here to make vector for each paragraph '''

    count = 0

    for line in ifile.readlines():

        '''
        if count == 10:
            break

        count += 1
        '''

        count += 1

        if (count % 1000 == 0):
            callMsg("get " + str(count) + " vectors")

        term_arr = line.split("--->")

        # print(count)

        doc_dic = terms_dic

        for key in doc_dic:
            doc_dic[key] = 0

        for term in term_arr[1:]:

            term_and_tf = term.split(":")
            term = term_and_tf[0]
            if isInt(term_and_tf[1]):
                tf = int(term_and_tf[1])
            else:
                tf = 0
            if term in doc_dic:
                doc_dic[term] = tf
            else:
                callError("Term missing")

        doc_vec = []

        for key in doc_dic:
            doc_vec.append(doc_dic[key])

        vec_list.append(doc_vec)
        # print(doc_vec)

    # print (vec_list)
    ifile.close()
    callMsg("get vectors done")
    return vec_list

def doKmeans(k, vec_list):

    ''' Here to do clustering by kmeans'''

    X = np.array(vec_list)

    callMsg("Kmeans Clustring...")

    # k Clusters and run n_init times
    kmeans = KMeans(init='k-means++', n_clusters=k, n_init=10)
    # Fitting the input data
    kmeans = kmeans.fit(X)
    # Getting the cluster labels
    labels = kmeans.labels_
    #labels = kmeans.predict(X)
    #print(labels)
    #print(type(labels))
    # Centroid values
    # centroids = kmeans.cluster_centers_
    # print (centroids)

    makePlot(X)

    ''' 
    
    # to generate clusters which have the index of paragraph 

    print("generating clusters...")

    clu_group = [[] for i in range(k)]
    index = 0
    for i in labels:
        clu = clu_group[i]
        clu.append(index)
        clu_group[i] = clu
        index += 1

    print("total number of vector is " + str(index))

    return clu_group
    
    '''

    callMsg("Clustering done")
    return labels

def getText(str):
    newStr = ''
    term_arr = str.split("--->")
    for term in term_arr[1:]:

        term_and_tf = term.split(":")
        term = term_and_tf[0]
        newStr += term + ' '
    return newStr

def makeCluText(labels, inputFile, outputDir):

    callMsg("Start writting text files for clusters to " + outputDir)

    ifile = open(inputFile)
    index = 0
    for line in ifile.readlines():
        k = labels[index]
        ofile = open(outputDir + "/Cluster_" + str(k), "a")
        text = getText(line)
        ofile.write(text + '\n')
        ofile.close()
        index += 1

    callMsg("Writting done")

def makePlot(X):
    '''reduce dimensions and show plot'''
    callMsg("making plot")
    X_norm = (X - X.min()) / (X.max() - X.min())
    pca = PCA(n_components=2)  # 2-dimensional PCA
    trans = pd.DataFrame(pca.fit_transform(X_norm))
    scatter_matrix(trans, alpha=0.2, figsize=(6, 6), diagonal='kde')
    plt.show()
    callMsg("plot showing")

# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
if __name__ == '__main__':

    usage()

    inputFile = sys.argv[1]
    outputDir = sys.argv[2]
    k = int(sys.argv[3])

    terms_dic = makeDicforTerms(inputFile)
    vec_list = makeVecforPara(inputFile, terms_dic)
    labels = doKmeans(k, vec_list)
    makeCluText(labels, inputFile, outputDir)
    callMsg("Program done")































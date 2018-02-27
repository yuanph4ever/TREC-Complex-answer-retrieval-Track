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

Usage: createVectors.py [para_vectors] [kmeans_result.xml]  

'''

# -------------------------- usage() ---------------------------------------
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

# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
# check argument usage correctness
# usage()

input = sys.argv[1]
ifile = open(input)

terms_dic = {}

# count = 0

print ("reading data...")

for line in ifile.readlines():

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
ifile = open(input)

# count = 0

vec_list = []

print ("getting vectors...")

count = 0

for line in ifile.readlines():

    '''
    if count == 10:
        break

    count += 1
    '''

    count += 1

    if(count%1000 ==0):
        print("get " + str(count) + " vectors")

    term_arr = line.split("--->")

    # print(count)

    doc_dic = terms_dic

    for key in doc_dic:

        doc_dic[key] = 0

    for term in term_arr[1:]:

        term_and_tf = term.split(":")
        term = term_and_tf[0]
        if term in terms_dic:

            terms_dic[term] += tf

        else:

            terms_dic[term] = tf

        if term in doc_dic:

            doc_dic[term] = tf

    doc_vec = []

    for key in doc_dic:

        doc_vec.append(doc_dic[key])

    vec_list.append(doc_vec)
    # print(doc_vec)

# print (vec_list)
ifile.close()

X = np.array(vec_list)

print("Clustring...")

kmeans = KMeans(init='k-means++', n_clusters=3, n_init=10)
# Fitting the input data
kmeans = kmeans.fit(X)
# Getting the cluster labels
labels = kmeans.predict(X)
# Centroid values
centroids = kmeans.cluster_centers_
# print (centroids)

'''reduce dimensions and show plot'''
X_norm = (X - X.min())/(X.max() - X.min())
pca = PCA(n_components=2) #2-dimensional PCA
trans = pd.DataFrame(pca.fit_transform(X_norm))
scatter_matrix(trans, alpha=0.2, figsize=(6, 6), diagonal='kde')
plt.show()

'''
to generate an output 
     with centroids (vectors) and terms for each dimensions
       as a xml format
       '''

ofile = open(sys.argv[2], "w")

print("generating kmeans' result...")

ofile.write('<?xml version="1.0"?>\n')

ofile.write('<cluster>\n')

ofile.write('<dimensions>\n')

ofile.write(' <dimension type="dimension value">\n')

ofile.write('  <description>\n')

dnum = 0

for key in terms_dic:

    ofile.write(str(key) + ' ')

    dnum += 1

ofile.write('\n  </description>\n')

ofile.write(' </dimension>\n')

ofile.write('</dimensions>\n')

ofile.write('<centroids>\n')

num = 1

for centroid in centroids:

    ofile.write(' <centroid number="' + str(num) + '" size ="' + str(dnum) + '" type="vector value">\n')

    ofile.write('  <description>' )

    for value in centroid:

        ofile.write(str(value) + " ")

    ofile.write('</description>\n')

    ofile.write(' </centroid>\n')

    num += 1

ofile.write('</centroids>\n')

ofile.write('</cluster>\n')

ofile.close()

print("work done!")
























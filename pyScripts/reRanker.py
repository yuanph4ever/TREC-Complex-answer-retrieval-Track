#!/usr/bin/env python
import sys
import numpy as np
import xml.etree.ElementTree as ET
from numpy import dot
from numpy.linalg import norm

# ----------- global variables -----------------------
usageMsg = '''

Usage: reRanker.py [input_runfile] [input_clusters] [output_runfile]  

'''

# -------------------------- usage() ---------------------------------------
def usage():
    if len(sys.argv) < 4 or sys.argv[1] == "-h":
        print(usageMsg)
        exit(0)

# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
# check argument usage correctness
# usage()

# --- step 1: get dic for each centroid ---

cfile = open(sys.argv[1])

tree = ET.parse(cfile)

root = tree.getroot()

dim = root.find('dimensions').find('dimension').find('description').text

# print(dim)

dim_vector = str(dim).split()

# print(len(dim_vector))

cens = root.find('centroids')

# print(cens)

cen_dic_arr = []
cen_vec_arr = []

for cen in cens.findall('centroid'):

    cen_content = cen.find('description').text
    cen_vector_str = str(cen_content).split()
    cen_vector_dic = {}
    cen_vector_float = []
    index = 0
    while index < len(cen_vector_str):
        cen_vector_dic[dim_vector[index]] = float(cen_vector_str[index])
        cen_vector_float.append(float(cen_vector_str[index]))
        index += 1
    # print (len(cen_vector_dic))
    cen_dic_arr.append(cen_vector_dic)
    cen_vec_arr.append(cen_vector_float)
# print (len(cen_dic_arr))

# --- step 2: get centroid for query and paragraphs ---

ifile = open(sys.argv[2])

count = 0

flag = 1

for line in ifile.readlines():

    if line.startswith("enwiki"):

        #print(line)
        new_line = line.split("//")
        #print(new_line[1])
        if flag == 1:

            query_vec = [0 for i in range(len(dim_vector))]
            query_str = new_line[1]
            query_terms = query_str.split()
            # print (query_terms)
            for term in query_terms:
                if term in dim_vector:
                    idx = dim_vector.index(term)
                    query_vec[idx] = 1
                    # print (term + " " + str(idx))

            flag = 0
            # print(query_vec)

            print("---\nquery: " + query_str)
            for cen_vec in cen_vec_arr:
                cos_sim = dot(query_vec, cen_vec) / (norm(query_vec) * norm(cen_vec))
                print(cos_sim)
            print("---")

        '''
        para_str = new_line[2]
        para_terms = para_str.split()

        for term in para_terms:
            if term in dim_vector:
                idx = dim_vector.index(term)
                
        '''

    else :

        flag = 1
        count += 1


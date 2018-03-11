#!/usr/bin/env python
import sys
import numpy as np

# ----------- global variables -----------------------
usageMsg = '''

Usage: entitiesEval.py [ground_truth] [test_file] 

'''

# -------------------------- usage() ---------------------------------------
def usage():
    if len(sys.argv) < 3 or sys.argv[1] == "-h":
        print(usageMsg)
        exit(0)

# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
# check argument usage correctness
usage()

ground_truth = sys.argv[1]
test_file = sys.argv[2]

file = open(ground_truth)
gt_dic = {}

for line in file.readlines():

    arr_line = line.strip().split(" ---> ")
    para_id = ( arr_line[0].replace("--->", "").split() )[0]
    # print(para_id)
    if len(arr_line) > 1:
        entities = arr_line[1]
        # print (entities)
    else:
        entities = "***"

    gt_dic[para_id] = entities

file.close()

file = open(test_file)
tf_dic = {}

for line in file.readlines():

    arr_line = line.strip().split(" ---> ")
    para_id = ( arr_line[0].replace("--->", "").split() )[0]
    if len(arr_line) > 1:
        entities = arr_line[1]
    else:
        entities = "***"

    tf_dic[para_id] = entities

file.close()

#print(len(gt_dic))
#print("---------")
#print(len(tf_dic))

t_pos = 0
f_neg = 0
f_pos = 0

for key in gt_dic:

    if key in tf_dic:

        gt_en_list = str(gt_dic[key]).replace("|", " ").split()
        tf_en_list = str(tf_dic[key]).replace("|", " ").split()
        # print(en_list)
        dif = len(set(gt_en_list) - set(tf_en_list))
        t_pos += len(set(gt_en_list).intersection(set(tf_en_list)))
        f_neg += dif
        f_pos += len(tf_en_list) - len(set(gt_en_list).intersection(set(tf_en_list)))

    else:

        print("error: key not found")

precision = t_pos/(t_pos + f_pos)
recall = t_pos/(t_pos + f_neg)
f_one = ( 2 * precision * recall ) / ( precision + recall )

#print(precision)
#print(recall)
print(f_one)
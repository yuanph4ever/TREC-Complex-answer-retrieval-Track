import os
import sys
import numpy

# ----------- global variables -----------------------
usageMsg = '''
    Usage: RanklibCompatible.py [f1] [qrel] [output]
    '''

# -------------------------- usage() ---------------------------------------
def usage():
    if len(sys.argv) != 4 or sys.argv[1] == "-h":
        print(usageMsg)
        exit(0)

def openfiles():
    
    f1 = open(sys.argv[1])
#    f2 = open(sys.argv[2])
#    f3 = open(sys.argv[3])
#    f4 = open(sys.argv[4])
#    f5 = open(sys.argv[5])
    file_list = [f1]
    
    return file_list

def closefiles(file_list):
    
    for f in file_list:
        f.close()

# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
# check argument usage correctness
usage()

file_list = openfiles()

pair_set = set()

for f in file_list:
    
    for line in f.readlines():
        
        new_line = line.split()
        
        pair_set.add(new_line[0])

closefiles(file_list)

#print(pair_set)

pair_list = list(pair_set)

pair_list.sort()

#print(pair_list)

file_list = openfiles()

feature1 = {}
#bnn_dic = {}
#lmu_dic = {}
#ujm_dic = {}
#uds_dic = {}

count = 0

for f in file_list:
    
    tmp_dic = {}
    count += 1
    tmp_list = []
    
    for line in f.readlines():
        
        new_line = line.split()
        tmp_dic[new_line[0]] = 1/int(new_line[1])
        tmp_list.append(new_line[0])

    for pair in pair_list:
        
        if pair not in tmp_list:
            tmp_dic[pair] = 0

if count == 1:
    feature1 = tmp_dic
#    elif count == 2:
#        bnn_dic = tmp_dic
#elif count == 3:
#    lum_dic = tmp_dic
#    elif count == 4:
#        ujm_dic = tmp_dic
#elif count == 5:
#    uds_dic = tmp_dic

closefiles(file_list)

#print(lum_dic)

#get relevant list
qrel = open(sys.argv[2])
rel_set = set()
for line in qrel:
    new_line = line.split()
    rel_set.add(new_line[0] + "_" + new_line[2])

rel_list = list(rel_set)
rel_list.sort()
qrel.close()

#print(rel_list)


#write output
output = open(sys.argv[3], "wb")

for pair in pair_list:
    
    if pair in rel_list:
        target = "1"
    else:
        target = "0"

    qid = (pair.split("_"))[0]
    did = (pair.split("_"))[1]

#print(pair + "--->" + str(lum_dic[pair]))


    fv1 = feature1[pair]
#fv2 = bnn_dic[pair]
#fv3 = lum_dic[pair]
#fv4 = ujm_dic[pair]
#fv5 = uds_dic[pair]

    output_line = target + " qid:" + qid + " 1:" + str(fv1) + " # " + did + "\n"

#print(output_line)
    output.write(output_line.encode())

output.close()

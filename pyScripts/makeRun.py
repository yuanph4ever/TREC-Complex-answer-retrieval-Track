#!/usr/bin/env python
import sys
import operator

# ----------- global variables -----------------------
usageMsg = '''

Usage: makeRun.py [input_file_combined_features_and_score] [output_run] [num_of_top]

'''

# -------------------------- Functions ---------------------------------------
def usage():
    if len(sys.argv) < 4:
        print (usageMsg)

# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
if __name__ == '__main__':

    usage()

    ifile = open(sys.argv[1])

    ofile = open(sys.argv[2], "a")

    num_of_top = sys.argv[3]

    dic_of_dic = {}

    for line in ifile.readlines():
        new_line = line.split()
        print(new_line)
        pair_of_qd = new_line[4] + " " + new_line[8].strip("#")
        #print(pair_of_qd)
        new_score = new_line[2]
        dic[pair_of_qd] = new_score

    sorted_list = sorted(dic.items(), key=operator.itemgetter(1), reverse=True)


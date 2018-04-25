#!/usr/bin/env python
import sys

# ----------- global variables -----------------------
usageMsg = '''

Usage: makeRanklibFile.py -train/-test [run_1] [run_2] [run_3] ... [output] [*qrel]

'''

# -------------------------- Functions ---------------------------------------
def usage():
    if len(sys.argv) == 1:
        print(usageMsg)
        exit(0)

def callMsg(str):
    print("Msg: " + str)

def getFlag():
    return sys.argv[1]

def getNumofRun():
    if getFlag() == '-train':
        return len(sys.argv) - 4
    else:
        return len(sys.argv) - 3

def getQrel():
    if sys.argv[1] == '-train':
        return sys.argv[len(sys.argv) - 1]
    else:
        print("qrel not found")
        exit(1)

def getOutput():
    return sys.argv[len(sys.argv) - 2]

def openRuns(num_of_run):

    callMsg("opening run files ...")

    run_list = []
    count = 0
    while count < num_of_run:
        index = 2 + count
        f = open(sys.argv[index])
        run_list.append(f)
        count += 1

    callMsg("opening DONE")

    return run_list

def closeRuns(run_list):
    callMsg("closing run files ...")
    for f in run_list:
        f.close()
    callMsg("closing DONE")

def readQrel():
    callMsg("reading qrel ...")
    f = open(getQrel())
    couple_list = []
    for line in f.readlines():
        new_line = line.split()
        couple = new_line[0] + " " + new_line[2]
        couple_list.append(couple)
    f.close()
    callMsg("reading qrel DONE")
    return couple_list

def makeBigList():

    run_list = openRuns(getNumofRun())
    callMsg("reading run files to get features ...")
    dic_list = []
    couple_set = set()
    for run in run_list:
        dic = {}
        for line in run.readlines():
            new_line = line.split()
            couple = new_line[0] + " " + new_line[2]
            couple_set.add(couple)
            feature_value = 1/int(new_line[3])
            dic[couple] = feature_value
        dic_list.append(dic)

    unique_couple_list = list(couple_set)
    unique_couple_list.sort()
    big_list = [dic_list, unique_couple_list]
    closeRuns(run_list)
    callMsg("reading run files DONE")
    return big_list

def writeTest(pool, output):

    pool_out = open("/Users/paul.yuan/Desktop/CS980/pyScripts/learn2rank/" + output, 'a')
    for couple in pool:
        pool_out.write(couple + "\n")
    pool_out.close()

def writeOut(dic_list, unique_couple_list):

    if sys.argv[1] == '-train':
        true_couple_list = readQrel()
    else:
        true_couple_list = ['null']

    callMsg("writing file for ranklib")

    output = open(getOutput(), 'a')
    for couple in unique_couple_list:
        feature_list = []
        for dic in dic_list:
            if couple in dic:
                fv = dic[couple]
            else:
                fv = 0
            feature_list.append(fv)

        if true_couple_list[0] != 'null':
            if couple in true_couple_list:
                rel = 1
            else:
                rel = 0
        else:
            rel = 2

        new_couple = couple.split()
        qid = new_couple[0]
        pid = new_couple[1]
        output_line = str(rel) + " qid:" + qid
        num_of_feature = 1
        while num_of_feature <= len(feature_list):
            output_line += " " + str(num_of_feature) + ":" + str(feature_list[num_of_feature - 1])
            num_of_feature += 1

        output_line += " #" + pid + "\n"
        output.write(output_line)
    output.close()
    callMsg("writing DONE")


# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
if __name__ == '__main__':

    usage()

    callMsg("program start ...")

    big_list = makeBigList()

    writeOut(big_list[0], big_list[1])

    callMsg("program DONE")


#!/usr/bin/env python
import sys
import operator

# ----------- global variables -----------------------
usageMsg = '''

Usage: reRankByML.py [input_file_ranklib_format] [score_file] [output_run] [num_top]

'''

# -------------------------- Functions ---------------------------------------
def usage():
    if len(sys.argv) < 5:
        print (usageMsg)
        exit(0)

def readScoreFile():

    score_file = open(sys.argv[2])

    print("reading " + sys.argv[2] + " ...")

    sf_dic = {}

    #list_of_dic = [] #a list of dictionaries (key is num_queryid, value is score)

    dic_of_dic = {}

    last_qid = ' ' #record the last query id

    last_sf_dic = {} #record the last dictionary (key is num_queryid, value is score)

    for line in score_file.readlines():

        new_line = line.split()

        qid  = new_line[0]

        if qid == last_qid:
            #for the same query id
            sf_dic[new_line[1] + " " + new_line[0]] = float(new_line[2])
            last_sf_dic = sf_dic

        else:

            if last_qid == ' ':
                #first time
                new_line = line.split()
                qid = new_line[0]
                sf_dic[new_line[1] + " " + new_line[0]] = float(new_line[2])
                last_sf_dic = sf_dic
                last_qid = qid
            else:
                #not the first time
                dic_of_dic[last_qid] = last_sf_dic
                #list_of_dic.append(last_sf_dic)
                sf_dic = {}
                new_line = line.split()
                qid = new_line[0]
                sf_dic[new_line[1] + " " + new_line[0]] = float(new_line[2])
                last_sf_dic = sf_dic
                last_qid = qid

    #sorted_list_of_list = []

    sorted_dic_of_dic = {}

    for key in dic_of_dic:
        dic = dic_of_dic[key]
        sorted_sf_list = sorted(dic.items(), key=operator.itemgetter(1), reverse=True)
        new_dic = {}
        for tuple in sorted_sf_list:
            sf = tuple[0]
            score = tuple[1]
            new_dic[sf] = score
        sorted_dic_of_dic[key] = new_dic

    '''
    for dic in list_of_dic:

        sorted_sf_list = sorted(dic.items(), key=operator.itemgetter(1))
        sorted_list_of_list.append(sorted_sf_list)
    '''

    return sorted_dic_of_dic #give you a dic of dic which has a key of "pos queryid" and value of score

def readRLFile():
    ranklib_file = open(sys.argv[1])
    print("reading " + sys.argv[1] + " ...")
    dic_sf_doc = {}
    dic_of_dic = {}
    last_dic = {}
    last_queryid = ' '
    pos = 0
    for line in ranklib_file.readlines():
        new_line = line.split()
        queryid = new_line[1].strip("qid:enwiki:")
        querydoc = new_line[5].strip("#")
        if last_queryid == ' ':
            dic_sf_doc[str(pos) + " " + queryid] = querydoc
            last_queryid = queryid
            last_dic = dic_sf_doc
            pos += 1
        else:
            if queryid == last_queryid:
                dic_sf_doc[str(pos) + " " + queryid] = querydoc
                last_dic = dic_sf_doc
                pos += 1
            else:
                dic_of_dic[last_queryid] = last_dic
                dic_sf_doc = {}
                pos = 0
                dic_sf_doc[str(pos) + " " + queryid] = querydoc
                last_queryid = queryid
                last_dic = dic_sf_doc
                pos += 1

    return dic_of_dic #return a dic which has key of queryid and value of a dic which has key of "pos queryid" and value of docid

def writeOutKey(score_dic_of_dic, rl_dic_of_dic):

    key_of_score = open("/Users/paul.yuan/Desktop/Ranklib/key_of_score", "a")
    for key in score_dic_of_dic:
        key_of_score.write(key + "\n")
    key_of_rl = open("/Users/paul.yuan/Desktop/Ranklib/key_of_rl", "a")
    for key in rl_dic_of_dic:
        key_of_rl.write(key + "\n")

def writeOut(score_dic_of_dic, rl_dic_of_dic):

    output = sys.argv[3]
    print ("writing to " + sys.argv[3] + " ...")
    ofile = open(output, "a")
    num_of_top = int(sys.argv[4])
    for key in score_dic_of_dic:
        queryid = key
        score_dic = score_dic_of_dic[key]
        if key in rl_dic_of_dic:
            rl_dic = rl_dic_of_dic[key]
            pos = 1
            for key in score_dic:
                docid = rl_dic[key]
                score = score_dic[key]
                outline = "enwiki:" + queryid + " Q0 " + docid + " " + str(pos) + " " + str(score) + " RankLib\n"
                ofile.write(outline)
                pos += 1
                if pos == num_of_top + 1:
                    break
        else:
            print(key)

    ofile.close()


# -----------------------------------------------------------------------
# --------------------------- main --------------------------------------
if __name__ == '__main__':

    usage()

    sorted_dic_of_dic_scoreFile = readScoreFile()
    '''
    for key in sorted_dic_of_dic_scoreFile:
        print(key)
    #print(sorted_dic_of_dic_scoreFile)
    '''
    dic_of_dic_RankLibFormatFile = readRLFile()
    #print(dic_of_dic_RankLibFormatFile)

    writeOutKey(sorted_dic_of_dic_scoreFile, dic_of_dic_RankLibFormatFile)

    #writeOut(sorted_dic_of_dic_scoreFile, dic_of_dic_RankLibFormatFile)
    print("program DONE")















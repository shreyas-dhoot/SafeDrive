import urllib
import urllib.request
import json
def url_builder(lat, longi):
    main = 'https://traffic.cit.api.here.com/traffic/6.1/flow.json?prox='
    #lat = 21.1535
    sec = '%2C'
    #longi = 79.0889
    final = '%2C100&app_id=X3NtRbJuTQ3bhyQ1ncGf&app_code=9JkXt2Znlk-bAdtv4ECjEg&maxfunctionalclass=2'

    full_url = main + str(lat) + sec + str(longi) + final
    print("calling: ", full_url)
    return full_url
    
def data_fetch(full_api_url):
    url = urllib.request.urlopen(full_api_url)
    output = url.read().decode('utf-8')
    #print(output)
    raw_api_dict = json.loads(output)
    jf_list = []
    address_list = []
    for i in range(len(raw_api_dict['RWS'][0]['RW'])):
        for j in range(len(raw_api_dict['RWS'][0]['RW'][i]["FIS"][0]["FI"])):
            address_list.append(raw_api_dict['RWS'][0]['RW'][i]["FIS"][0]["FI"][j]['TMC']['DE'])
            jf_list.append(raw_api_dict['RWS'][0]['RW'][i]["FIS"][0]["FI"][j]['CF'][0]['JF'])
    #print(raw_api_dict['RWS'][0]['RW'][0]['FIS'][0]['FI'][0]["TMC"]["DE"])
    #print(raw_api_dict['RWS'][0]['RW'][0]['FIS'][0]['FI'][0]["CF"][0]["JF"])
    url.close()
    return address_list, jf_list
def lcs(X , Y):
    # find the length of the strings
    m = len(X)
    n = len(Y)
 
    # declaring the array for storing the dp values
    L = [[None]*(n+1) for i in range(m+1)]
 
    """Following steps build L[m+1][n+1] in bottom up fashion
    Note: L[i][j] contains length of LCS of X[0..i-1]
    and Y[0..j-1]"""
    for i in range(m+1):
        for j in range(n+1):
            if i == 0 or j == 0 :
                L[i][j] = 0
            elif X[i-1] == Y[j-1]:
                L[i][j] = L[i-1][j-1]+1
            else:
                L[i][j] = max(L[i-1][j] , L[i][j-1])
 
    # L[m][n] contains the length of LCS of X[0..n-1] & Y[0..m-1]
    return L[m][n]
from difflib import SequenceMatcher
def similar(a, b):
    a = a.lower()
    b = b.lower()
    l1 = a.split(' ')
    l2 = b.split(' ')
    w1 = []
    w2 = []
    f1 = []
    f2 = []
    for x in l1:
        w1+= x.split(',') 
    for y in l2:
        w2+= y.split(',')
    for x in w1:
        f1+= x.split('/')
    for y in w2:
        f2+= y.split('/')
    try:
        if "road" in f1: f1.remove('road')
        if "road" in f2: f2.remove('road')
        if "chowk" in f1: f1.remove('chowk')
        if "chowk" in f2: f2.remove('chowk')
    except:
        pass
    #print(f1)
    #print(f2)
    count= 0
    for x in f1:
        if x in f2:
            count+=1

    return count
    #return lcs(a,b)
    #return SequenceMatcher(None, a, b).ratio()

def most_match_jf(string, lat, longi):
    address, jf_list = data_fetch(url_builder(lat, longi))
    index = 0
    max_sim = 0
    for c, x in enumerate(address):
        val = similar(x, string)
        #print(x, val)
        if val> max_sim:
            max_sim = val
            index = c
    return jf_list[index], address[index]

if __name__ == "__main__":
    most_match_jf("Fergusson College Road", 18.52175, 73.84102)

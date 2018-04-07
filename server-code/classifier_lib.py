import pandas as pd
import numpy as np
from sklearn import svm

filename = "dataset.csv"

#returns data, last column are the labels
def parse_csv(filename):
    a = np.genfromtxt(filename, delimiter=',', skip_header=1)
    return (a[:,1:])

means =  [30.405799871254164, 1.0, 4.7525706114499995, 0.82499999999999996]
stds =  [14.324286549551823, 0.81649658092772603, 1.9800801770665239, 0.77095719725546374]
def normalize_traindata(dataset, samples = 120):
    global means
    global stds
    for x in range(dataset.shape[1]-1):
        means.append(np.mean(dataset[:, x].reshape((samples))))
        stds.append(np.std(dataset[:, x].reshape((samples))))
        dataset[:,x] = dataset[:,x] - means[x]
        dataset[:,x] = dataset[:,x] / stds[x]
    return dataset
def normalize_testdata(dataset):
    global means
    global stds
    print("means = ", means)
    print("stds = ", stds)
    for x in range(dataset.shape[1]-1):
        dataset[:,x] = dataset[:,x] - means[x]
        dataset[:,x] = dataset[:,x] / stds[x]
    return dataset
def normalize_testpoints(dataset):
    global means
    global stds
    print(dataset)
    for x in range(dataset.shape[1]):
        dataset[:,x] = dataset[:,x] - means[x]
        dataset[:,x] = dataset[:,x] / stds[x]
    return dataset



def get_indices_of_wrong(labels, predictions):
    indices = []
    for x in range(labels.shape[0]):
        if labels[x] != predictions[x]:
            indices.append(x)
    return indices
def check_on_test(classifier):
    test_filename = "testfile.csv"
    test_set = parse_csv(test_filename)
    print(test_set.shape)
    print(test_set)
    print("------------------")
    test_set = normalize_testdata(test_set)
    prediction = classifier.predict(test_set[:, :test_set.shape[1]-1])
    index = get_indices_of_wrong(prediction, test_set[:, test_set.shape[1]-1])
    print(index)
    print(prediction)
    print(np.count_nonzero(prediction != test_set[:, test_set.shape[1] - 1]))

def check_single_point(speed, vehicle_class, jf, weather):
    import _pickle as cPickle
    with open('svm_hyperpara.pkl', 'rb') as fid:
        gnb_loaded = cPickle.load(fid)
    mat = np.array([speed, vehicle_class, jf, weather]).reshape(1, 4)
    mat = normalize_testpoints(np.float64(mat))
    return gnb_loaded.predict(mat)[0]


if __name__=="__main__":
    data = parse_csv(filename)
    dataset = normalize_traindata(data)
    labels = dataset[:,dataset.shape[1] - 1]


    print("Training an SVM...")
    classifier = svm.SVC()
    classifier.fit(dataset[:, :dataset.shape[1] - 1], dataset[:,dataset.shape[1] - 1])
    #print(dataset)
    import _pickle as cPickle
    with open("svm_hyperpara.pk1", "wb") as fid:
        cPickle.dump(classifier, fid)
    prediction = classifier.predict(dataset[:, :dataset.shape[1] - 1])
    index = get_indices_of_wrong(dataset[:, dataset.shape[1] - 1], prediction)

    print(index)
    print(np.count_nonzero(prediction != dataset[:, dataset.shape[1] - 1]))
    print("ON test DATA...")
    check_on_test(classifier)

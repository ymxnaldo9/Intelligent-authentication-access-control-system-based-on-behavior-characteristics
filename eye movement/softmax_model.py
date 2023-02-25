from keras.models import Sequential
from keras.layers import Dense, Dropout
from keras.optimizers import SGD
from sklearn.preprocessing import LabelBinarizer
import numpy as np
path = r'D:\PycharmProjects\my project_eye movement\train.csv'
data = np.genfromtxt(path, delimiter=',', skip_header=1)
def features_model_renew(data):
    l = np.shape(data)[0]
    y_train = np.zeros([l])
    for i in range(l):
        y_train[i] = data[i][12]
    x_train = data[:, 0:12]
    one_hot = LabelBinarizer()
    y_train = one_hot.fit_transform(y_train)
    model = Sequential()
    model.add(Dense(32, activation='relu', input_dim=12))
    model.add(Dropout(0.5))
    model.add(Dense(32, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(41, activation='softmax'))
    sgd = SGD(lr=0.01, decay=1e-6, momentum=0.9, nesterov=True)
    model.compile(loss='categorical_crossentropy', optimizer=sgd, metrics=['accuracy'])
    model.fit(x_train, y_train, epochs=10, batch_size=154)
    model.save('D:\PycharmProjects\my project_eye movement\model.h5')


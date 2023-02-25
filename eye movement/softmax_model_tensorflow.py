import tensorflow as tf
from sklearn.preprocessing import LabelBinarizer
import numpy as np
import os
path = r'D:\PycharmProjects\my project_eye movement\train.csv'
data = np.genfromtxt(path, delimiter=',', skip_header=1)
model_path = './tensorflow_model/Model.ckpt'
def data_preprocessing(data):
    data_x = data[:, 0:12]
    data_y = data[:, 12:]
    one_hot = LabelBinarizer()
    data_y = one_hot.fit_transform(data_y)
    return data_x, data_y
def model_create(data, label_num, model_path):
    data_x, data_y = data_preprocessing(data)
    x = tf.placeholder(tf.float32, [None, 12])
    w = tf.Variable(tf.zeros([12, label_num]))
    b = tf.Variable(tf.zeros([label_num]))
    y = tf.nn.softmax(tf.matmul(x, w) + b)  ##预测值
    y = tf.clip_by_value(y, 1e-8, 1.0)
    y_ = tf.placeholder(tf.float32, [None, label_num])  ##真实值
    cross_entropy = -tf.reduce_sum(y_ * tf.log(y))
    train_step = tf.train.GradientDescentOptimizer(0.01).minimize(cross_entropy)
    init = tf.global_variables_initializer()
    gpu_no = '0'
    os.environ['CUDA_VISIBLE_DEVICES'] = gpu_no
    config = tf.ConfigProto()
    config.gpu_options.allow_growth = True
    config.gpu_options.per_process_gpu_memory_fraction = 0.1
    sess = tf.Session(config=config)
    sess.run(init)
    for i in range(30000):
        sess.run(train_step, feed_dict={x: data_x, y_: data_y})
    saver = tf.train.Saver()
    saver.save(sess, model_path)
label_num = 40
model_create(data, 40, model_path)

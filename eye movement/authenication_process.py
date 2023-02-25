import tensorflow as tf
import os
import numpy as np
model_path = 'tensorflow_model/model.ckpt'
label_num = 40
data = np.array([[5,0.1264954,0.08465212,0.03360677,-0.02878819,0.6883247,1.53948079,2.23656188,1.16274239,0.04889571,3,2]])
def authenication(data, model_path):
    gpu_no = '0'
    os.environ['CUDA_VISIBLE_DEVICES'] = gpu_no
    config = tf.ConfigProto()
    config.gpu_options.allow_growth = True
    config.gpu_options.per_process_gpu_memory_fraction = 0.1
    sess = tf.Session(config=config)
    x = tf.placeholder(tf.float32, [None, 12])
    w = tf.Variable(tf.zeros([12, label_num]))
    b = tf.Variable(tf.zeros([label_num]))
    saver = tf.train.Saver()
    saver.restore(sess, model_path)
    y = tf.nn.softmax(tf.matmul(x, w) + b)
    label = tf.argmax(y, 1)
    output1 = sess.run(y, feed_dict={x: data})
    output2 = sess.run(label, feed_dict={x: data})
    if output1[0][output2] >= 0.8:
        return output2
    else:
        return None



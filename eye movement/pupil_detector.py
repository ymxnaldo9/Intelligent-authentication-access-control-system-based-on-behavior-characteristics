import cv2
import numpy as np
from eye_seperation import left_eye
from eye_seperation import right_eye
def iris_size_ratio(image):                ##计算虹膜面积占比
    height, width = image.shape[:2]
    size = height * width
    iris_size = size - cv2.countNonZero(image)
    ratio = iris_size / size
    return ratio
def iris_seperation(eye_image, threshold):             ##按最优阈值分离虹膜
    kernel = np.ones((3, 3), np.uint8)   #uint8将取值定义在0到255之间
    new_frame = cv2.bilateralFilter(eye_image, 10, 15, 15)
    new_frame = cv2.erode(new_frame, kernel, iterations=3)
    new_frame = cv2.threshold(new_frame, threshold, 255, cv2.THRESH_BINARY)[1]
    return new_frame
def best_threshold(image):                 ##计算最优阈值
    average_iris_size = 0.48
    thres = np.zeros(256)
    for i in range(256):
        new_image = iris_seperation(image, i)
        t = iris_size_ratio(new_image)
        thres[i] = abs(t - average_iris_size)
    threshold = np.argmin(thres)
    return threshold
def iris_detect(image, threshold):                      ##虹膜质心
    eye_image = iris_seperation(image, threshold)
    contours, a = cv2.findContours(eye_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)[-2:]
    contours = sorted(contours, key=cv2.contourArea)
    moments = cv2.moments(contours[0])
    x = int(moments['m10'] / moments['m00'])
    y = int(moments['m01'] / moments['m00'])
    return x, y
def left_pupil(image):                    ##左瞳孔坐标
    left_eye_, left_origin, left_center = left_eye(image)
    left_threshold = best_threshold(left_eye_)
    left_x_, left_y_ = iris_detect(left_eye_, left_threshold)
    left_x = left_x_ + left_origin[0]
    left_y = left_y_ + left_origin[1]
    return (left_x, left_y)
def right_pupil(image):                     ##有瞳孔坐标
    right_eye_, right_origin, right_center = right_eye(image)
    right_threshold = best_threshold(right_eye_)
    right_x_, right_y_ = iris_detect(right_eye_, right_threshold)
    right_x = right_x_ + right_origin[0]
    right_y = right_y_ + right_origin[1]
    return (right_x, right_y)

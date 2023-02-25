import dlib
import numpy as np
import cv2
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor('shape_predictor_68_face_landmarks.dat')
def left_eye(image):          ##左眼图像、最小原点、中心点
    rects = detector(image, 0)
    for i in range(len(rects)):
        landmarks = np.matrix([[p.x, p.y] for p in predictor(image, rects[i]).parts()])
    left_eye = np.zeros((6, 2))
    for i in range(6):
        left_eye[i] = landmarks[i+42]
    min = np.min(left_eye, axis=0)
    max = np.max(left_eye, axis=0)
    left_eye_image = image[int(min[1]):int(max[1] + 1), int(min[0]):int(max[0] + 1)]
    center = ((min[0] + max[0]) / 2, (min[1] + max[1]) / 2)
    return left_eye_image, min, center
def right_eye(image):             ##右眼图像、最小原点、中心点
    rects = detector(image, 0)
    for i in range(len(rects)):
        landmarks = np.matrix([[p.x, p.y] for p in predictor(image, rects[i]).parts()])
    right_eye = np.zeros((6, 2))
    for i in range(6):
        right_eye[i] = landmarks[i+36]
    min = np.min(right_eye, axis=0)
    max = np.max(right_eye, axis=0)
    right_eye_image = image[int(min[1]):int(max[1] + 1), int(min[0]):int(max[0] + 1)]
    center = ((min[0] + max[0]) / 2, (min[1] + max[1]) / 2)
    return right_eye_image, min, center



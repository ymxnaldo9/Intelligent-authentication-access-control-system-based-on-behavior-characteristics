import cv2
import dlib
import numpy as np
from gaze_estimation import gaze_estimation
def video_recording():
    detector = dlib.get_frontal_face_detector()
    cap = cv2.VideoCapture(0)
    fps = cap.get(cv2.CAP_PROP_FPS)
    image_number = 100
    i = 0
    time = 0
    time_unit = 1 / fps
    gaze = np.zeros([image_number, 3])
    while True:
        ret, frame = cap.read()
        if ret == True:
            time = time + time_unit
            image = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            rects = detector(image, 0)
            if len(rects) != 1:  ##判断是否仅有一张人脸
                continue
            else:
                i = i + 1
                print(i)
                try:
                    gaze_point = gaze_estimation(image)
                    gaze[i][0] = time
                    gaze[i][1] = gaze_point[0]
                    gaze[i][2] = gaze_point[1]
                except:
                    gaze[i][0] = time
                    gaze[i][1] = np.nan
                    gaze[i][2] = np.nan
        if i == image_number - 1:
            break
    cap.release()
    cv2.destroyAllWindows()
    return gaze






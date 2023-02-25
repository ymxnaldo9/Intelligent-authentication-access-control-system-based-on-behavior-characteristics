import imageio
import cv2
import numpy as np
import dlib
from gaze_estimation import gaze_estimation
filename = '1.mp4'
detector = dlib.get_frontal_face_detector()
vid = imageio.get_reader(filename, 'ffmpeg')
fps = 30
image_number = 100
time = 0
time_unit = 1 / fps
gaze = np.zeros([image_number, 3])
i = 0
for num,im in enumerate(vid):
    img = np.array(im)
    img_gray = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
    time = time + time_unit
    rects = detector(img_gray, 0)
    print(len(rects))
    if len(rects) != 1:
        continue
    else:
        try:
            gaze_point = gaze_estimation(img_gray)
            gaze[i][0] = time
            gaze[i][1] = gaze_point[0]
            gaze[i][2] = gaze_point[1]
            i = i + 1
        except:
            gaze[i][0] = time
            gaze[i][1] = np.nan
            gaze[i][2] = np.nan
            i = i + 1
    if i == image_number:
        break

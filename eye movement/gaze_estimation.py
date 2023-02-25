from eye_seperation import left_eye
from eye_seperation import right_eye
from pupil_detector import left_pupil
from pupil_detector import right_pupil
def gaze_estimation(image):       ##估测的注视点（相对值,在0到1之间）
    left = left_pupil(image)
    right = right_pupil(image)
    left_eye_, left_origin, left_center = left_eye(image)
    right_eye_, right_origin, right_center = right_eye(image)
    gaze_left_x = (left[0] - left_origin[0]) / ((left_center[0] - left_origin[0]) * 2)
    gaze_right_x = (right[0] - right_origin[0])/ ((right_center[0] - right_origin[0]) * 2)
    gaze_x = (gaze_left_x + gaze_right_x) / 2
    gaze_left_y = (left[1] - left_origin[1]) / ((left_center[1] - left_origin[1]) * 2)
    gaze_right_y = (right[1] - right_origin[1])/ ((right_center[1] - right_origin[1]) * 2)
    gaze_y = (gaze_left_y + gaze_right_y) / 2
    return (gaze_x, gaze_y)


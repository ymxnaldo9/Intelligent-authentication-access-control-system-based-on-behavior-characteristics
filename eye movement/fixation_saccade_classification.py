import numpy as np
def velocity(gaze):
    l = len(gaze)
    gaze_velocity = np.zeros([l])
    for i in range(l):
        if (gaze[i][1] == np.nan) or (gaze[i][2] == np.nan):
            gaze_velocity[i] = 0
        elif i == 0:
            v_x = (gaze[i + 1][1] - gaze[i][1]) / (gaze[i][0] - gaze[i + 1][0])
            v_y = (gaze[i + 1][2] - gaze[i][2]) / (gaze[i][0] - gaze[i + 1][0])
            gaze_velocity[i] = np.sqrt(np.square(v_x) + np.square(v_y))
        elif i == l - 1:
            v_x = (gaze[i][1] - gaze[i - 1][1]) / (gaze[i][0] - gaze[i - 1][0])
            v_y = (gaze[i][2] - gaze[i - 1][2]) / (gaze[i][0] - gaze[i - 1][0])
            gaze_velocity[i] = np.sqrt(np.square(v_x) + np.square(v_y))
        else:
            v_x = (gaze[i + 1][1] - gaze[i - 1][1]) / (gaze[i + 1][0] - gaze[i - 1][0])
            v_y = (gaze[i + 1][2] - gaze[i - 1][2]) / (gaze[i + 1][0] - gaze[i - 1][0])
            gaze_velocity[i] = np.sqrt(np.square(v_x) + np.square(v_y))
    return gaze_velocity
def classification(gaze, velocity_threshold):
    gaze_velocity = velocity(gaze)
    l = len(gaze_velocity)
    events = []
    for i in range(l):
        if gaze_velocity[i] > velocity_threshold:
            events.append('Saccade')
        elif gaze_velocity[i] < velocity_threshold and gaze_velocity[i] > 0:
            events.append('Fixation')
        else:
            events.append('Blink')
    events = np.array(events)
    return events
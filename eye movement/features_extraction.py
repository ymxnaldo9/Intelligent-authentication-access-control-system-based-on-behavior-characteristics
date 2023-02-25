import numpy as np
from sklearn.cluster import MeanShift
from calculation import peak_velocity
from calculation import central_point
from calculation import graham_scan
from calculation import area_calculation
gaze_path = 'data\Participant00\gaze_positions.csv'
events_path = 'data\Participant00\events.csv'
features_number = 12
def fixation_index(gaze, events):
    t = []
    m = []
    for i in range(len(events)):
        if events[i] == 'Fixation' and i == 0 and events[i + 1] == 'Fixation':
            t.append(i)
        if events[i] == 'Fixation' and i == 0 and events[i + 1] != 'Fixation':
            t.append(i)
            m.append(i + 1)
        if events[i] == 'Fixation' and i == len(events) - 1 and events[i - 1] == 'Fixation':
            m.append(i)
        if events[i] == 'Fixation' and i == len(events) - 1 and events[i - 1] != 'Fixation':
            t.append(i)
            m.append(i)
        if i != 0 and i != len(events) - 1:
            if events[i] == 'Fixation' and events[i - 1] != 'Fixation':
                t.append(i)
            if events[i] == 'Fixation' and events[i + 1] != 'Fixation':
                m.append(i + 1)
    if len(t) == len(m):
        fixation_count = len(t)
    else:
        print('error')
    fixation_index = np.zeros([fixation_count, 5])
    for i in range(fixation_count):
        if t[i] != m[i]:
            gaze_i = gaze[t[i]:m[i]]
            fixation_index[i][0] = t[i]  ##fixation开始时间
            fixation_index[i][1] = m[i]  ##fixation结束时间
            fixation_index[i][2] = gaze[m[i]][0] - gaze[t[i]][0]  ##周期
            fixation_index[i][3] = central_point(gaze_i)[0]  ##fixation中心点x轴坐标
            fixation_index[i][4] = central_point(gaze_i)[1]  ##fixation中心点y轴坐标
        else:
            fixation_index[i][0] = t[i]  ##fixation开始时间
            fixation_index[i][1] = m[i]  ##fixation结束时间
            fixation_index[i][2] = 0  ##周期
            fixation_index[i][3] = gaze[t[i]][1]  ##fixation中心点x轴坐标
            fixation_index[i][4] = gaze[m[i]][2]
    return fixation_index
def saccade_index(gaze, events):
    t = []
    m = []
    for i in range(len(events)):
        if events[i] == 'Saccade' and i == 0 and events[i + 1] == 'Saccade':
            t.append(i)
        if events[i] == 'Saccade' and i == 0 and events[i + 1] != 'Saccade':
            t.append(i)
            m.append(i + 1)
        if events[i] == 'Saccade' and i == len(events) - 1 and events[i - 1] == 'Saccade':
            m.append(i)
        if events[i] == 'Saccade' and i == len(events) - 1 and events[i - 1] != 'Saccade':
            t.append(i)
            m.append(i)
        if i != 0 and i != len(events) - 1:
            if events[i] == 'Saccade' and events[i - 1] != 'Saccade':
                t.append(i)
            if events[i] == 'Saccade' and events[i + 1] != 'Saccade':
                m.append(i + 1)
    if len(t) == len(m):
        saccade_count = len(t)
    else:
        print('error')
    saccade_index = np.zeros([saccade_count, 10])
    for i in range(saccade_count):
        if t[i] != m[i]:
            duration = gaze[m[i]][0] - gaze[t[i]][0]
            gaze_i = gaze[t[i]:m[i]]
            saccade_index[i][0] = t[i]  ##saccade开始时间
            saccade_index[i][1] = m[i]  ##saccade结束时间
            saccade_index[i][2] = gaze[m[i]][1] - gaze[t[i]][1]  ##水平幅度
            saccade_index[i][3] = gaze[m[i]][2] - gaze[t[i]][2]  ##竖直幅度
            saccade_index[i][4] = np.sqrt(np.square(saccade_index[i][2]) + np.square(saccade_index[i][3]))  ##vertorial幅度
            saccade_index[i][5] = saccade_index[i][2] / duration  ##水平速度
            saccade_index[i][6] = saccade_index[i][3] / duration  ##竖直速度
            saccade_index[i][7] = saccade_index[i][4] / duration  ##vertorial速度
            saccade_index[i][8] = peak_velocity(gaze_i)  ##vertorial最快速度
            saccade_index[i][9] = saccade_index[i][8] / saccade_index[i][7]
        else:
            saccade_index[i][0] = t[i]  ##saccade开始时间
            saccade_index[i][1] = m[i]  ##saccade结束时间
            saccade_index[i][2] = 0  ##水平幅度
            saccade_index[i][3] = 0  ##竖直幅度
            saccade_index[i][4] = 0  ##vertorial幅度
            saccade_index[i][5] = 0  ##水平速度
            saccade_index[i][6] = 0  ##竖直速度
            saccade_index[i][7] = 0  ##vertorial速度
            saccade_index[i][8] = 0  ##vertorial最快速度
            saccade_index[i][9] = 0
    return saccade_index
def scanpath_length(fixation_index):
    l = len(fixation_index)
    scanpath_length = 0
    for i in range(l - 1):
        point1 = np.array([fixation_index[i][3], fixation_index[i][4]])
        point2 = np.array([[fixation_index[i + 1][3], fixation_index[i + 1][4]]])
        distance = np.linalg.norm(point1 - point2)
        scanpath_length = scanpath_length + distance
    return scanpath_length
def scanpath_area(fixation_index):
    try:
        f = fixation_index[:, 3:]
        f_list = list(f)
        stack = graham_scan(f_list)
        scanpath_area = area_calculation(stack)
    except:
        scanpath_area = 0
    return scanpath_area
def inflection_count(saccade_index):
    inflections = 0
    i = 0
    saccade_count = len(saccade_index)
    saccade_velocity = saccade_index[:, 5:7]
    while i < saccade_count - 1:
        if (np.sign(saccade_velocity[i][0]) != np.sign(saccade_velocity[i + 1][0])) or (
                np.sign(saccade_velocity[i][1]) != np.sign(saccade_velocity[i + 1][1])):
            inflections = inflections + 1
        i = i + 1
    return inflections
def region_of_interest(gaze):
    gaze_point = gaze[:, 1:]
    gaze_point = np.nan_to_num(gaze_point)
    clf = MeanShift()
    point = clf.fit_predict(gaze_point)
    region_of_interest = np.max(point) + 1
    return region_of_interest
def feature_vector(gaze, events):
    fixations = fixation_index(gaze, events)
    saccades = saccade_index(gaze, events)
    feature_vector = np.zeros([features_number])
    feature_vector[0] = np.shape(fixations)[0]  ##fixation count
    feature_vector[1] = np.mean(fixations, axis = 0)[2]  ##average fixaiton duration
    feature_vector[2] = np.mean(saccades, axis = 0)[4]  ##average vectorial saccade amplitude
    feature_vector[3] = np.mean(saccades, axis = 0)[2]  ##average horizontal saccade amplitude
    feature_vector[4] = np.mean(saccades, axis = 0)[3]  ##average vertical saccade amplitude
    feature_vector[5] = np.mean(saccades, axis = 0)[7]  ##average vertorial saccade velocity
    feature_vector[6] = np.mean(saccades, axis = 0)[8]  ##average vertorial saccade peak velocity
    feature_vector[7] = feature_vector[6] / feature_vector[5]  ##velocity waveform indicator(Q)
    feature_vector[8] = scanpath_length(fixations)  ##scanpath length
    feature_vector[9] = scanpath_area(fixations)  ##scanpath area
    feature_vector[10] = region_of_interest(gaze)  ##regions of interest
    feature_vector[11] = inflection_count(saccades)  ##inflection count
    return feature_vector










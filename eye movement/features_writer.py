from features_extraction import feature_vector
import numpy as np
import csv
label = np.zeros([42])
f = np.zeros([210, 13])
i = 16
filename = 'Participant' + str(i)
path_gaze = 'data' + '/' + filename + '/' + 'gaze_positions.csv'
path_events = 'data' + '/' + filename + '/' + 'events.csv'
gaze = np.genfromtxt(path_gaze, delimiter=',', skip_header=1)
events = np.genfromtxt(path_events, delimiter=',', skip_header=1, dtype=str)
y_label = i + 1
gaze1 = gaze[500:600]
events1 = events[500:600]
m = feature_vector(gaze1, events1)

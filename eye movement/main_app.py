from fixation_saccade_classification import classification
from features_extraction import feature_vector
import numpy as np
from video_processing import video_processing
velocity_threshold = 3
path = 'output.MP4'
gaze = video_processing(path)
events = classification(gaze, velocity_threshold)
gaze = np.nan_to_num(gaze)
v = feature_vector(gaze, events)

from real_time_video_recording import video_recording
from fixation_saccade_classification import classification
from features_extraction import feature_vector
import numpy as np
velocity_threshold = 4.5
gaze = video_recording()
events = classification(gaze, velocity_threshold)
gaze = np.nan_to_num(gaze)
v = feature_vector(gaze, events)

print(gaze)
print(events)

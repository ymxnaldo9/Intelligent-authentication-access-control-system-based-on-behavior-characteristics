import numpy as np
import math
def peak_velocity(gaze):
    length = len(gaze)
    if length > 2:
        horizonial_velocity = np.zeros(length - 2)
        vertical_velocity = np.zeros(length - 2)
        vectorial_velocity = np.zeros(length - 2)
        for i in range(1, length - 1):
            horizonial_velocity[i - 1] = (gaze[i + 1][1] - gaze[i - 1][1]) / (gaze[i + 1][0] - gaze[i - 1][0])
            vertical_velocity[i - 1] = (gaze[i + 1][2] - gaze[i - 1][2]) / (gaze[i + 1][0] - gaze[i - 1][0])
            vectorial_velocity[i - 1] = np.sqrt(np.square(horizonial_velocity[i - 1]) + np.square(vertical_velocity[i - 1]))
        peak_velocity = np.max(vectorial_velocity)
    else:
        peak_velocity = 0
    return peak_velocity
def central_point(gaze):
    t = np.mean(gaze, axis = 0)
    return (t[1], t[2])
def get_bottom_point(points):
    min_index = 0
    n = len(points)
    for i in range(0, n):
        if points[i][1] < points[min_index][1] or (points[i][1] == points[min_index][1] and points[i][0] < points[min_index][0]):
            min_index = i
    return min_index
def sort_polar_angle_cos(points, center_point):
    n = len(points)
    cos_value = []
    rank = []
    norm_list = []
    for i in range(0, n):
        point_ = points[i]
        point = [point_[0] - center_point[0], point_[1] - center_point[1]]
        rank.append(i)
        norm_value = math.sqrt(point[0] * point[0] + point[1] * point[1])
        norm_list.append(norm_value)
        if norm_value == 0:
            cos_value.append(1)
        else:
            cos_value.append(point[0] / norm_value)
    for i in range(0, n - 1):
        index = i + 1
        while index > 0:
            if cos_value[index] > cos_value[index - 1] or (
                    cos_value[index] == cos_value[index - 1] and norm_list[index] > norm_list[index - 1]):
                temp = cos_value[index]
                temp_rank = rank[index]
                temp_norm = norm_list[index]
                cos_value[index] = cos_value[index - 1]
                rank[index] = rank[index - 1]
                norm_list[index] = norm_list[index - 1]
                cos_value[index - 1] = temp
                rank[index - 1] = temp_rank
                norm_list[index - 1] = temp_norm
                index = index - 1
            else:
                break
    sorted_points = []
    for i in rank:
        sorted_points.append(points[i])
    return sorted_points
def vector_angle(vector):
    norm_ = math.sqrt(vector[0] * vector[0] + vector[1] * vector[1])
    if norm_ == 0:
        return 0
    angle = math.acos(vector[0] / norm_)
    if vector[1] >= 0:
        return angle
    else:
        return 2 * math.pi - angle
def coss_multi(v1, v2):
    return v1[0] * v2[1] - v1[1] * v2[0]
def graham_scan(points):
    bottom_index = get_bottom_point(points)
    bottom_point = points.pop(bottom_index)
    sorted_points = sort_polar_angle_cos(points, bottom_point)
    m = len(sorted_points)
    if m < 2:
        print("点的数量过少，无法构成凸包")
        return
    stack = []
    stack.append(bottom_point)
    stack.append(sorted_points[0])
    stack.append(sorted_points[1])
    for i in range(2, m):
        length = len(stack)
        top = stack[length - 1]
        next_top = stack[length - 2]
        v1 = [sorted_points[i][0] - next_top[0], sorted_points[i][1] - next_top[1]]
        v2 = [top[0] - next_top[0], top[1] - next_top[1]]
        while coss_multi(v1, v2) >= 0:
            stack.pop()
            length = len(stack)
            top = stack[length - 1]
            next_top = stack[length - 2]
            v1 = [sorted_points[i][0] - next_top[0], sorted_points[i][1] - next_top[1]]
            v2 = [top[0] - next_top[0], top[1] - next_top[1]]
        stack.append(sorted_points[i])
    stack = np.array(stack)
    return stack
def area_calculation(vertex_set):
    l = len(vertex_set)
    if l > 2:
        sum1 = 0
        sum2 = 0
        for i in range(l - 1):
            sum1 += vertex_set[i][0] * vertex_set[i + 1][1]
            sum2 += vertex_set[i][1] * vertex_set[i + 1][0]
        sum1 += vertex_set[l - 1][0] * vertex_set[0][1]
        sum2 += vertex_set[l - 1][1] * vertex_set[0][0]
        area = 0.5 * abs(sum1 - sum2)
    else:
        area = 0
    return area




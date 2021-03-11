import csv
import numpy as np
import pandas as pd
from skimage import io, color
from skimage.transform import resize
from sklearn.cluster import KMeans

# Configuration Variables - feel free to modify these
imageFile = 'geo.png'
n_colors = 16
finalWidth = 160
finalHeight = 160
penColors = ['red','orange','yellow','green','cyan','blue','purple','magenta','black','white','brown','grey']
outputFile = 'output.csv'

# Start - read the image file
original = io.imread(imageFile)

res = resize(original, (finalHeight, finalWidth), order=3) # 0=nearest, 3=BiCubic
oshape = res.shape

# PNG files have an Alpha channel that we do not need. Strip it.
if oshape[2] == 4:
    res = color.rgba2rgb(res)

# Convert to array of 3-color values (r,g,b)
arr = res.reshape((-1, 3))

# Use the KMeans algorithm to find the closest n_colors in the image
kmeans = KMeans(n_clusters=n_colors, random_state=42).fit(arr)
labels = kmeans.predict(arr)
centers = kmeans.cluster_centers_

#reduce the number of colors to what is stated in n_colors
less_colors = centers[labels].reshape(res.shape)

# reduce the array to the unique colors, which should be 16 values
cmap, color_index = np.unique(less_colors.reshape(-1, less_colors.shape[2]), axis=0, return_inverse=True)

# Convert to a Hue Saturation Value (HSV) array
hsvMap = color.rgb2hsv(cmap)

# Break out the HSV values into separate variables for Hue, Saturation, and Value
hue_img = hsvMap[:, 0]
saturation_img = hsvMap[:, 1]
value_img = hsvMap[:, 2]

# Assign color names to the values based on Hue
df = pd.DataFrame({'hue':hue_img})
df['new_colors'] = pd.cut(x=df['hue'],bins=[0, 0.053, 0.1083, 0.1805, 0.4444, 0.5277, 0.7138, 0.8055, 0.8888, 1],
                        labels=['red','orange','yellow','green','cyan','blue','purple','magenta','red'],
                        ordered=False, include_lowest=True)

# Replace Hue color names where Value or Saturation indicate the color is black, white, and brown
df['new_colors'] = np.where((value_img < 0.25), 'black', df.new_colors)
df['new_colors'] = np.where(((saturation_img < 0.05) & (value_img > 0.95)), 'white', df.new_colors)
df['new_colors'] = np.where(((hue_img > 0.53) & (hue_img < 0.1083) & (value_img > 0.25) & (value_img < 0.65)), 'brown', df.new_colors)

# Map each color index value to the corresponding color name (category)
penNameData = [df['new_colors'][v] for v in color_index]
outputPenNames = np.array(penNameData).reshape((-1, finalWidth))

# Assign pen colors to pen numbers
cf = dict([(y, x) for x,y in enumerate(penColors, 1)])
outputPenNumbers = [cf[v] for v in penNameData]

with open(outputFile, 'w', newline='\n') as csvfile:
    csvwriter = csv.writer(csvfile)
    csvwriter.writerows(outputPenNames)
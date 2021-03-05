import numpy as np
import pandas as pd
from skimage import io, color
from skimage.transform import resize
from sklearn.cluster import KMeans

original = io.imread('geo.png')
n_colors = 16
res = resize(original, (160, 160))

# PNG files have an Alpha channel that we do not need. Strip it.
res4 = original[:,:,:-1]

#res = cv2.resize(original, dsize=(160, 160), interpolation=cv2.INTER_CUBIC)
                            #width and height, change this for different final ones

arr = res4.reshape((-1, 3))
kmeans = KMeans(n_clusters=n_colors, random_state=42).fit(arr)
labels = kmeans.labels_
centers = kmeans.cluster_centers_
less_colors = centers[labels].reshape(res4.shape).astype('uint8')
#reduces the number of colors to what is stated in n_colors


#io.imshow(less_colors)
#io.show()

#input()  # pause until user presses a key
hsv_img = color.rgb2hsv(less_colors)

cmap = np.unique(hsv_img.reshape(-1, hsv_img.shape[2]), axis=0)
print(cmap)

hue_img = cmap[:, 0]
saturation_img = cmap[:, 1]
value_img = cmap[:, 2]

df = pd.DataFrame({'hue':hue_img})
df['new_colors'] = pd.cut(x=df['hue'],bins=[0, 0.053, 0.1083, 0.1805, 0.4444, 0.5277, 0.7138, 0.8055, 0.8888, 1],
                        labels=['red','orange','yellow','green','cyan','blue','purple','magenta','red'],
                        ordered=False, include_lowest=True)


df['new_colors'] = np.where((value_img < 0.25), 'black', df.new_colors)
df['new_colors'] = np.where(((saturation_img < 0.05) & (value_img > 0.95)), 'white', df.new_colors)
df['new_colors'] = np.where(((hue_img > 0.53) & (hue_img < 0.1083) & (value_img > 0.25) & (value_img < 0.65)), 'brown', df.new_colors)


print(df)
io.imshow(hsv_img)
io.show()
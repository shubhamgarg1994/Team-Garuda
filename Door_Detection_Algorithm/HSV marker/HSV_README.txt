 HSV color space is the most suitable color space for color based image segmentation.

Hue values of basic colors
Orange  0-22
Yellow 22- 38
Green 38-75
Blue 75-130
Violet 130-160
Red 160-179

After thresholding the image, we will see small white isolated objects here and there. It may be because of noises in the image or the actual small objects which have the same color as our main object. These unnecessary small white patches can be eliminated by applying morphological opening. Morphological opening can be achieved by a erosion, followed by the dilation with the same structuring element.

How to Find Exact Range for 'Hue', 'Saturation' and 'Value' for a Given Object??

1. Track bars should be placed in a separate window so that ranges for HUE, SATURATION and VALUE can be adjusted. And set the initial ranges for HUE, SATURATION and VALUE as 0-179, 0-255 and 0-255 respectively. So, we will see a complete white image in the 'Control' window.

2. First, adjust 'LowH' and 'HighH' track bars so that the gap between 'LowH' and 'HighH' is minimized. Here you have to be careful that white area in 'Ball' window that represents the object should not be affected, while you are trying to minimize the gap.

3.Repeat the step 2 for 'LowS' and 'HighS' trackbars

4.Repeat the step2 for 'LowV' and 'HighV' trackbars
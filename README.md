# Team-Garuda
Investigation of possible exits and window in an Indoor environment for Quadcopter Navigation (IDL Lab, IIT Bombay)


In the last few decades, Unmanned Aerial Vehicles (UAVs) have become more commonly used for many applications. The need for aircraft with greater maneuverability and hovering ability has led to current rise in quadcopter research. The four-rotor design allows quadcopters to be relatively simple in design yet highly reliable and maneuverable. 
The purpose of our project is to create a Aakash controlled quadcopter with onboard image processing. Our quadcopter can be used for indoor and outdoor navigation. In outdoor navigation we can traverse from one way-point to another way-point using an on board GPS. But, in indoor navigation, we can't reach to the GPS satellites. So, we are detecting doors and obstacle using Raspberry pi with an onboard raspberry pi camera. We have even provided an USB Camera with it for live video streaming on our Ground Control Station (GCS).
The scheduler program arranges the following tasks: Controller input, sensor data received from the accelerometer, Gyroscope, and Magnetometer. The accelerometer/gyroscope and magnetometer both uses I2C protocol to communicate with the main controller (Atmega2560). We have implemented a 9-axis IMU for the sensor fusion using a Complementary filter. 

Presentation: https://www.youtube.com/watch?v=0966YNqcisg

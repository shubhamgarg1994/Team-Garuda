#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>

using namespace std;


int main()
{

CvMoments moments;
double M00, M01, M10;
int cx,cy;
CvPoint CircleCenter;
int Radius;
CvScalar Color;
int Thickness;
int Shift;
 //show the original image
int iLowH = 163;
 int iHighH = 179;

  int iLowS = 74; 
 int iHighS = 255;

  int iLowV = 57;
 int iHighV = 255;

 cvNamedWindow("Control",CV_WINDOW_AUTOSIZE);
 //Create trackbars in "Control" window
 cvCreateTrackbar("LowH", "Control",  &iLowH, 179); //Hue (0 - 179)
 cvCreateTrackbar("HighH", "Control", &iHighH, 179);

  cvCreateTrackbar("LowS", "Control", &iLowS, 255); //Saturation (0 - 255)
 cvCreateTrackbar("HighS", "Control", &iHighS, 255);

  cvCreateTrackbar("LowV", "Control", &iLowV, 255); //Value (0 - 255)
 cvCreateTrackbar("HighV", "Control", &iHighV, 255);

 CvCapture* capture=cvCreateCameraCapture(-1);
  IplImage* img; 
  IplImage* img_HSV;
  IplImage* imgThresholded;

  CvPoint pt1,pt2;

  int temp_x[10]={0};
  int temp_y[10]={0};

  int final_x[4]={0};
  int final_y[4]={0};

  int j=0,i=0,z=0;
  int count=0;

  int cx1=0,cy1=0;
  int p=0,q=0;
  int max=0;
  int may=0;
  int valuex=0;
  int valuey=0;

while(1)
 {
 
  img=cvQueryFrame(capture);
  img_HSV = cvCreateImage(cvGetSize(img),8,3); 
  imgThresholded = cvCreateImage(cvGetSize(img),8,1);

  //converting the original image into grayscale

 cvCvtColor(img,img_HSV,CV_BGR2HSV);
 cvInRangeS(img_HSV, cvScalar(iLowH, iLowS, iLowV), cvScalar(iHighH, iHighS, iHighV), imgThresholded); //Threshold the image

 //morphological opening (remove small objects from the foreground)
  cvErode(imgThresholded,imgThresholded,cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_ELLIPSE));
  cvDilate(imgThresholded,imgThresholded,cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_ELLIPSE)); 

   //morphological closing (fill small holes in the foreground)
  cvDilate(imgThresholded, imgThresholded,cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_ELLIPSE)); 
  cvErode(imgThresholded, imgThresholded,cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_ELLIPSE));

 //  cvShowImage("Thresholded Image", imgThresholded); //show the thresholded image
 // cvShowImage("Original", img); //show the original image

 CvSeq* contours;  //hold the pointer to a contour in the memory block
 CvSeq* result;   //hold sequence of points of a contour
 CvMemStorage *storage = cvCreateMemStorage(0); //storage area for all contours

 cvFindContours(imgThresholded, storage, &contours, sizeof(CvContour),CV_RETR_LIST,CV_CHAIN_APPROX_SIMPLE,cvPoint(0,0));
  
  //iterating through each contour
 
  while(contours)
 {
     //obtain a sequence of points of contour, pointed by the variable 'contour'
     result = cvApproxPoly(contours, sizeof(CvContour), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0);  
    
	 if(result->total==4 &&
                    fabs(cvContourArea(result,CV_WHOLE_SEQ)) > 100 &&
                    cvCheckContourConvexity(result))
     {
         //iterating through each point
         CvPoint *pt[4];
         for(int i=0;i<4;i++)
         pt[i] = (CvPoint*)cvGetSeqElem(result,i);

cvMoments(contours,&moments);
M00 = cvGetSpatialMoment(&moments,0,0);
M10 = cvGetSpatialMoment(&moments,1,0);
M01 = cvGetSpatialMoment(&moments,0,1);
cx = (int)(M10/M00);
cy = (int)(M01/M00);
		 cvLine(img, *pt[0], *pt[1], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[1], *pt[2], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[2], *pt[3], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[3], *pt[0], cvScalar(0,255,0),4); //green
//printf("The coordinates are:(%d %d)\n",cx,cy);
/*
CircleCenter=cvPoint(cx,cy);
Radius=3;
Color=CV_RGB(255,0,0);
Thickness=3;
cvCircle(img,CircleCenter,Radius,Color,Thickness,CV_AA,0);
*/
	 //Printing the Coordinates of the points
	 //Very Important
	
	// printf("Coordinates of center :%d %d \n",cx,cy);
	 
	 for(i=0;i<10;i++)
	 {
		 if(cx < temp_x[i]+80 && cx > temp_x[i]-80 &&  cy < temp_y[i]+120 && cy > temp_y[i]-120)
			count++;
	 }
	
	 if(count==0)
	 {
	 final_x[j]=cx;
	 final_y[j]=cy;
	 j++;
	 }
	 
	 else
	 count=0;

	 if(j>=4)
	 {
	 j=0;
	 for(j=0;j<4;j++)
	 {
	 printf("center %d: %d,%d\n",j+1,final_x[j],final_y[j]);
	 }
	 j=1;

	 for(j=1;j<4;j++)
	 {
		 p=final_x[0]-final_x[j];
		 q=final_y[0]-final_y[j];
		 
		 if(p<0)
			 p=-p;
		 if(q<0);
		 q=-q;

		 if(p>max)
		 {
		 max=p;
		 valuex=j;
		 }
		 if(q>may)
		 {
		 may=q;
		 valuey=j;
		 }
	 }
	 cx1=(final_x[0] + final_x[valuex])/2;
	 cy1=(final_y[0] + final_y[valuey])/2;
	 
	 //printf("The coordinates are:(%d %d)\n",cx1,cy1);

/*	  
CircleCenter=cvPoint(cx1,cy1);
Radius=3;
Color=CV_RGB(255,0,0);
Thickness=3;
cvCircle(img,CircleCenter,Radius,Color,Thickness,CV_AA,0);
cvWaitKey(10);
*/
	 pt1=cvPoint(final_x[0],final_y[0]);
	 pt2=cvPoint(final_x[1],final_y[1]);
	 cvLine(img, pt1, pt2, cvScalar(0,255,0),4);

j=0;
	
 }

	 /*
	 printf("Coordinates of Point 1 :%d %d \n", pt[0]->x,pt[0]->y);
	 printf("Coordinates of Point 2 :%d %d \n", pt[1]->x,pt[1]->y);
	 printf("Coordinates of Point 3 :%d %d \n", pt[2]->x,pt[2]->y);
	 printf("Coordinates of Point 4 :%d %d \n", pt[3]->x,pt[3]->y);
	 */
	 
        //drawing lines around the quadrilateral
    //     cvLine(img, *pt[0], *pt[1], cvScalar(0,255,0),4); //green
    //     cvLine(img, *pt[1], *pt[2], cvScalar(0,255,0),4); //green
    //     cvLine(img, *pt[2], *pt[3], cvScalar(0,255,0),4); //green
    //     cvLine(img, *pt[3], *pt[0], cvScalar(0,255,0),4); //green
	//	 cvLine(img, *pt[0], *pt[2], cvScalar(0,255,255),4); //yellow
	//	 cvLine(img, *pt[1], *pt[3], cvScalar(0,255,255),4); //yellow
		/*
		 cvLine(img, *pt[4], *pt[5], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[5], *pt[6], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[6], *pt[7], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[7], *pt[0], cvScalar(0,255,0),4); //green
		// cvLine(img, *pt[0], *pt[2], cvScalar(0,255,255),4); //yellow
		// cvLine(img, *pt[1], *pt[3], cvScalar(0,255,255),4); //yellow
		 
		 
		 cvLine(img, *pt[0], *pt[1], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[1], *pt[2], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[2], *pt[3], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[3], *pt[0], cvScalar(0,255,0),4); //green
		 cvLine(img, *pt[0], *pt[2], cvScalar(0,255,255),4); //yellow
		 cvLine(img, *pt[1], *pt[3], cvScalar(0,255,255),4); //yellow

		 */
		 temp_x[z]=cx;
		 temp_y[z]=cy;
		 z++;
		 if(z>=10)
			 z=0;
			 
	 }

	  contours = contours->h_next;
 }
 
		//cvShowImage("Thresholded image",imgThresholded);
		cvShowImage("Original",img);

        if (cvWaitKey(30) == 27) //wait for 'esc' key press for 30ms. If 'esc' key is pressed, break loop
       {
            cout << "esc key is pressed by user" << endl;
            break; 
       }
   
}
	 //cleaning up
  cvDestroyAllWindows();
  cvReleaseCapture(&capture);

   return 0;
}


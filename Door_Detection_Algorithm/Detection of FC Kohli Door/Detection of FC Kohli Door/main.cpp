#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <stdio.h>


using namespace std;
using namespace cv;

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


    CvCapture* capture=cvCaptureFromFile("p.mp4");
  
	IplImage* img = cvQueryFrame(capture);
	
	CvSize size = cvSize((int)cvGetCaptureProperty(capture,CV_CAP_PROP_FRAME_WIDTH),(int)cvGetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT));

	CvVideoWriter *writer = cvCreateVideoWriter("out.avi",CV_FOURCC('D','I','V','X'),15,size);
  //converting the original image into grayscale
 IplImage* imgGrayScale = cvCreateImage(cvGetSize(img), 8, 1);

 while((img = cvQueryFrame(capture))!= NULL)
 {
 cvCvtColor(img,imgGrayScale,CV_BGR2GRAY);
  //thresholding the grayscale image to get better results
 cvThreshold(imgGrayScale,imgGrayScale,0,255,THRESH_OTSU | THRESH_BINARY_INV);
	
 CvSeq* contours;  //hold the pointer to a contour in the memory block
 CvSeq* result;   //hold sequence of points of a contour
 CvMemStorage *storage = cvCreateMemStorage(0); //storage area for all contours

 //finding all contours in the image
 cvFindContours(imgGrayScale, storage, &contours, sizeof(CvContour), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

 //iterating through each contour
 while(contours)
 {
     //obtain a sequence of points of contour, pointed by the variable 'contour'
     result = cvApproxPoly(contours, sizeof(CvContour), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0);

      //if there are 4 vertices in the contour(It should be a quadrilateral)
     if(result->total == 4 &&
                    fabs(cvContourArea(result,CV_WHOLE_SEQ)) > 10000 &&
                    cvCheckContourConvexity(result) )
     {
         //iterating through each point
         CvPoint *pt[4];
         for(int i=0;i<4;i++)
		 {
             pt[i] = (CvPoint*)cvGetSeqElem(result, i);
         }

cvMoments(contours,&moments);
M00 = cvGetSpatialMoment(&moments,0,0);
M10 = cvGetSpatialMoment(&moments,1,0);
M01 = cvGetSpatialMoment(&moments,0,1);
cx = (int)(M10/M00);
cy = (int)(M01/M00);

//printf("The coordinates are:(%d %d)\n",cx,cy);

    CircleCenter=cvPoint(cx,cy);
    Radius=3;
    Color=CV_RGB(255,0,0);
    Thickness=3;
    cvCircle(img,CircleCenter,Radius,Color,Thickness,CV_AA,0);

	 //Printing the Coordinates of the points
	 //Very Important
	 /*
	 printf("Coordinates of Point 1 :%d %d \n", pt[0]->x,pt[0]->y);
	 printf("Coordinates of Point 2 :%d %d \n", pt[1]->x,pt[1]->y);
	 printf("Coordinates of Point 3 :%d %d \n", pt[2]->x,pt[2]->y);
	 printf("Coordinates of Point 4 :%d %d \n", pt[3]->x,pt[3]->y);
	 */

         //drawing lines around the quadrilateral
         cvLine(img, *pt[0], *pt[1], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[1], *pt[2], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[2], *pt[3], cvScalar(0,255,0),4); //green
         cvLine(img, *pt[3], *pt[0], cvScalar(0,255,0),4); //green
		 cvLine(img, *pt[0], *pt[2], cvScalar(0,255,255),4); //yellow
		 cvLine(img, *pt[1], *pt[3], cvScalar(0,255,255),4); //yellow
     }
	
  //obtain the next contour
     contours = contours->h_next;
 }


  //show the image in which identified shapes are marked
 cvNamedWindow("Tracked",CV_WINDOW_AUTOSIZE);
 cvWriteFrame(writer,img);
 cvShowImage("Tracked",img);
 char c = cvWaitKey( 33 );
    if( c == 27 ) break;

 }

  //cleaning up
 cvReleaseVideoWriter(&writer);
 cvDestroyAllWindows();
 cvReleaseCapture(&capture);
  return 0;
}


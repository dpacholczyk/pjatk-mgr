package com.fixus.towerdefense.analyze;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class FrameAnalyzer {
	public static Point tl;
	public static Point tr;
	public static Point bl;
	public static Point br;
	public static Mat frame;
	
	public static Mat analyzeFrame(Mat aInputFrame) {

    	// klasyfikacja obiektów
    	
//    	// Create a grayscale image
//        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);
// 
// 
//        MatOfRect objects = new MatOfRect();
// 
// 
//        // Use the classifier to detect faces
//        if (cascadeClassifier != null) {
//            cascadeClassifier.detectMultiScale(grayscaleImage, objects, 1.1, 1, 2,
//                    new Size(absoluteObjectSize, absoluteObjectSize), new Size());
//        }
// 
// 
//        Rect[] facesArray = objects.toArray();
//        for (int i = 0; i <facesArray.length; i++) {
//            Log.d("POSITION", "point 1: " + facesArray[i].tl());
//            Log.d("POSITION", "point 2: " + facesArray[i].br());
//        	
//        	Core.rectangle(aInputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
//        }
//		Point p1 = new Point(0, 0);
//		Point p2 = new Point(100, 100);
//		
//		FrameAnalyzer.p1 = p1;
//		FrameAnalyzer.p2 = p2;
 
        return aInputFrame;
	}
}

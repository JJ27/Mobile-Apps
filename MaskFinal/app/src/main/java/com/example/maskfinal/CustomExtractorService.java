package com.example.maskfinal;

import org.opencv.core.Mat;
import org.opencv.dnn.Net;

public interface CustomExtractorService {
    Net getConvertedNet(String clsModelPath, String tag);
    String getPredictedLabel(Mat inputImage, Net dnnNet);
}

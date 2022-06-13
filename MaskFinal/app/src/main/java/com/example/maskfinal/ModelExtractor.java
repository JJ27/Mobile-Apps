package com.example.maskfinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.osgi.OpenCVInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModelExtractor implements CustomExtractorService {
    private static final int IMG_WIDTH = 224;
    private static final int IMG_HEIGHT = 224;

    private static final double SCALING = 1 / 255.0;

    private static final Scalar AVG = new Scalar(0.485, 0.456, 0.406);
    private static final Scalar STD = new Scalar(0.229, 0.224, 0.225);

    private ArrayList<String> getLabels(){
        ArrayList<String> labels = new ArrayList<>();
        labels.add("No mask");
        labels.add("Mask");
        return labels;
    }

    public static Mat centerCrop(Mat inputImage) {
        int y1 = Math.round((inputImage.rows() - IMG_HEIGHT) / 2);
        int y2 = Math.round(y1 + IMG_HEIGHT);
        int x1 = Math.round((inputImage.cols() - IMG_WIDTH) / 2);
        int x2 = Math.round(x1 + IMG_WIDTH);
        Rect centerRect = new Rect(x1, y1, (x2 - x1), (y2 - y1));
        Mat croppedImage = new Mat(inputImage, centerRect);

        return croppedImage;
    }

    private Mat getPreprocessedImage(Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);

        // create empty Mat images for float conversions
        Mat imgFloat = new Mat(image.rows(), image.cols(), CvType.CV_32FC3);

        // convert input image to float type
        image.convertTo(imgFloat, CvType.CV_32FC3, SCALING);

        // resize input image
        Imgproc.resize(imgFloat, imgFloat, new Size(256, 256));

        // crop input image
        imgFloat = centerCrop(imgFloat);

        // prepare DNN input
        Mat blob = Dnn.blobFromImage(
                imgFloat,
                1.0, /* default scalefactor */
                new Size(IMG_WIDTH, IMG_HEIGHT), /* target size */
                AVG,  /* mean */
                true, /* swapRB */
                false /* crop */
        );

        // divide on std
        Core.divide(blob, STD, blob);

        return blob;
    }

    private String getPredictedClass(Mat classificationResult) {
        ArrayList<String> imgLabels = getLabels();
        if (imgLabels.isEmpty()) {
            return "Empty label";
        }
        // obtain max prediction result
        Core.MinMaxLocResult mm = Core.minMaxLoc(classificationResult);
        double maxValIndex = mm.maxLoc.x;
        return imgLabels.get((int) maxValIndex);
    }

    @Override
    public Net getConvertedNet(String clsModelPath, String tag) {
        Net convertedNet = Dnn.readNetFromONNX(clsModelPath);
        Log.i(tag, "Network was successfully loaded");
        return convertedNet;
    }

    @Override
    public String getPredictedLabel(Mat inputImage, Net dnnNet) {
        // preprocess input frame
        Mat inputBlob = getPreprocessedImage(inputImage);
        // set OpenCV model input
        dnnNet.setInput(inputBlob);
        // provide inference
        Mat classification = dnnNet.forward();
        return getPredictedClass(classification);
    }
}

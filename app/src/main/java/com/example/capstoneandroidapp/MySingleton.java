package com.example.capstoneandroidapp;

import android.graphics.Bitmap;

import com.example.capstoneandroidapp.tflite.SimilarityClassifier;
import com.example.capstoneandroidapp.tflite.TFLiteObjectDetectionAPIModel;

import java.util.List;

import static com.example.capstoneandroidapp.DetectorActivity.TF_OD_API_INPUT_SIZE;
import static com.example.capstoneandroidapp.DetectorActivity.TF_OD_API_IS_QUANTIZED;
import static com.example.capstoneandroidapp.DetectorActivity.TF_OD_API_LABELS_FILE;
import static com.example.capstoneandroidapp.DetectorActivity.TF_OD_API_MODEL_FILE;

public class MySingleton {
    private static MySingleton _instance;
    SimilarityClassifier detector;

    private MySingleton(){}

    public static MySingleton getInstance(){
        if(_instance==null) _instance = new MySingleton();
        return _instance;
    }

    public SimilarityClassifier getSimilarityClassifier(){
        return detector;
    }
    
    public void SetDetector(SimilarityClassifier newClassifier) {
        detector = newClassifier;
    }
    public List<SimilarityClassifier.Recognition> doRecognizeImage(Bitmap bitmap){
        List<SimilarityClassifier.Recognition> list = detector.recognizeImage(bitmap, true);
        return list;
    }

}
//MySingleton.getInstance().getSimilarityClassifier()
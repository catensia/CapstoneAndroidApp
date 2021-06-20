package com.example.capstoneandroidapp;

import android.app.Application;
import com.example.capstoneandroidapp.tflite.SimilarityClassifier;

class detectorMethods extends DetectorActivity{


}

public class myGlobal extends Application {
    private int state;
    private SimilarityClassifier GlobalDetector;

    @Override
    public void onCreate() {
        state = 0;
        super.onCreate();
    }
    @Override
    public void onTerminate(){
        super.onTerminate();
    }

    public void setState(int state){
        this.state=state;
    }

    public int getState(){
        return state;
    }

    public SimilarityClassifier getSimilarityClassifier() {
        return GlobalDetector;
    }

}

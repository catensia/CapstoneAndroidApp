package com.example.capstoneandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.capstoneandroidapp.env.Logger;
import com.example.capstoneandroidapp.tflite.SimilarityClassifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

class RegisterDetectionActivity extends DetectorActivity{

    public static void doSomestuff(){
        //is it right...? possible to "access" but to "edit"?
        //Worth giving a try... DetectorActivity.detector
        //DetectorActivity.detector

    }

}

public class RegisterActivity extends AppCompatActivity {



    //Uses MLkit - for face detection bitmaps
    private Integer sensorOrientation;
    private static final Logger LOGGER = new Logger();
    private static final DetectorActivity.DetectorMode MODE = DetectorActivity.DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final int TF_OD_API_INPUT_SIZE = 112;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;
    private Bitmap portraitBmp = null;
    private Bitmap faceBmp = null;
    private SimilarityClassifier detector;
    private long lastProcessingTimeMs;
    private FaceDetector faceDetector;
    //Face detector options for manual registration
    FaceDetectorOptions options =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .build();


    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private EditText et_id, et_pass, et_name, et_age;
    private Button btn_register, btn_picture;
    private ImageView iv_picture, iv_cropped;
    private boolean pictureExists = false;
    static final int REQUEST_IMAGE_CAPTURE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Executes on starting activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //searches for ID values
        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        iv_cropped=findViewById(R.id.iv_cropped);
        iv_picture = findViewById(R.id.iv_picture);
        btn_register = findViewById(R.id.btn_register);
        btn_register.setEnabled(false);


        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Present input in edittext
                String userID = et_id.getText().toString();
                String userPass = et_pass.getText().toString();
                String userName = et_name.getText().toString();
                int userAge = Integer.parseInt(et_age.getText().toString());

                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success){


                                Toast.makeText(getApplicationContext(), "Finished Registration", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed Registration", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPass, userName,userAge, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });

        btn_picture = findViewById(R.id.btn_picture);
        btn_picture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePicture.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK && intent.hasExtra("data")){
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                    if(bitmap!=null){
                        iv_picture.setImageBitmap(bitmap);
                        btn_register.setEnabled(true);
                        FaceDetector faceDetector = FaceDetection.getClient(options);

                        InputImage image = InputImage.fromBitmap(bitmap, 0);
                        Task<List<Face>> result =
                                faceDetector.process(image)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<List<Face>>() {
                                                    @Override
                                                    public void onSuccess(List<Face> faces) {
                                                        // Task completed successfully
                                                        // ...
                                                        int facenum = faces.size();
                                                        if(facenum==1) {
                                                            for (Face face : faces) {
                                                                Rect rect = face.getBoundingBox();
                                                                Bitmap cropped = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
                                                                iv_cropped.setImageBitmap(cropped);
                                                                iv_cropped.setScaleType(ImageView.ScaleType.FIT_CENTER);

                                                            }
                                                        }
                                                        if(facenum!=1) {
                                                            btn_register.setEnabled(false);
                                                            iv_cropped.setImageBitmap(null);

                                                        }
                                                        Toast.makeText(getApplicationContext(), ""+facenum, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Task failed with an exception
                                                        // ...
                                                    }
                                                });
                    }
                }
        }
    }
}
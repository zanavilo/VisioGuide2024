package org.tensorflow.lite.examples.detection;

import static android.Manifest.permission.CAMERA;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.tensorflow.lite.examples.detection.Location.LocationActivity;
import org.tensorflow.lite.examples.detection.ObjectDetection.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class OCRReader extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    float x1, x2, y1, y2;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    private static TextToSpeech textToSpeech;
    private String stringResult = null;
    static String Readmessage;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mVoiceInputTv = (TextView) findViewById(R.id.textView);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PackageManager.PERMISSION_GRANTED);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.CANADA);
                    textToSpeech.setSpeechRate(0.85f);
                    textToSpeech.speak("Swipe right and say yes to read, say back to return, or swipe left to listen again.", TextToSpeech.QUEUE_ADD, null);
                }
            }
        });
        mVoiceInputTv = (TextView) findViewById(R.id.textView);
    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    textToSpeech.speak(stringResult, TextToSpeech.QUEUE_FLUSH, null);
                    textToSpeech.speak("Swipe right and say yes to read, say back to return, or swipe left to listen again.", TextToSpeech.QUEUE_ADD, null);

                } else if (x1 > x2) {
                    startVoiceInput();
                }
                break;
        }
        return false;
    }


    private void textRecognizer() {
        Toast.makeText(OCRReader.this, "Tap on the screen and listen ", Toast.LENGTH_SHORT).show();
        textToSpeech.speak(" Tap on the screen take a picture of any text with your device and listen", TextToSpeech.QUEUE_FLUSH, null);
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView = findViewById(R.id.surfaceView);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textToSpeech.speak("Image is clearly visible tap on the screen", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 5000);


        Context context = this;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    private void capture() {
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < sparseArray.size(); ++i) {
                    TextBlock textBlock = sparseArray.valueAt(i);
                    if (textBlock != null && textBlock.getValue() != null) {
                        stringBuilder.append(textBlock.getValue() + " ");
                    }
                }

                final String stringText = stringBuilder.toString();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stringResult = stringText;
                        resultObtained();
                    }
                });
            }
        });
    }

    private void resultObtained() {
        setContentView(R.layout.activity_main2);
        TextView textView = findViewById(R.id.textView);
        textView.setText(stringResult);
        textToSpeech.speak(stringResult, TextToSpeech.QUEUE_FLUSH, null, null);
        textToSpeech.speak("Swipe left to listen again. or swipe right and say what you want", TextToSpeech.QUEUE_ADD, null);
    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                    if (mVoiceInputTv.getText().toString().contains("read")) {
                        Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                        startActivity(intent);
                    } else if (mVoiceInputTv.getText().toString().contains("object")) {
                        onPause();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        mVoiceInputTv.setText(null);
                    } else if (mVoiceInputTv.getText().toString().contains("back")) {
                        Intent intent = new Intent(getApplicationContext(), Features.class);
                        startActivity(intent);
                        mVoiceInputTv.setText(null);
                    } else if (mVoiceInputTv.getText().toString().contains("back")) {
                        mVoiceInputTv.setText(null);
                        startActivity(new Intent(this, Features.class));
                    } else if (mVoiceInputTv.getText().toString().contains("exit")) {
                        finish();
                    } else if (mVoiceInputTv.getText().toString().contains("yes")) {
                        setContentView(R.layout.surface);
                        surfaceView = findViewById(R.id.surfaceView);
                        surfaceView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                OCRReader.this.capture();
                            }
                        });
                        textRecognizer();
                        mVoiceInputTv.setText(null);
                    } else if (mVoiceInputTv.getText().toString().contains("no")) {
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textToSpeech.speak("You are in main menu. Swipe right to listen to the features of the app or swipe left and say what you want", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }, 1000);
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    }
                } else {
                    textToSpeech.speak("Do not understand Swipe left Say again", TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            }
        }
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }
}
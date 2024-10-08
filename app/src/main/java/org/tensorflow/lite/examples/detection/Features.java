package org.tensorflow.lite.examples.detection;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.examples.detection.Message.MessageReader;
import org.tensorflow.lite.examples.detection.Moneytransfer.Banktransfer;
import org.tensorflow.lite.examples.detection.Moneytransfer.phonetransfer;
import org.tensorflow.lite.examples.detection.Music.Music;
import org.tensorflow.lite.examples.detection.Navigation.Navigation;
import org.tensorflow.lite.examples.detection.ObjectDetection.MainActivity;
import org.tensorflow.lite.examples.detection.QRProduct.QRactivity;
import org.tensorflow.lite.examples.detection.Reminder.Reminder;

import java.util.ArrayList;
import java.util.Locale;

public class Features extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    float x1, x2, y1, y2;
    static String Readmessage;
    private static TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.85f);
                    textToSpeech.speak("Say read for read. Object detection to detect objects. Calculator to perform mathematical calculations. Weather to get weather details. Navigation to navigate. Battery to get battery percentage. Time and date for time and date. Say back to return to Home screen. Say exit for closing the application. Swipe left and say what you want ", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);


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
                if (x1 > x2) {
                    textToSpeech.stop();
                    startVoiceInput();
                }
                break;
        }
        return false;
    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mVoiceInputTv.setText(result.get(0));

                if (mVoiceInputTv.getText().toString().contains("read") && !mVoiceInputTv.getText().toString().contains("message")) {
                    Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                /*} else if (mVoiceInputTv.getText().toString().contains("calculator")) {
                    Intent intent = new Intent(getApplicationContext(), Calculator.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);*/
                } else if (mVoiceInputTv.getText().toString().contains("time and date")) {
                    Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                /*} else if (mVoiceInputTv.getText().toString().contains("weather")) {
                    Intent intent = new Intent(getApplicationContext(), Weather.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);*/
                } else if (mVoiceInputTv.getText().toString().contains("object")) {
                    onPause();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                /*} else if (mVoiceInputTv.getText().toString().contains("message")) {
                    Intent intent = new Intent(getApplicationContext(), MessageReader.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);

                }  else if (mVoiceInputTv.getText().toString().contains("music")) {
                    Intent intent = new Intent(getApplicationContext(), Music.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);*/
                } else if (mVoiceInputTv.getText().toString().contains("back")) {
                    mVoiceInputTv.setText(null);
                    startActivity(new Intent(this, Home.class));
                /*} else if (mVoiceInputTv.getText().toString().contains("battery")) {
                    Intent intent = new Intent(getApplicationContext(), Battery.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);

                } else if (mVoiceInputTv.getText().toString().contains("bank transfer")) {
                    Intent i = new Intent(Features.this, Banktransfer.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("phone transfer")) {
                    Intent i = new Intent(Features.this, phonetransfer.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("scan the QR") || mVoiceInputTv.getText().toString().contains("QR")) {
                    Intent i = new Intent(Features.this, QRactivity.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("navigat")) {
                    Intent intent = new Intent(getApplicationContext(), Navigation.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);

                }
                if (mVoiceInputTv.getText().toString().contains("create reminder")||mVoiceInputTv.getText().toString().contains("reminder")||mVoiceInputTv.getText().toString().contains("reminders")) {
                    Intent i = new Intent(Features.this, Reminder.class);
                    startActivity(i);*/
                }
                else if (mVoiceInputTv.getText().toString().contains("exit")) {
                    onPause();
                    finishAffinity();
                } else {
                    textToSpeech.speak("Do not understand Swipe left Say again", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    public void onDestroy() {
        if (mVoiceInputTv.getText().toString().contains("exit")) {
            finish();
        }
        super.onDestroy();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}


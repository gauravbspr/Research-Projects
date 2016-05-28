package com.example.shashank.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Shashank on 3/8/2016.
 */
public class MyService extends Service {

    private WindowManager windowManager;
    private FrameLayout layout;
    private LayoutInflater inflater;
    private WindowManager.LayoutParams params;
    private View myview;
    private MyCameraSurfaceView preview;
    private Camera myCamera;
    private int fullScreen=0;
    private ImageButton minimize;
    private Button start;
    Boolean flag;
    private MediaRecorder mediaRecorder;
    private Boolean recording;
    private String videoFilename;
    private final Handler handler = new Handler();
    public static final String BROADCAST_ACTION = "com.example.shashank.myapplication.MyService";


    private final IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        videoFilename = intent.getStringExtra("filename");
    }
    //For setting the filename dynamically
    public void SetFilename(String filename){
       videoFilename = filename;
    }


    @Override public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        recording = false;
        myview = inflater.inflate(R.layout.service_layout, null);
        layout = (FrameLayout) myview.findViewById(R.id.preview);
        minimize = (ImageButton) myview.findViewById(R.id.minimize);
        minimize.setVisibility(ImageButton.GONE);
        start = (Button) myview.findViewById(R.id.record);
        start.setVisibility(Button.GONE);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recording) {
                    if (prepareMediaRecorder()) {
                        try {
                            mediaRecorder.start();
                            Log.e("Camera------>", "working after");
                            recording = true;
                            start.setText("STOP");
                            handler.post(sendUpdates);
                        }catch (Exception e){
                            Log.getStackTraceString(e);
                        }
                    }
                }else{
                    mediaRecorder.stop(); // stop the recording
                    releaseMediaRecorder();
                    recording = false;
                    Log.e("Camera1------>", "working");
                    start.setText("START");
                    handler.post(sendUpdates);
                }

            }
        });
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.width = 500;
                params.height = 300;
                minimize.setVisibility(ImageButton.GONE);
                start.setVisibility(Button.GONE);
                windowManager.updateViewLayout(myview,params);
                fullScreen = 0;
            }
        });
        myCamera = Camera.open();
        if (myCamera == null) {
            Toast.makeText(getApplicationContext(), "Failed to start Camera", Toast.LENGTH_LONG).show();
        }
        myCamera.setDisplayOrientation(90);

        preview = new MyCameraSurfaceView(getApplicationContext(), myCamera);
        layout.addView(preview);

        params = new WindowManager.LayoutParams(500, 300,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 150;


        windowManager.addView(myview, params);

        myview.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.e("w","1");
                        flag = false;
                        return true;
                    case MotionEvent.ACTION_UP:
                        if(!flag&&fullScreen==0) {
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            params.height = WindowManager.LayoutParams.MATCH_PARENT;
                            windowManager.updateViewLayout(myview,params);
                            minimize.setVisibility(ImageButton.VISIBLE);
                            start.setVisibility(Button.VISIBLE);
                            fullScreen = 1;
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(myview, params);
                        Log.e("w","3");
                        flag = true;
                        return true;
                }
                return false;
            }
        });
    }

    private Runnable sendUpdates= new Runnable() {
        public void run() {
            Log.e("broadcast--->","working");
            DisplayLoggingInfo();
        }
    };

    private void DisplayLoggingInfo() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("status", recording.toString());
        intent.putExtra("filename",videoFilename);
        sendBroadcast(intent);
        Log.e("Receiver--->",""+recording.toString());
    }

    @SuppressLint("SdCardPath")
    private boolean prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        myCamera.unlock();
        mediaRecorder.setCamera(myCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String fname = videoFilename+".mp4";
        String filepath = Environment.getExternalStorageDirectory().getPath()+ "/"+fname;
        mediaRecorder.setOutputFile(filepath);
        mediaRecorder.setPreviewDisplay(preview.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            Log.e("exception1--->", Log.getStackTraceString(e));
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            Log.e("exception2--->", Log.getStackTraceString(e));
            return false;
        } catch (Exception e){
            Log.e("exception3--->", Log.getStackTraceString(e));
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            myCamera.lock(); // lock camera for later use
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myview != null) windowManager.removeView(myview);
    }
}


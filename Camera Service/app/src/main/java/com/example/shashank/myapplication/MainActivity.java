package com.example.shashank.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

   Boolean recording;
    Button ok,close;
    public final static int REQUEST_CODE = 0;
    private MyService serviceBinder;
    private String filename;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            serviceBinder = ((MyService.MyBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            serviceBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recording = false;
        Intent bindIntent = new Intent(MainActivity.this, MyService.class);
        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        filename = "vedio-"+String.valueOf(i1);
        bindIntent.putExtra("filename",filename);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
        startService(new Intent(MainActivity.this, MyService.class));

        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filename.equalsIgnoreCase("null"))
                    Toast.makeText(MainActivity.this,"Video is nessecary",Toast.LENGTH_LONG).show();
                else {
                    if (!recording) {
                        stopService(new Intent(MainActivity.this, MyService.class));
                        unbindService(mConnection);
                    } else
                        Toast.makeText(MainActivity.this, "Stop the video to continue", Toast.LENGTH_LONG).show();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
            if(!hasCameraPermissionGranted())
                requestCameraPermission();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateDate(intent);
        }
    };

    private void updateDate(Intent intent) {
        recording = Boolean.valueOf(intent.getStringExtra("status"));
        filename = intent.getStringExtra("filename");
        Log.e("Receiver--->",""+recording.toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(
                MyService.BROADCAST_ACTION));
    }

    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            }
    }

    private boolean hasCameraPermissionGranted(){
        return  ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
            }
        }


    }
}

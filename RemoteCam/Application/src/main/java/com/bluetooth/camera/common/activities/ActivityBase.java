/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.bluetooth.camera.common.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Base launcher activity, to handle most of the common plumbing for samples.
 */
public class ActivityBase extends AppCompatActivity {

    public static final String TAG = "ActivityBase";
    private static final int REQUEST_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissionGranted())
                requestPermissions();
        }
    }

    @Override
    protected  void onStart() {
        super.onStart();
    }

    private boolean hasPermissionGranted(){
        if(ContextCompat.checkSelfPermission(ActivityBase.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(ActivityBase.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(ActivityBase.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(ActivityBase.this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.BLUETOOTH_ADMIN},
                    REQUEST_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_ID){
            for (int i=0;i<grantResults.length;i++)
                if(grantResults[i] != 0){
                    Toast.makeText(ActivityBase.this,"All the permissions are neccesary.",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

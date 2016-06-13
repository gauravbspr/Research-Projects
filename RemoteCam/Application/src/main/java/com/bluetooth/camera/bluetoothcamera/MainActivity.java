


package com.bluetooth.camera.bluetoothcamera;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.bluetooth.camera.common.activities.ActivityBase;
import com.bluetooth.camera.R;


public class MainActivity extends ActivityBase {

    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            MainCameraFragment fragment = new MainCameraFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

}

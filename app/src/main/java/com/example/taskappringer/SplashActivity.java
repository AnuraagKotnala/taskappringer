package com.example.taskappringer;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.firebase.FirebaseApp;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.BuildConfig;

import java.util.HashMap;



public class SplashActivity extends AppCompatActivity {


    public static final String LATEST_APPLICATION_VERSION_KEY = "Latest_Application_Version";
    private AVLoadingIndicatorView mProgressBar;
    private RelativeLayout mRlContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransparentStatusBar(SplashActivity.this);
        setContentView(R.layout.activity_splash);

        try {
            FirebaseApp.initializeApp(this);
        }
        catch (Exception e) {
        }

        //This is default Map
        //Setting the Default Map Value with the current version code


        init();
     callNextScreen();
    }

    private void init() {

        mProgressBar = findViewById(R.id.pb_check_update);
        mRlContainer = findViewById(R.id.rl_container);
    }

    //https://firebasetutorials.com/firebase-remote-config-complete-android-tutorials-with-sample-app/

    /**
     * Used to check whether application update is available or not
     */





    private void callNextScreen() {

        Thread splashTread = new Thread() {
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } finally {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }

        };
        splashTread.start();
    }




    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public static void setTransparentStatusBar(Activity activity){
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}

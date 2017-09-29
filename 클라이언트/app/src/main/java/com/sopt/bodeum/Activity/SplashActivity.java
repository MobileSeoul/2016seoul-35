package com.sopt.bodeum.Activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.sopt.bodeum.R;

/**
 * Created by HOME on 2016-06-27.
 */
public class SplashActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //화면회전 금지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), login_Activity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MyTag", "로그인 액티비티 onResume가 실행됨");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MyTag", "로그인 액티비티 onPause가 실행됨");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MyTag", "로그인 액티비티 onDestroy가 실행됨");
    }
}

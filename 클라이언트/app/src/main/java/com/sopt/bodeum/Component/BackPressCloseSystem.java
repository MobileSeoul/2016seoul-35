package com.sopt.bodeum.Component;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by USER on 2016-07-07.
 */
public class BackPressCloseSystem {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseSystem(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed() {

        if (isAfter2Seconds()) {
            backKeyPressedTime = System.currentTimeMillis();
            // 현재시간을 다시 초기화

            toast = Toast.makeText(activity,
                    "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT);
            toast.show();

            return;
        }

        if (isBefore2Seconds()) {
            programShutdown();
            toast.cancel();
        }
    }

    private Boolean isAfter2Seconds() {
        return System.currentTimeMillis() > backKeyPressedTime + 2000;
        // 2초 지났을 경우
    }

    private Boolean isBefore2Seconds() {
        return System.currentTimeMillis() <= backKeyPressedTime + 2000;
        // 2초가 지나지 않았을 경우
    }

    private void programShutdown() {
        activity .moveTaskToBack(true);
        activity .finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
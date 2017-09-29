package com.sopt.bodeum.Component;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by softheaven on 2016-10-12.
 */

public class Constants {

    //폰트 설정 메서드
    public static void InitFont(Typeface font, ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView)
                ((TextView)child).setTypeface(font, Typeface.BOLD);
            else if (child instanceof ViewGroup)
                InitFont(font, (ViewGroup)child);
        }

    }
}

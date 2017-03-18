package com.pab.unpar.pklmobilekelompok;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class Utils extends AppCompatActivity {

    private static int themeCode;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_GRAY = 1;
    public final static int THEME_GREEN = 2;
    public final static int THEME_BLUE = 3;


    public static void changeToTheme(Activity activity, int code) {
        themeCode = code;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (themeCode) {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_GRAY:
                activity.setTheme(R.style.AppThemeGray);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppThemeGreen);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppThemeBlue);
                break;
        }
    }

    public static void onActivityCreateSetTheme(Activity activity, int code) {
        themeCode = code;
        onActivityCreateSetTheme(activity);
    }

}

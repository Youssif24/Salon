package com.saad.youssif.alsalonalmalaky;

import android.app.Application;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by youssif on 20/01/18.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        getOverflowMenu();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/cairo_extralight.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }
    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

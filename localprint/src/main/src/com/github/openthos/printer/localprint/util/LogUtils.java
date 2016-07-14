package com.github.openthos.printer.localprint.util;

import android.util.Log;

import com.github.openthos.printer.localprint.APP;

/**
 * LogUtils
 * Created by bboxh on 2016/4/12.
 */
public class LogUtils {

    public static void i(String tag, String msg) {
        if (APP.IS_LOGI) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (APP.IS_LOGD) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (APP.IS_LOGE) {
            Log.e(tag, msg);
        }
    }

}

package com.github.openthos.printer.openthosprintservice.task;

/**
 * Created by bboxh on 2016/4/14.
 */
public abstract class BaseTask implements Runnable {

    public static String TAG = "BaseTask";

    public BaseTask(String TAG){
        this.TAG = TAG;
    }

    public BaseTask setTAG(String TAG){
        this.TAG = TAG;
        return this;
    }
}

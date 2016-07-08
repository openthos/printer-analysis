package com.github.openthos.printer.localprint.task;

import android.os.AsyncTask;

/**
 * BaseTask
 * Created by bboxh on 2016/5/14.
 */
public abstract class BaseTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected String TAG = "BaseTask";

    public BaseTask() {
        super();
        String TAG = bindTAG();
        if (TAG != null && !TAG.equals("")) {
            this.TAG = TAG;
        }
    }

    /**
     * set the TAG of the task
     *
     * @return the TAG
     */
    protected abstract String bindTAG();

    /**
     * Concurrent execution.
     *
     * @param params parameters
     * @return itself
     */
    public AsyncTask<Params, Progress, Result> start(Params... params) {
        return this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

}

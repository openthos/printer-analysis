package com.github.openthos.printer.openthosprintservice.util;

import com.github.openthos.printer.openthosprintservice.task.BaseTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bboxh on 2016/4/14.
 */
public class TaskUtils {

    private static final String TAG = "TaskUtils";
    private static TaskUtils instance;
    private ExecutorService threadPool;

    public static TaskUtils getInstance(){
        if(instance == null){
            synchronized (TaskUtils.class){
                if(instance == null){
                    instance = new TaskUtils();
                }
            }
        }
        return instance;
    }

    public TaskUtils(){
        init();
    }

    private void init() {
        threadPool = Executors.newCachedThreadPool();
    }

    public static void execute(BaseTask baseTask){
        LogUtils.d(TAG, "execute -> " + baseTask.TAG);
        getInstance().getExecutor().execute(baseTask);
    }

    private ExecutorService getExecutor() {
        return threadPool;
    }


}

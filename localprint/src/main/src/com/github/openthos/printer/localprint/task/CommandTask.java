package com.github.openthos.printer.localprint.task;

import android.os.AsyncTask;
import android.util.Log;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.util.FileUtils;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base command task template.
 * Created by bboxh on 2016/5/14.
 */
public abstract class CommandTask<Params, Progress, Result> extends BaseTask<Params, Progress, Result> {

    /**
     * Use the lock when starting CUPS.
     */
    private static final Boolean IS_STARTING_CUPS = false;

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * The ERROR value can be shown to the user.
     */
    protected String ERROR = "";

    private boolean RUN_AGAIN = true;

    private List<String> mStdOut = new ArrayList<>();
    private List<String> mStdErr = new ArrayList<>();
    private String[] mCmd = null;

    @Override
    protected final Result doInBackground(Params... params) {
        boolean flag = beforeCommand();
        if (!flag)
            return null;

        mCmd = setCmd(params);

        Result result = null;

        //Start CUPS when CUPS is not running ,then run the cmd again.
        while (RUN_AGAIN) {
            RUN_AGAIN = false;
            runCommand(mCmd);
            result = handleCommand(mStdOut, mStdErr);
        }
        return result;
    }

    /**
     * Run the cmd again and need to return immediately.
     * After the method finished, CommandTask will call handleCommand() automatically.
     */
    protected final void runCommandAgain() {
        if (mCmd != null)
            RUN_AGAIN = true;
    }

    /**
     * Called before the cmd executed, in doInBackground method.
     *
     * @return boolean
     */
    protected boolean beforeCommand() {
        return true;
    }

    /**
     * Set the command to execute.
     *
     * @param params mCmd
     * @return cmds
     */
    protected abstract String[] setCmd(Params... params);

    /**
     * Execute the command.
     *
     * @param cmd mCmd
     */
    protected void runCommand(String[] cmd) {

        if (cmd != null && cmd.length == 0) {
            return;
        }
        LogUtils.d(TAG, "mCmd => " + Arrays.toString(cmd));

        mStdOut.clear();
        mStdErr.clear();

        try {
            File file = new File(bindWorkPath());
            final Process p = Runtime.getRuntime().exec(cmd, null, file);

            final Lock lock_in = new Lock();
            final Lock lock_error = new Lock();

            Runnable taskIn = new Runnable() {
                @Override
                public void run() {

                    BufferedReader in
                            = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            mStdOut.add(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_in) {
                        lock_in.notify();
                        lock_in.setFinish(true);
                    }

                }
            };

            Runnable taskError = new Runnable() {
                @Override
                public void run() {

                    BufferedReader in
                            = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            mStdErr.add(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_error) {
                        lock_error.notify();
                        lock_error.setFinish(true);
                    }
                }
            };

            threadPool.execute(taskIn);
            threadPool.execute(taskError);

            synchronized (lock_in) {
                if (!lock_in.isFinish()) {
                    lock_in.wait();
                }
            }

            synchronized (lock_error) {
                if (!lock_error.isFinish()) {
                    lock_error.wait();
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "mStdOut " + mStdOut.toString());
        Log.d(TAG, "mStdErr " + mStdErr.toString());

    }

    /**
     * Execute after the cmd executed, in doInBackground method.
     *
     * @param stdOut standard output
     * @param stdErr error output
     * @return Result
     */
    protected abstract Result handleCommand(List<String> stdOut, List<String> stdErr);

    /**
     * Bind the work path which can be overwritten.
     *
     * @return the work path
     */
    protected String bindWorkPath() {
        return FileUtils.getComponentPath();
    }

    class Lock {
        boolean finish = false;

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }
    }

    /**
     * Check the CUPS running status.
     *
     * @return boolean
     */
    protected boolean cupsIsRunning() {
        boolean flag = false;

        runCommand(new String[]{"sh", "proot.sh", "lpstat", "-r"});
        //  2016/5/15 Check the CUPS running status A1
        for (String line : mStdOut) {
            if (line.contains("scheduler is running")) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Check the CUPS running status.
     * Another method.
     *
     * @return boolean
     */
    protected boolean cupsIsRunning1() {
        boolean flag = false;
        runCommand(new String[]{"sh", "proot.sh", "ps", "|", "grep", "cupsd"});
        //check the CUPS process
        for (String line : mStdOut) {
            if (line.contains("cupsd.conf")) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Start cups
     *
     * @return the result
     */
    protected boolean startCups() {

        // Prevent repeated start CUPS.
        synchronized (IS_STARTING_CUPS) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (cupsIsRunning()) {
                return true;
            }

            File file = new File(bindWorkPath());
            try {
                APP.cupsdProcess = Runtime.getRuntime()
                        .exec(new String[]{"sh", "proot.sh", "cupsd"}, null, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2016/5/15 Start cups A2
        return cupsIsRunning();
    }

    /**
     * Shutdown cups normally to avoid data loss.
     */
    public static void killCups() {

        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"sh", "-c",
                    "ps | grep cupsd 2>/dev/null | awk '{cmd=\"kill \"$2;system(cmd)}'"});
            proc.waitFor();
            Thread.sleep(1);
            LogUtils.d("CommandTask", "killCups");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

package com.github.openthos.printer.localprint.task;

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

/**
 * Created by bboxh on 2016/5/14.
 */
public abstract class CommandTask<Params, Progress, Result> extends BaseTask<Params, Progress, Result> {

    private boolean RUN_AGAIN = true;
    private List<String> stdOut = new ArrayList<String>();
    private List<String> stdErr = new ArrayList<String>();
    protected  String ERROR = "";                   //可以填写错误信息，输出给用户
    private String[] cmd = null;
    private Thread cupsdThread = null;

    @Override
    protected final Result doInBackground(Params... params) {
        boolean flag = beforeCommand();
        if(!flag)
            return null;

        cmd = setCmd(params);

        Result result = null;
        //在CUPS没有启动的情况下会启动CUPS，并且重新运行命令
        while(RUN_AGAIN){
            RUN_AGAIN = false;
            runCommand(cmd);
            result = handleCommand(stdOut, stdErr);
        }
        return result;
    }


    /**
     * 再次运行命令
     * 调用该命令，需要立即return，运行后会自动调用handleCommand函数
     */
    protected final void runCommandAgain(){
        if(cmd != null)
            RUN_AGAIN = true;
    }

    /**
     * 在command运行之前执行
     * 仍然在doInBackground里执行
     * @return
     */
    protected boolean beforeCommand() {

        return true;
    }

    /**
     * 设置要执行的命令
     * @return
     * @param params
     */
    protected abstract String[] setCmd(Params... params);

    /**
     * 执行命令
     * @param cmd
     */
    private void runCommand(String[] cmd) {

        if(cmd != null && cmd.length == 0){
            return;
        }
        LogUtils.d(TAG, "cmd => " + Arrays.toString(cmd));

        stdOut.clear();
        stdErr.clear();

        try {
            File file = new File(getWorkPath());
            final Process p = Runtime.getRuntime().exec(cmd, null, file);

            final Lock lock_in = new Lock();
            final Lock lock_error = new Lock();

            Runnable taskIn = new Runnable() {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    try {
                        while ((line = in.readLine()) != null) {
                            stdOut.add(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_in){
                        lock_in.notify();
                        lock_in.setFinish(true);
                    }


                }
            };

            Runnable taskError = new Runnable() {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line = null;
                    try {
                        while((line = in.readLine()) != null){
                            stdErr.add(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_error){
                        lock_error.notify();
                        lock_error.setFinish(true);
                    }
                }
            };

            new Thread(taskIn).start();
            new Thread(taskError).start();

            synchronized (lock_in){
                if(!lock_in.isFinish()){
                    lock_in.wait();
                }
            }

            synchronized (lock_error){
                if(!lock_error.isFinish()){
                    lock_error.wait();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"stdOut " + stdOut.toString());
        Log.d(TAG,"stdErr " + stdErr.toString());

    }

    /**
     * 命令执行完毕，处理命令。
     * 仍然在doInBackground里执行。
     * @param stdOut
     * @param stdErr
     * @return
     */
    protected abstract Result handleCommand(List<String> stdOut, List<String> stdErr);

    /**
     * 获得工作路径
     * 可自行重写
     * @return 工作路径
     */
    protected String getWorkPath() {
        return FileUtils.getComponentPath();
    }

    class   Lock{
        boolean finish = false;

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }
    }

    /**
     * 检测cups是否在运行
     * @return
     */
    protected boolean cupsIsRunning(){
        boolean flag = false;

        runCommand(new String[]{"sh", "proot.sh", "lpstat", "-r"});
        //  2016/5/15 检测cups是否在运行 A1
        for(String line: stdOut){
            if(line.contains("scheduler is running")){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 检测cups是否在运行
     * 另一个检查方法
     * @return
     */
    protected boolean cupsIsRunning1(){
        boolean flag = false;
        runCommand(new String[]{"sh", "proot.sh", "ps", "|", "grep", "cupsd"});
        //再判断进程是否存在
        for(String line: stdOut){
            if(line.contains("cupsd.conf")){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 启动cups
     * @return  启动结果
     */
    protected boolean startCups(){

        if(cupsIsRunning()){
            return true;
        }

        //runCommand(new String[]{"sh", "proot.sh" ,"cupsd"});

        File file = new File(getWorkPath());
        try {
            APP.cupsdProcess = Runtime.getRuntime().exec(new String[]{"sh", "proot.sh" ,"cupsd"}, null, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2016/5/15 启动cups A2
        return cupsIsRunning();
    }

    /**
     * 关闭cups
     * @return
     */
    protected void killCups(){
        // 2016/5/15 关闭CUPS A3
        /*if(cupsdProcess != null){
            cupsdProcess.destroy();
        }*/

    }

}

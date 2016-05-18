package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * Created by bboxh on 2016/5/17.
 */
public class CancelPrintTask<Progress, Result> extends CommandTask<String, Progress, Result> {
    @Override
    protected String[] setCmd(String... jobId) {
        return new String[]{"sh", "proot.sh", "cancel", jobId[0]};
    }

    @Override
    protected Result handleCommand(List<String> stdOut, List<String> stdErr) {
        //需要注意，处理CUPS没启动的情况，先启动CUPS再重新执行一遍。



        return null;
    }

    @Override
    protected String bindTAG() {
        return "CancelPrintTask";
    }
}

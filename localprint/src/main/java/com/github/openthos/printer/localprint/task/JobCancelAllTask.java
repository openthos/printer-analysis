package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.JobItem;

import java.util.List;

/**
 * 取消所有打印任务 C6
 * Created by bboxh on 2016/6/5.
 */
public class JobCancelAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<JobItem> list;

    public JobCancelAllTask(List<JobItem> list) {
        super();
        this.list = list;
    }

    @Override
    protected String[] setCmd(Params... params) {
        return new String[]{};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5 取消所有打印任务 C6

        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobCancelAllTask";
    }
}

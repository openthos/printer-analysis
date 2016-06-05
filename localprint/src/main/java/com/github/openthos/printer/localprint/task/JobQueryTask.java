package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.JobItem;

import java.util.List;

/**
 * 查询当前打印任务
 * Created by bboxh on 2016/6/5.
 */
public class JobQueryTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<JobItem> list;

    public JobQueryTask(List<JobItem> list) {
        super();
        this.list = list;
    }

    @Override
    protected String[] setCmd(Params... params) {


        return new String[]{};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5 查询打印任务 C2
        
        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobQueryTask";
    }
}

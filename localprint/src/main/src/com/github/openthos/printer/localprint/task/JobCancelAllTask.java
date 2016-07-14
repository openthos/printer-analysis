package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.model.JobItem;

import java.util.List;

/**
 * Cancel all printing job C6
 * Created by bboxh on 2016/6/5.
 */
public class JobCancelAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<JobItem> mList;

    public JobCancelAllTask(List<JobItem> list) {
        super();
        mList = list;
    }

    @Override
    protected String[] setCmd(Params... params) {
        return new String[]{"sh","proot.sh","cancel","-a"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        APP.sendRefreshJobsIntent();

        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobCancelAllTask";
    }
}

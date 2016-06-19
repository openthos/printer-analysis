package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.model.JobItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 恢复所有打印任务 C8
 * Created by bboxh on 2016/6/5.
 */
public class JobResumeAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<JobItem> list;

    public JobResumeAllTask(List<JobItem> list) {
        super();
        this.list = list;
    }
    @Override
    protected String[] setCmd(Params... params) {
        List<String> command = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            JobItem printTask = list.get(i);
            command.add("sh");
            command.add("proot.sh");
            command.add("lp");
            command.add("-i");
            command.add(Integer.toString(printTask.getJobId()));
            command.add("-H");
            command.add("release");
            command.add(";");
        }
        String[] cmd = command.toArray(new String[0]);
        return cmd;
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5 恢复所有打印任务 C8

        APP.sendRefreshJobsIntent();        //发送更新打印任务信息Intent

        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobResumeAllTask";
    }
}

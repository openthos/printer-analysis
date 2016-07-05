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
        command.add("sh");
        command.add("proot.sh");
        command.add("sh");
        command.add("/hold_release.sh");
        for (int i = 0; i < list.size(); i++) {
            JobItem printTask = list.get(i);
            command.add(Integer.toString(printTask.getJobId()));
        }
        command.add("6310");
        command.add("release");
        String[] cmd = command.toArray(new String[0]);
        return cmd;
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        for (String line : stdErr) {

            if (line.startsWith("WARNING"))
                continue;
            else if (line.contains("Bad file descriptor")) {
                if (startCups()) {
                    runCommandAgain();      //再次运行命令
                    return null;
                } else {
                    ERROR = "Cups start failed.";
                    return null;
                }
            }
        }

        // TODO: 2016/6/5 恢复所有打印任务 C8

        APP.sendRefreshJobsIntent();        //发送更新打印任务信息Intent

        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobResumeAllTask";
    }
}

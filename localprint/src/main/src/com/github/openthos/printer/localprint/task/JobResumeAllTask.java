package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.model.JobItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Resume all jobs C8
 * Created by bboxh on 2016/6/5.
 */
public class JobResumeAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<JobItem> mList;

    public JobResumeAllTask(List<JobItem> list) {
        super();
        mList = list;
    }

    @Override
    protected String[] setCmd(Params... params) {
        List<String> command = new ArrayList<String>();
        command.add("sh");
        command.add("proot.sh");
        command.add("sh");
        command.add("/hold_release.sh");
        for (int i = 0; i < mList.size(); i++) {
            JobItem printTask = mList.get(i);
            command.add(Integer.toString(printTask.getJobId()));
        }
        command.add(String.valueOf(APP.CUPS_PORT));
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
                    runCommandAgain();
                    return null;
                } else {
                    ERROR = "Cups start failed.";
                    return null;
                }
            }
        }

        APP.sendRefreshJobsIntent();

        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobResumeAllTask";
    }
}

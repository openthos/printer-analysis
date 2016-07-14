package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.model.JobItem;

import java.util.List;

/**
 * Pause a specified job C4
 * Created by bboxh on 2016/6/5.
 */
public class JobPauseTask<Progress> extends CommandTask<JobItem, Progress, Boolean> {
    @Override
    protected String[] setCmd(JobItem... params) {

        JobItem item = params[0];

        return new String[]{"sh", "proot.sh"
                , "ipptool", "http://localhost:" + APP.CUPS_PORT + "/jobs", "-d"
                , "job-id=" + String.valueOf(item.getJobId()), "hold-job.test"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        boolean stat = true;
        for (String line : stdErr) {
            if (line.contains("lp:") && line.contains("is finished and cannot be altered.")) {
                stat = false;
                ERROR = line;
            }
            if (line.contains("lp:") && line.contains("does not exist.")) {
                stat = false;
                ERROR = line;
            }
        }


        APP.sendRefreshJobsIntent();
        return stat;
    }

    @Override
    protected String bindTAG() {
        return "JobPauseTask";
    }
}

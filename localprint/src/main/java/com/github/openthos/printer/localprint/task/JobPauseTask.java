package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.JobItem;

import java.util.List;

/**
 * Created by bboxh on 2016/6/5.
 */
public class JobPauseTask<Progress> extends CommandTask<JobItem, Progress, Boolean> {
    @Override
    protected String[] setCmd(JobItem... params) {

        JobItem item = params[0];

        return new String[]{"sh", "proot.sh", "lp", "-i", String.valueOf(item.getJobId()), "-H", "hold"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5  暂停打印任务 C4
        boolean stat = true;
        for(String line:stdErr){
            if (line.contains("lp:") && line.contains("is finished and cannot be altered.")){
                stat = false;
                ERROR = line;
            }
            if(line.contains("lp:") && line.contains("does not exist.")){
                stat = false;
                ERROR = line;
            }
        }
        return stat;
    }

    @Override
    protected String bindTAG() {
        return "JobPauseTask";
    }
}

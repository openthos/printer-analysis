package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.model.JobItem;

import java.util.List;

/**
 * Created by bboxh on 2016/6/5.
 */
public class JobResumeTask<Progress> extends CommandTask<JobItem, Progress, Boolean> {
    @Override
    protected String[] setCmd(JobItem... params) {

        JobItem item = params[0];

        return new String[]{"sh", "proot.sh", "ipptool", "http://localhost:"+APP.CUPS_PORT+"/jobs","-d", "job-id="+String.valueOf(item.getJobId()), "release-job.test"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5  恢复打印任务 C5
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

        APP.sendRefreshJobsIntent();        //发送更新打印任务信息Intent

        return stat;
    }

    @Override
    protected String bindTAG() {
        return "JobResumeTask";
    }
}

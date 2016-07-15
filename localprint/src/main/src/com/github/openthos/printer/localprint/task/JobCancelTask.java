package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.android.systemui.statusbar.phone.PrinterJobStatus;

import java.util.List;

/**
 * Cancel a specified job C3
 * Created by bboxh on 2016/6/5.
 */
public class JobCancelTask<Progress> extends CommandTask<PrinterJobStatus, Progress, Boolean> {
    @Override
    protected String[] setCmd(PrinterJobStatus... params) {

        PrinterJobStatus item = params[0];

        return new String[]{"sh", "proot.sh", "cancel", String.valueOf(item.getJobId())};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {
        boolean stat = true;

        for (String line : stdErr) {
            if (line.contains("cancel: cancel-job failed:")
                    && line.contains("is already canceled - can't cancel.")) {
                stat = false;
                ERROR = line;
            }
            if (line.contains("cancel: cancel-job failed:") && line.contains("does not exist.")) {
                stat = false;
                ERROR = line;
            }
        }

        APP.sendRefreshJobsIntent();

        return stat;
    }

    @Override
    protected String bindTAG() {
        return "JobCancelTask";
    }
}

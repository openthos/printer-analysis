package com.github.openthos.printer.localprint.task;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.APP;

import java.util.ArrayList;
import java.util.List;

/**
 * Pause all jobs C7
 * Created by bboxh on 2016/6/5.
 */
public class JobPauseAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<PrinterJobStatus> mList;

    public JobPauseAllTask(List<PrinterJobStatus> list) {
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
            PrinterJobStatus printTask = mList.get(i);
            command.add(Integer.toString(printTask.getJobId()));
        }
        command.add(String.valueOf(APP.CUPS_PORT));
        command.add("hold");
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
        return "JobPauseAllTask";
    }
}

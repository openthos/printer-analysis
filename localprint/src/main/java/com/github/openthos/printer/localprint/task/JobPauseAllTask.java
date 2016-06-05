package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * 暂停所有打印任务 C7
 * Created by bboxh on 2016/6/5.
 */
public class JobPauseAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    @Override
    protected String[] setCmd(Params... params) {
        return new String[]{};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5 暂停所有打印任务 C7

        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobPauseAllTask";
    }
}

package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * 恢复所有打印任务 C8
 * Created by bboxh on 2016/6/5.
 */
public class JobResumeAllTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    @Override
    protected String[] setCmd(Params... params) {
        return new String[]{};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5 恢复所有打印任务 C8
        
        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobResumeAllTask";
    }
}

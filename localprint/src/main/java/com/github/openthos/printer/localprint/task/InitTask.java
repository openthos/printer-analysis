package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.util.FileUtils;

import java.util.List;

/**
 * 初始化CUPS等数据
 * Created by bboxh on 2016/5/15.
 */
public class InitTask<Progress> extends CommandTask<Object, Progress, Boolean> {
    @Override
    protected String[] setCmd(Object[] params) {
        return new String[]{"tar", "vxzf", "/mnt/sdcard"+ APP.COMPONENT_PATH + ".tar.gz"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        Boolean flag = false;

        //判断是否执行成功，大于4条信息就成功
        if(stdOut.size() > 4 ){
            flag = true;
        }

        return flag;
    }

    @Override
    protected String getWorkPath() {
        return FileUtils.getFilePath();
    }

    @Override
    protected String bindTAG() {
        return "InitTask";
    }
}

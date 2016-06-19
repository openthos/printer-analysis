package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.JobItem;
import com.github.openthos.printer.localprint.model.PrinterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询当前打印任务
 * Created by bboxh on 2016/6/5.
 */
public class JobQueryTask<Params, Progress> extends CommandTask<Params, Progress, Boolean> {
    private final List<JobItem> list;

    public JobQueryTask(List<JobItem> list) {
        super();
        this.list = list;
    }

    @Override
    protected String[] setCmd(Params... params) {


        return new String[]{"sh","proot.sh","lpq","-a","&&","sh","proot.sh","lpstat","-l","-o"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/5 查询打印任务 C2
        String statusLine = " ";
        int stat = 0;
        int id = -1;
        for(String line:stdOut){
            if(line.equals("no entries") || line.startsWith("Rank"))
                continue;

            if(line.endsWith("bytes")){
                String[] splitLine = line.split(" ");
                JobItem printTask = new JobItem();
                printTask.setJobId(Integer.parseInt(splitLine[2]));
                printTask.setSize(splitLine[splitLine.length-2] + splitLine[splitLine.length-1]);
                StringBuffer sb = new StringBuffer();
                for (int i = 3; i < (splitLine.length-2); i++) {
                    sb.append(splitLine[i]);
                }
                printTask.setFileName(sb.toString());
                list.add(printTask);
                continue;
            }

            if(line.startsWith("Status")) {
                statusLine = line.replace("Status:",null);
                continue;
            }

            if(line.startsWith("Alerts")){
                String[] splitLine = line.split(":");
                if(splitLine[1].contains("none")) {
                    stat = JobItem.STATUS_READY;
                    continue;
                }
                if(splitLine[1].contains("job-hold-until-specified")) {
                    stat = JobItem.STATUS_HOLDING;
                    continue;
                }
                if(splitLine[1].contains("job-printing")){
                    if (statusLine.endsWith("failed")) {
                        stat = JobItem.STATUS_ERROR;
                    }
                    if(statusLine.endsWith("Waiting for printer to become available."))
                        stat = JobItem.STATUS_WAITING_FOR_PRINTER;

                    if(statusLine.contains("ing")) {
                        stat = JobItem.STATUS_PRINTING;
                    }
                    continue;
                }
            }

            if(line.startsWith("queued")){
                String[] splitLine = line.split(" ");
                for (int i = 0; i < list.size(); i++) {
                    JobItem printTask = list.get(i);
                    if (printTask.getJobId() == id) {
                        printTask.setPrinter(splitLine[2]);
                        printTask.setStatus(stat);
                        if(stat == JobItem.STATUS_ERROR)
                            printTask.setERROR(statusLine);
                        if (stat == JobItem.STATUS_PRINTING)
                            printTask.setERROR(statusLine);
                    }
                    continue;
                }
                continue;
            }

            String[] splitLine = line.split(" ");
            String[] info = splitLine[0].split("-");
            id = Integer.parseInt(info[info.length-1]);


        }
        return true;
    }

    @Override
    protected String bindTAG() {
        return "JobQueryTask";
    }
}

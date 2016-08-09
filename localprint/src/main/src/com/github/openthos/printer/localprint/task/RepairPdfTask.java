package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * Created by Taocr on 2016/8/8.
 */
public class RepairPdfTask extends CommandTask <String, Void, Void> {
    @Override
    protected String[] setCmd(String... params) {
        String fileName = params[0];
        return new String[]{"sh", "proot.sh", "gs", "-o", fileName + "1",
                            "-sDEVICE=pdfwrite", fileName};
    }

    @Override
    protected Void handleCommand(List stdOut, List stdErr) {
        return null;
    }

    @Override
    protected String bindTAG() {
        return "RepairPdfTask";
    }

}

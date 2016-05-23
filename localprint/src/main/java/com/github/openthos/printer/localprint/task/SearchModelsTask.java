package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.ModelsItem;
import com.github.openthos.printer.localprint.model.PPDItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查找驱动
 * Created by bboxh on 2016/5/16.
 */
public class SearchModelsTask<Params, Progress> extends CommandTask<Params, Progress, ModelsItem> {
    @Override
    protected String[] setCmd(Params[] params) {
        return new String[]{""};
    }

    @Override
    protected ModelsItem handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/5/16 查找可添加打印机型号（驱动） B1
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> brand = new ArrayList<>();
        Map<String, List<PPDItem>> models = new HashMap<>();

        brand.add("Epson");
        List<PPDItem> epsonBrand = new ArrayList<>();
        epsonBrand.add(new PPDItem("drv:///sample.drv/epson9.ppd", "Epson", "9-Pin Series"));
        epsonBrand.add(new PPDItem("Epson-AcuLaser_CX17NF.ppd.gz" ,"Epson", "AcuLaser CX17NF Foomatic/foo2hbpl2 (recommended)"));
        models.put("Epson", epsonBrand);

        return new ModelsItem(brand, models);
    }

    @Override
    protected String bindTAG() {
        return "SearchModelsTask";
    }
}

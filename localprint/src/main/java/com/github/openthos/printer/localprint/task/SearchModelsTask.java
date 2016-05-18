package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.ModelsItem;

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
        Map<String, List<String>> models = new HashMap<>();

        brand.add("Epson");
        List<String> epsonBrand = new ArrayList<>();
        epsonBrand.add("AcuLaser CX17NF Foomatic/foo2hbpl2 (recommended)");
        epsonBrand.add("AcuLaser M1400 Foomatic/foo2hbpl2 (recommended)");
        models.put("Epson", epsonBrand);

        return new ModelsItem(brand, models);
    }

    @Override
    protected String bindTAG() {
        return "SearchModelsTask";
    }
}

package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.ModelsItem;
import com.github.openthos.printer.localprint.model.PPDItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Search available printer models(drivers) B1
 * Created by bboxh on 2016/5/16.
 */
public abstract class SearchModelsTask<Params, Progress> extends CommandTask<Params, Progress,
                                                                             ModelsItem> {
    @Override
    protected String[] setCmd(Params[] params) {
        return new String[]{"sh", "proot.sh", "lpinfo", "-m"};
    }

    @Override
    protected ModelsItem handleCommand(List<String> stdOut, List<String> stdErr) {

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

        final List<String> brand = new ArrayList<>();
        final Map<String, List<PPDItem>> models = new HashMap<>();

        for (String line : stdOut) {
            String[] splitLine = line.split(" ");
            String currentPPD = splitLine[0];
            String currentBrand = splitLine[1];
            String currentDriver = "";
            for (int i = 2; i < splitLine.length; i++) {
                currentDriver = currentDriver + " " + splitLine[i];
            }

            List<PPDItem> location;

            if (brand.contains(currentBrand)) {
                location = models.get(currentBrand);
            } else {
                brand.add(currentBrand);
                location = new ArrayList<>();
                models.put(currentBrand, location);
            }

            location.add(new PPDItem(currentPPD, currentBrand, currentDriver));
        }

        if (bindPrinter() != null) {
            CommandTask<Void, Void, Boolean> task = new CommandTask<Void, Void, Boolean>() {

                @Override
                protected String bindTAG() {
                    return "SearchModelsTaskInner";
                }

                @Override
                protected String[] setCmd(Void... params) {
                    return new String[]{"sh", "proot.sh", "lpinfo",
                                        "--product", bindPrinter(), "-m"};
                }

                @Override
                protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

                    boolean flag = false;

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

                    for (String line : stdOut) {
                        String[] splitLine = line.split(" ");
                        String currentPPD = splitLine[0];
                        String currentBrand
                                = APP.getApplicatioContext().getString(R.string.recommanded);
                        String currentDriver = "";
                        for (int i = 2; i < splitLine.length; i++) {
                            currentDriver = currentDriver + " " + splitLine[i];
                        }

                        List<PPDItem> location;

                        if (brand.contains(currentBrand)) {
                            location = models.get(currentBrand);
                        } else {
                            brand.add(currentBrand);
                            location = new ArrayList<>();
                            models.put(currentBrand, location);
                        }

                        location.add(new PPDItem(currentPPD, currentBrand, currentDriver));
                        flag = true;
                    }

                    return flag;
                }

                @Override
                protected void onPostExecute(Boolean flag) {
                    synchronized (this) {
                        this.notify();
                    }
                }
            };

            task.start();

            try {
                synchronized (task) {
                    task.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new ModelsItem(brand, models);
    }

    protected abstract String bindPrinter();

    @Override
    protected String bindTAG() {
        return "SearchModelsTask";
    }
}

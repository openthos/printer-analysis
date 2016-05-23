package com.github.openthos.printer.localprint;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.github.openthos.printer.localprint.task.CommandTask;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void test_usb(){
        new CommandTask<Void, Void, Void>(){

            @Override
            protected String bindTAG() {
                return "test_usb";
            }

            @Override
            protected String[] setCmd(Void... params) {
                return new String[]{"sh","proot.sh","sh","/tools/usb.sh"};
            }

            @Override
            protected Void handleCommand(List<String> stdOut, List<String> stdErr) {
                return null;
            }
        }.start();
    }

}
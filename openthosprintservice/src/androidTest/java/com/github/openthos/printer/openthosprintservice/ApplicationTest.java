package com.github.openthos.printer.openthosprintservice;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.view.WindowManager;

import com.github.openthos.printer.openthosprintservice.model.DriveGsFoo2zjsItem;
import com.github.openthos.printer.openthosprintservice.model.DriveGsFoo2zjsItemHelper;
import com.github.openthos.printer.openthosprintservice.model.DriveItem;
import com.github.openthos.printer.openthosprintservice.model.DriveItemHelper;
import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.ui.ManagementActivity;
import com.github.openthos.printer.openthosprintservice.util.DialogUtils;
import com.github.openthos.printer.openthosprintservice.util.SQLHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static boolean IS_FIRST = true;

    public ApplicationTest() {
        super(Application.class);
    }

    public void testSQL(){
        SQLHelper helper = new SQLHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        db.getVersion();
    }

    public void testPrinterItem(){
        PrinterItemHelper helper = new PrinterItemHelper();
        PrinterItem item = new PrinterItem("print2", "Hewlett-Packard", 1008, 42,
                 "000001111Q8D9XVKPR1b", 1);
        //helper.insert(item);
        helper.query(4);
        //helper.delete(1);
    }

    public void testOnce(){
        if(!IS_FIRST){
            return;
        }
        IS_FIRST = false;

        testPrinterItem();
    }

    public void testPrinterItem1(){
        PrinterItemHelper helper = new PrinterItemHelper();
        PrinterItem item = new PrinterItem();
        item.setVendorId(1008);
        item.setProductId(42);
        item.setSerialNumber("000000000Q8D9XVKPR1a");
        helper.isExist(item);
    }

    public void testPrinterItem2(){
        PrinterItemHelper helper = new PrinterItemHelper();
        //helper.queryAll();
        //helper.delete(9);
        PrinterItem item = new PrinterItem();
        item.setVendorId(1008);
        item.setProductId(42);
        item.setSerialNumber("000000000Q8D9XVKPR1a");
        helper.deleteItem(item);
    }

    public void testPrinterItem3(){
        PrinterItemHelper helper = new PrinterItemHelper();
        PrinterItem item = new PrinterItem("print123", "Hewlett-Packard", 1008, 42,
                "000000000Q8D9XVKPR1a", 1);
        item.setPrinterId(13);
        helper.update(item);
    }

    public void testDriveItemHelper(){
        DriveItemHelper helper = new DriveItemHelper();
        DriveItem item = new DriveItem(1, "gs&foo2zjs", 1008, 42);
        helper.insert(item);
        helper.query(item);
        item.setDriverName("gs&&&&foo2zjs");
        helper.update(item);
        helper.queryAll();
        //helper.delete(1);
    }

    public void testDriveGsFoo2zjsItem(){
        DriveGsFoo2zjsItemHelper helper = new DriveGsFoo2zjsItemHelper();
        DriveGsFoo2zjsItem item = new DriveGsFoo2zjsItem(2, "-q -a9", "-z3 -r600x600");

        helper.insert(item);
        helper.query(item);
        item.setGs("-q -a9 -r600x600");
        helper.update(item);
        helper.queryAll();
        //helper.delete(1);
    }

    public void testDriveItemHelper1(){
        DriveItemHelper helper = new DriveItemHelper();
        helper.queryVendorId(1008);
    }

    public void testSQL1(){
        new PrinterItemHelper().queryAll();
        new DriveItemHelper().queryAll();
        new DriveGsFoo2zjsItemHelper().queryAll();
    }

    public void testSQL2(){
        PrinterItemHelper helper = new PrinterItemHelper();
        if(helper.queryAll() == null){
            PrinterItem item = new PrinterItem("print1", "Hewlett-Packard", 1008, 42,
                    "000000000Q8D9XVKPR1a", 1);
            helper.insert(item);
        }

        DriveGsFoo2zjsItemHelper helper1 = new DriveGsFoo2zjsItemHelper();
        DriveGsFoo2zjsItem item = new DriveGsFoo2zjsItem(1, "-q -a9", "-z3 -r600x600");

        helper1.insert(item);
        helper1.query(item);

    }


}
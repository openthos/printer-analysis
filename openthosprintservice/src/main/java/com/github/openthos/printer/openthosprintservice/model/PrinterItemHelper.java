package com.github.openthos.printer.openthosprintservice.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;
import com.github.openthos.printer.openthosprintservice.util.SQLHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/4/14.
 */
public class PrinterItemHelper {

    public static final String PRINTER_TABLE_NAME = "printer";

    public static final String PrinterId = "printerid";
    public static final String NickName = "nickname";
    public static final String ManufacturerName = "manufacturername";
    public static final String VendorId = "vendorid";
    public static final String ProductId = "productid";
    public static final String SerialNumber = "serialnumber";
    public static final String DriverId = "driverid";
    
    public static final String PRINTER_TABLE_CREATE =
            "CREATE TABLE " + PRINTER_TABLE_NAME + " (" +
                    PrinterId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NickName + " TEXT NOT NULL, " +
                    ManufacturerName + " TEXT NOT NULL, " +
                    VendorId + " INTEGER NOT NULL, " +
                    ProductId + " INTEGER NOT NULL, " +
                    SerialNumber + " TEXT NOT NULL, " +
                    DriverId + " INTEGER NOT NULL);";

    private static final String TAG = "PrinterItemHelper";
    private final SQLHelper helper;
    private final SQLiteDatabase db;

    public PrinterItemHelper(){
        helper = new SQLHelper(APP.getApplicatioContext());
        db = helper.getWritableDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /**
     * 使用结束时，必须关闭数据库连接
     */
    public void close(){
        if(db.isOpen()) {
            db.close();
        }
    }

    public int insert(PrinterItem item){
        boolean flag = false;


        ContentValues values = new ContentValues();
        values.put(NickName, item.getNickName());
        values.put(ManufacturerName, item.getManufacturerName());
        values.put(VendorId, item.getVendorId());
        values.put(ProductId, item.getProductId());
        values.put(SerialNumber, item.getSerialNumber());
        values.put(DriverId, item.getDriverId());
        int rowid= (int) db.insert(PRINTER_TABLE_NAME, null, values);

        item.setPrinterId(rowid);

        Log.d(TAG, "insert-> rowid = " + rowid + "\n" + item.toString());

        return rowid;
    }

    public int update(PrinterItem item){
        ContentValues values = new ContentValues();
        values.put(NickName, item.getNickName());
        values.put(ManufacturerName, item.getManufacturerName());
        values.put(VendorId, item.getVendorId());
        values.put(ProductId, item.getProductId());
        values.put(SerialNumber, item.getSerialNumber());
        values.put(DriverId, item.getDriverId());
        int number = db.update(PRINTER_TABLE_NAME, values,  PrinterId + " = ? ", new String[]{String.valueOf(item.getPrinterId())});
        LogUtils.d(TAG, "update() number -> " + number);
        return number;
    }

    /**
     * 按 VendorId ProductId SerialNumber 三个属性查找
     * @param item
     * @return  存在返回true
     */
    public boolean isExist(PrinterItem item){
        boolean flag = false;

        if(query(item) != null){
            flag = true;
        }

        LogUtils.d(TAG, "isExist() -> " + flag);

        return flag;
    }

    /**
     * 按 VendorId ProductId SerialNumber 三个属性查找
     * @param item
     * @return  空返回 null
     */
    public List<PrinterItem> query(PrinterItem item){

        Cursor cursor = db.query(PRINTER_TABLE_NAME, new String[]{PrinterId, NickName, ManufacturerName, VendorId, ProductId, SerialNumber, DriverId},
                VendorId + " = ? AND " + ProductId + " = ? AND " + SerialNumber + " = ? ",
                new String[]{String.valueOf(item.getVendorId()), String.valueOf(item.getProductId()), item.getSerialNumber()},
                null, null, null);

        List<PrinterItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            PrinterItem new_item = new PrinterItem();
            new_item.setPrinterId(cursor.getInt(0));
            new_item.setNickName(cursor.getString(1));
            new_item.setManufacturerName(cursor.getString(2));
            new_item.setVendorId(cursor.getInt(3));
            new_item.setProductId(cursor.getInt(4));
            new_item.setSerialNumber(cursor.getString(5));
            new_item.setDriverId(cursor.getInt(6));
            LogUtils.d(TAG,"query() -> " + new_item.toString());
            list.add(new_item);
        }

        if(list.isEmpty()){
            list = null;
        }

        return list;
    }

    /**
     *通过printerid查找
     * @param printerid
     * @return
     */
    public PrinterItem query(int printerid){
        Cursor cursor = db.query(PRINTER_TABLE_NAME, new String[]{PrinterId, NickName, ManufacturerName, VendorId, ProductId, SerialNumber, DriverId},
                PrinterId + " = ? ",
                new String[]{String.valueOf(printerid)},
                null, null, null);

        if(cursor.moveToNext()) {
            PrinterItem new_item = new PrinterItem();
            new_item.setPrinterId(cursor.getInt(0));
            new_item.setNickName(cursor.getString(1));
            new_item.setManufacturerName(cursor.getString(2));
            new_item.setVendorId(cursor.getInt(3));
            new_item.setProductId(cursor.getInt(4));
            new_item.setSerialNumber(cursor.getString(5));
            new_item.setDriverId(cursor.getInt(6));
            LogUtils.d(TAG, "query() -> " + new_item.toString());
            return new_item;
        }
        return null;
    }

    /**
     *
     * @return 为空返回null
     */
    public List<PrinterItem> queryAll(){

        Cursor cursor = db.query(PRINTER_TABLE_NAME, new String[]{PrinterId, NickName, ManufacturerName, VendorId, ProductId, SerialNumber, DriverId},
                null, null,null, null, null);

        List<PrinterItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            PrinterItem new_item = new PrinterItem();
            new_item.setPrinterId(cursor.getInt(0));
            new_item.setNickName(cursor.getString(1));
            new_item.setManufacturerName(cursor.getString(2));
            new_item.setVendorId(cursor.getInt(3));
            new_item.setProductId(cursor.getInt(4));
            new_item.setSerialNumber(cursor.getString(5));
            new_item.setDriverId(cursor.getInt(6));
            LogUtils.d(TAG,"queryAll() -> " + new_item.toString());
            list.add(new_item);
        }
        cursor.close();
        if(list.isEmpty()){
            list = null;
        }

        return list;
    }

    public int delete(int printerid){
        int number = db.delete(PRINTER_TABLE_NAME, PrinterId + " = ? ",
                new String[]{String.valueOf(printerid)});
        LogUtils.d(TAG, "delete -> number = " + number);
        return number;
    }

    /**
     * 按 VendorId ProductId SerialNumber 三个属性匹配删除
     *所以可能删除多个项
     * @param item
     * @return
     */
    public int deleteItem(PrinterItem item){
        SQLiteDatabase db = helper.getWritableDatabase();
        int number = db.delete(PRINTER_TABLE_NAME, VendorId + " = ? AND " + ProductId + " = ? AND " + SerialNumber + " = ? ",
                new String[]{String.valueOf(item.getVendorId()), String.valueOf(item.getProductId()), item.getSerialNumber()});
        LogUtils.d(TAG, "deleteItem -> number = " + number);
        return number;
    }

}

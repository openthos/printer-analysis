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
public class DriveItemHelper {

    public static final String DRIVE_TABLE_NAME = "drive";

    public static final String DriverId = "driverid";
    public static final String DriverName = "drivername";
    public static final String VendorId = "vendorid";
    public static final String ProductId = "productid";

    public static final String DRIVE_TABLE_CREATE =
            "CREATE TABLE " + DRIVE_TABLE_NAME + " (" +
                    DriverId + " INTEGER PRIMARY KEY, " +
                    DriverName + " TEXT NOT NULL, " +
                    VendorId + " INTEGER NOT NULL, " +
                    ProductId + " INTEGER NOT NULL);";

    private static final String TAG = "DriveItemHelper";
    private final SQLHelper helper;
    private final SQLiteDatabase db;

    public DriveItemHelper(){
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

    public int insert(DriveItem item){
        boolean flag = false;

        ContentValues values = new ContentValues();
        values.put(DriverId, item.getDriverId());
        values.put(DriverName, item.getDriverName());
        values.put(VendorId, item.getVendorId());
        values.put(ProductId, item.getProductId());
        int rowid = (int) db.insert(DRIVE_TABLE_NAME, null, values);

        Log.d(TAG, "insert-> rowid = " + rowid + "\n" + item.toString());

        return rowid;
    }

    public int update(DriveItem item){
        ContentValues values = new ContentValues();
        values.put(DriverId, item.getDriverId());
        values.put(DriverName, item.getDriverName());
        values.put(VendorId, item.getVendorId());
        values.put(ProductId, item.getProductId());
        int number = db.update(DRIVE_TABLE_NAME, values,  DriverId + " = ? ", new String[]{String.valueOf(item.getDriverId())});
        LogUtils.d(TAG, "update() number -> " + number);
        return number;
    }

    /**
     * 只比较 DriverId
     * @param item
     * @return
     */
    public boolean isExist(DriveItem item){
        boolean flag = false;

        if(query(item) != null){
            flag = true;
        }

        LogUtils.d(TAG, "isExist() -> " + flag);

        return flag;
    }

    /**
     * 比较 DriverId
     * @param item
     * @return
     */
    public List<DriveItem> query(DriveItem item){

        Cursor cursor = db.query(DRIVE_TABLE_NAME, new String[]{DriverId, DriverName, VendorId, ProductId},
                DriverId + " = ? ",
                new String[]{String.valueOf(item.getDriverId())},
                null, null, null);

        List<DriveItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            DriveItem new_item = new DriveItem();
            new_item.setDriverId(cursor.getInt(0));
            new_item.setDriverName(cursor.getString(1));
            new_item.setVendorId(cursor.getInt(2));
            new_item.setProductId(cursor.getInt(3));
            LogUtils.d(TAG,"query() -> " + new_item.toString());
            list.add(new_item);
        }

        if(list.isEmpty()){
            list = null;
        }

        return list;
    }

    /**
     *
     * @param vendorid
     * @return  没有查询到返回 null 否则返回 List<DriveItem>
     */
    public List<DriveItem> queryVendorId(int vendorid){

        Cursor cursor = db.query(DRIVE_TABLE_NAME, new String[]{DriverId, DriverName, VendorId, ProductId},
                VendorId + " = ? ",
                new String[]{String.valueOf(vendorid)},
                null, null, null);

        List<DriveItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            DriveItem new_item = new DriveItem();
            new_item.setDriverId(cursor.getInt(0));
            new_item.setDriverName(cursor.getString(1));
            new_item.setVendorId(cursor.getInt(2));
            new_item.setProductId(cursor.getInt(3));
            LogUtils.d(TAG,"queryVendorId() -> " + new_item.toString());
            list.add(new_item);
        }

        if(list.isEmpty()){
            list = null;
        }

        return list;
    }

    /**
     *
     * @return
     */
    public List<DriveItem> queryAll(){

        Cursor cursor = db.query(DRIVE_TABLE_NAME, new String[]{DriverId, DriverName, VendorId, ProductId},
                null, null,null, null, null);

        List<DriveItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            DriveItem new_item = new DriveItem();
            new_item.setDriverId(cursor.getInt(0));
            new_item.setDriverName(cursor.getString(1));
            new_item.setVendorId(cursor.getInt(2));
            new_item.setProductId(cursor.getInt(3));
            LogUtils.d(TAG,"queryAll() -> " + new_item.toString());
            list.add(new_item);
        }
        cursor.close();
        if(list.isEmpty()){
            list = null;
        }

        return list;
    }

    /**
     *
     * @param driverid
     * @return
     */
    public int delete(int driverid){
        int number = db.delete(DRIVE_TABLE_NAME, DriverId + " = ? ",
                new String[]{String.valueOf(driverid)});
        LogUtils.d(TAG, "delete -> number = " + number);
        return number;
    }


}

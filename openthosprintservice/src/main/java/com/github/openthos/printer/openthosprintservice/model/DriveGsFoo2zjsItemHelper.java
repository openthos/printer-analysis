package com.github.openthos.printer.openthosprintservice.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;
import com.github.openthos.printer.openthosprintservice.util.SQLHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/4/14.
 */
public class DriveGsFoo2zjsItemHelper {

    public static final String DRIVEGSFOO2ZJS_TABLE_NAME = "drive_gs_foo2zjs";

    public static final String PrinterId = "printerid";
    public static final String Gs = "gs";
    public static final String Foo2zjs = "foo2zjs";

    public static final String DRIVEGSFOO2ZJS_TABLE_CREATE =
            "CREATE TABLE " + DRIVEGSFOO2ZJS_TABLE_NAME + " (" +
                    PrinterId + " INTEGER PRIMARY KEY, " +
                    Gs + " TEXT NOT NULL, " +
                    Foo2zjs + " TEXT NOT NULL, " +
                    " FOREIGN KEY(" + PrinterId + ") REFERENCES " + PrinterItemHelper.PRINTER_TABLE_NAME + "(" + PrinterItemHelper.PrinterId + ") ON DELETE CASCADE );";

    private static final String TAG = "DriveGsFoo2..ItemHelper";
    private final SQLHelper helper;
    private final SQLiteDatabase db;

    public DriveGsFoo2zjsItemHelper() {
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

    /**
     *
     * @param item
     * @return 返回 -1 表示insert失败
     */
    public int insert(DriveGsFoo2zjsItem item){

        ContentValues values = new ContentValues();
        values.put(PrinterId, item.getPrinterId());
        values.put(Gs, item.getGs());
        values.put(Foo2zjs, item.getFoo2zjs());
        int rowid = -1;
        rowid = (int) db.insert(DRIVEGSFOO2ZJS_TABLE_NAME, null, values);

        LogUtils.d(TAG, "insert-> rowid = " + rowid + "\n" + item.toString());

        return rowid;
    }

    public int update(DriveGsFoo2zjsItem item){

        ContentValues values = new ContentValues();
        values.put(Gs, item.getGs());
        values.put(Foo2zjs, item.getFoo2zjs());
        int number = db.update(DRIVEGSFOO2ZJS_TABLE_NAME, values,  PrinterId + " = ? ", new String[]{String.valueOf(item.getPrinterId())});
        LogUtils.d(TAG, "update() number -> " + number);
        db.close();
        return number;
    }

    public boolean isExist(DriveGsFoo2zjsItem item){
        boolean flag = false;

        if(query(item) != null){
            flag = true;
        }

        LogUtils.d(TAG, "isExist() -> " + flag);

        return flag;
    }

    public List<DriveGsFoo2zjsItem> query(DriveGsFoo2zjsItem item){


        Cursor cursor = db.query(DRIVEGSFOO2ZJS_TABLE_NAME, new String[]{PrinterId, Gs, Foo2zjs},
                PrinterId + " = ? ",
                new String[]{String.valueOf(item.getPrinterId())},
                null, null, null);

        List<DriveGsFoo2zjsItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            DriveGsFoo2zjsItem new_item = new DriveGsFoo2zjsItem();
            new_item.setPrinterId(cursor.getInt(0));
            new_item.setGs(cursor.getString(1));
            new_item.setFoo2zjs(cursor.getString(2));
            LogUtils.d(TAG,"query() -> " + new_item.toString());
            list.add(new_item);
        }

        if(list.isEmpty()){
            list = null;
        }
        return list;
    }

    public List<DriveGsFoo2zjsItem> queryAll(){


        Cursor cursor = db.query(DRIVEGSFOO2ZJS_TABLE_NAME, new String[]{PrinterId, Gs, Foo2zjs},
                null, null,null, null, null);

        List<DriveGsFoo2zjsItem> list = new ArrayList<>();

        while(cursor.moveToNext()){
            DriveGsFoo2zjsItem new_item = new DriveGsFoo2zjsItem();
            new_item.setPrinterId(cursor.getInt(0));
            new_item.setGs(cursor.getString(1));
            new_item.setFoo2zjs(cursor.getString(2));
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

        int number = db.delete(DRIVEGSFOO2ZJS_TABLE_NAME, PrinterId + " = ? ",
                new String[]{String.valueOf(printerid)});
        LogUtils.d(TAG, "delete -> number = " + number);
        return number;
    }

}

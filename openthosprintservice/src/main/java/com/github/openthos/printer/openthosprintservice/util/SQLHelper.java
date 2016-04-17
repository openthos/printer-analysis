package com.github.openthos.printer.openthosprintservice.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.openthos.printer.openthosprintservice.model.DriveGsFoo2zjsItemHelper;
import com.github.openthos.printer.openthosprintservice.model.DriveItem;
import com.github.openthos.printer.openthosprintservice.model.DriveItemHelper;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;

/**
 * Created by bboxh on 2016/4/14.
 */
public class SQLHelper extends SQLiteOpenHelper {


    private static final String DRIVE_TABLE_CREATE = "";
    private static final String DRIVE_GS_FOO2ZJS_TABLE_CREATE ="";
    private static final String DATABASE_NAME = "print.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "SQLHelper";

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if(!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PrinterItemHelper.PRINTER_TABLE_CREATE);
        db.execSQL(DriveItemHelper.DRIVE_TABLE_CREATE);
        db.execSQL(DriveGsFoo2zjsItemHelper.DRIVEGSFOO2ZJS_TABLE_CREATE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.d(TAG, "onUpgrade() -> " + oldVersion + " -> " + "newVersion");
    }
}

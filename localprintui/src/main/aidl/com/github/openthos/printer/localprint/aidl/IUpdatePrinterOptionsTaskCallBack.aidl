package com.github.openthos.printer.localprint.aidl;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;


interface IUpdatePrinterOptionsTaskCallBack {

    void onPostExecute(boolean aBoolean, String ERROR);

    String getPrinter();

    PrinterOptionItem bindStart();

}
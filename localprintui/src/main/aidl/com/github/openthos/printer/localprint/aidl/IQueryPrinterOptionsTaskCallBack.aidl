package com.github.openthos.printer.localprint.aidl;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

interface IQueryPrinterOptionsTaskCallBack {

    void onPostExecute(in PrinterOptionItem printerOptionItem);

    String bindStart();

}
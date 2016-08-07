package com.github.openthos.printer.localprint.aidl;

interface IUpdatePrinterCupsOptionsTaskCallBack {

    String getPrinter();

    void onPostExecute(boolean flag);

    List bindStart();

}

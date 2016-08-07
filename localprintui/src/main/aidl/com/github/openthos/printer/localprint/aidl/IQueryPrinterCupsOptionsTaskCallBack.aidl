package com.github.openthos.printer.localprint.aidl;

interface IQueryPrinterCupsOptionsTaskCallBack {

    void onPostExecute(in List printerOptionItems, String ERROR);

    String bindStart();

}

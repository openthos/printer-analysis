package com.github.openthos.printer.localprint.aidl;

interface IPrintTaskCallBack {

    String bindPrinterName();

    void onPostExecute(String jobId, String ERROR);

    Map bindStart();

}
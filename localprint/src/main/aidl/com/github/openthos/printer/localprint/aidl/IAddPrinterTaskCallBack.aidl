package com.github.openthos.printer.localprint.aidl;

interface IAddPrinterTaskCallBack {

    void onPostExecute(boolean aBoolean);

    Map bindStart();

}

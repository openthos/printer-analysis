package com.github.openthos.printer.localprint.aidl;

import com.android.systemui.statusbar.phone.PrinterJobStatus;

interface IJobCancelTaskCallBack {

    void onPostExecute(boolean aBoolean);

    PrinterJobStatus bindStart();

}

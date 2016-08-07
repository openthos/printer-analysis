package com.github.openthos.printer.localprint.aidl;

import com.github.openthos.printer.localprint.aidl.IInitTaskCallBack;

interface ITaskAidlInterface {

    void InitTask(IInitTaskCallBack callBack);

    void initFailed();

    void initSucceed();

    void sendRefreshJobsIntent();

}
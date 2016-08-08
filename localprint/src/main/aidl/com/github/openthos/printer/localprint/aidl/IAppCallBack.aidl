package com.github.openthos.printer.localprint.aidl;

interface IAppCallBack {

    void IS_FIRST_RUN(boolean aBoolean);

    void IS_INITIALIZING(boolean aBoolean);

    void setJobList(in List jobList);

    boolean IS_MANAGEMENT_ACTIVITY_ON_TOP();

}

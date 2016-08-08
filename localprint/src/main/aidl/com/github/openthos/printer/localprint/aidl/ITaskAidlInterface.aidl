package com.github.openthos.printer.localprint.aidl;

import com.github.openthos.printer.localprint.aidl.IInitTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IAddPrinterTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IDeletePrinterTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobCancelAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobCancelTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobPauseAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobPauseTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobResumeAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobResumeTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IListAddedTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IPrintTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IQueryPrinterCupsOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IQueryPrinterOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.ISearchModelsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.ISearchPrintersTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IUpdatePrinterCupsOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IUpdatePrinterOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IAppCallBack;

interface ITaskAidlInterface {

    void IInitTaskCallBack(IInitTaskCallBack callBack);
    void IAddPrinterTaskCallBack(IAddPrinterTaskCallBack callBack);
    void IDeletePrinterTaskCallBack(IDeletePrinterTaskCallBack callBack);
    void IJobCancelAllTaskCallBack(IJobCancelAllTaskCallBack callBack);
    void IJobCancelTaskCallBack(IJobCancelTaskCallBack callBack);
    void IJobPauseAllTaskCallBack(IJobPauseAllTaskCallBack callBack);
    void IJobPauseTaskCallBack(IJobPauseTaskCallBack callBack);
    void IJobResumeAllTaskCallBack(IJobResumeAllTaskCallBack callBack);
    void IJobResumeTaskCallBack(IJobResumeTaskCallBack callBack);
    void IListAddedTaskCallBack(IListAddedTaskCallBack callBack);
    void IPrintTaskCallBack(IPrintTaskCallBack callBack);
    void IQueryPrinterCupsOptionsTaskCallBack(IQueryPrinterCupsOptionsTaskCallBack callBack);
    void IQueryPrinterOptionsTaskCallBack(IQueryPrinterOptionsTaskCallBack callBack);
    void ISearchModelsTaskCallBack(ISearchModelsTaskCallBack callBack);
    void ISearchPrintersTaskCallBack(ISearchPrintersTaskCallBack callBack);
    void IUpdatePrinterCupsOptionsTaskCallBack(IUpdatePrinterCupsOptionsTaskCallBack callBack);
    void IUpdatePrinterOptionsTaskCallBack(IUpdatePrinterOptionsTaskCallBack callBack);

    void IAppCallBack(IAppCallBack callBack);

    void initFailed();

    void initSucceed();

    void sendRefreshJobsIntent();

}
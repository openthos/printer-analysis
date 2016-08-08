package com.github.openthos.printer.localprint.aidl;

import com.github.openthos.printer.localprint.model.ModelsItem;

interface ISearchModelsTaskCallBack {

    String bindPrinter();

    void onPostExecute(in ModelsItem modelsItem, String ERROR);

}

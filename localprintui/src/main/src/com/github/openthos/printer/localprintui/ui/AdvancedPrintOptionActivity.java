package com.github.openthos.printer.localprintui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.aidl.IQueryPrinterCupsOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IUpdatePrinterCupsOptionsTaskCallBack;
import com.github.openthos.printer.localprintui.APP;
import com.github.openthos.printer.localprintui.R;
import com.github.openthos.printer.localprint.model.PrinterCupsOptionItem;

import java.util.List;

public class AdvancedPrintOptionActivity extends BaseActivity {

    private static final String TAG = "AdvancedPrintOptionActivity";

    private TableLayout mTableLayoutOptions;
    public List<PrinterCupsOptionItem> mPrinterOptionItems;
    private Button mButtonOk;
    private Button mButtonCancel;
    private String mPrinterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2016/5/10 System printer service how to call AdvancedPrintOptionActivity

        Intent intent = getIntent();
        mPrinterName = intent.getStringExtra(APP.PRINTER_NAME);

        setContentView(R.layout.activity_advanced_print_option);
        mTableLayoutOptions = (TableLayout) findViewById(R.id.tableLayout_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mButtonOk = (Button) findViewById(R.id.button_ok);
        mButtonCancel = (Button) findViewById(R.id.button_cancel);

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boolean flag = false;
        try {
            flag = APP.remoteExec(new IQueryPrinterCupsOptionsTaskCallBack.Stub() {

                @Override
                public void onPostExecute(List printerOptionItems, String ERROR)
                        throws RemoteException {
                    if (printerOptionItems == null) {
                        Toast.makeText(AdvancedPrintOptionActivity.this,
                                getResources().getString(R.string.query_error) + " " + ERROR,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AdvancedPrintOptionActivity.this.mPrinterOptionItems = printerOptionItems;
                    addItems();
                }

                @Override
                public String bindStart() throws RemoteException {
                    return mPrinterName;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!flag) {
            Toast.makeText(this, R.string.connect_service_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void save() {

        Toast.makeText(AdvancedPrintOptionActivity.this, R.string.updating, Toast.LENGTH_SHORT).show();

        boolean flag = false;
        try {
            flag = APP.remoteExec(new IUpdatePrinterCupsOptionsTaskCallBack.Stub() {

                @Override
                public String getPrinter() throws RemoteException {
                    return mPrinterName;
                }

                @Override
                public void onPostExecute(boolean flag) throws RemoteException {
                    Toast.makeText(AdvancedPrintOptionActivity.this,
                            R.string.update_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AdvancedPrintOptionActivity.this,
                            ManagementActivity.class);
                    intent.putExtra(APP.TASK, APP.TASK_REFRESH_ADDED_PRINTERS);
                    AdvancedPrintOptionActivity.this.startActivity(intent);

                    finish();
                }

                @Override
                public List bindStart() throws RemoteException {
                    return mPrinterOptionItems;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!flag) {
            Toast.makeText(this, R.string.connect_service_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Add each of the configuration interface
     */
    private void addItems() {

        LayoutInflater inflater = LayoutInflater.from(AdvancedPrintOptionActivity.this);

        for (PrinterCupsOptionItem item : mPrinterOptionItems) {
            int def = item.getDef();
            String name = item.getName();
            List<String> list = item.getOption();

            TableRow row = (TableRow) inflater.inflate(R.layout.item_advanced_printer_option, null);
            mTableLayoutOptions.addView(row);
            TextView textView_option_name = (TextView) row.findViewById(R.id.textView_option_name);
            Spinner spinner_option = (Spinner) row.findViewById(R.id.spinner_option);

            textView_option_name.setText(name);
            spinner_option.setTag(item);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, list);
            spinner_option.setAdapter(adapter);
            spinner_option.setSelection(def);
            spinner_option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    PrinterCupsOptionItem optionItem
                            = (PrinterCupsOptionItem) ((View) view.getParent()).getTag();
                    optionItem.setDef2(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }

    @Override
    protected String bindTAG() {
        return TAG;
    }
}

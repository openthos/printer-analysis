package com.github.openthos.printer.localprint.ui;

import android.content.Intent;
import android.os.Bundle;
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

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterCupsOptionItem;
import com.github.openthos.printer.localprint.task.QueryPrinterCupsOptoinsTask;
import com.github.openthos.printer.localprint.task.UpdatePrinterCupsOptionsTask;

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

        QueryPrinterCupsOptoinsTask<Void> task = new QueryPrinterCupsOptoinsTask<Void>() {

            @Override
            protected void onPostExecute(List<PrinterCupsOptionItem> printerOptionItems) {
                if (printerOptionItems == null) {
                    Toast.makeText(AdvancedPrintOptionActivity.this,
                                   getResources().getString(R.string.query_error) + " " + ERROR,
                                   Toast.LENGTH_SHORT).show();
                    return;
                }

                AdvancedPrintOptionActivity.this.mPrinterOptionItems = printerOptionItems;
                addItems();
            }
        };
        task.start(mPrinterName);


    }

    private void save() {

        Toast.makeText(AdvancedPrintOptionActivity.this, R.string.updating, Toast.LENGTH_SHORT).show();

        UpdatePrinterCupsOptionsTask<Void> task = new UpdatePrinterCupsOptionsTask<Void>() {

            @Override
            protected String getPrinter() {
                return mPrinterName;
            }

            @Override
            protected void onPostExecute(Boolean flag) {
                Toast.makeText(AdvancedPrintOptionActivity.this,
                               R.string.update_success, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AdvancedPrintOptionActivity.this,
                                           ManagementActivity.class);
                intent.putExtra(APP.TASK, APP.TASK_REFRESH_ADDED_PRINTERS);
                AdvancedPrintOptionActivity.this.startActivity(intent);

                finish();
            }
        };

        task.start(mPrinterOptionItems);
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

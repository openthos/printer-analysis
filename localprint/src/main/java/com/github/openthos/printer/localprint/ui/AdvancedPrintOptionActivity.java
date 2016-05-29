package com.github.openthos.printer.localprint.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterOptionItem;
import com.github.openthos.printer.localprint.task.QueryPrinterOptonsTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedPrintOptionActivity extends BaseActivity {

    private static final String TAG = "AdvancedPrintOptionActivity";
    private TableLayout tableLayout_options;
    public List<PrinterOptionItem> printerOptionItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2016/5/10 AdvancedPrintOptionActivity 打印机高级设置



        setContentView(R.layout.activity_advanced_print_option);
        tableLayout_options = (TableLayout)findViewById(R.id.tableLayout_options);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();


        QueryPrinterOptonsTask<Void> task = new QueryPrinterOptonsTask<Void>() {

            @Override
            protected void onPostExecute(List<PrinterOptionItem> printerOptionItems) {
                if(printerOptionItems == null){
                    Toast.makeText(AdvancedPrintOptionActivity.this, getResources().getString(R.string.query_error) + " " + ERROR, Toast.LENGTH_SHORT).show();
                    return;
                }

                AdvancedPrintOptionActivity.this.printerOptionItems = printerOptionItems;
                addItems();
            }
        };
        task.start("HP_LaserJet_Professional_P1108");


    }

    /**
     * 添加每一项配置界面
     */
    private void addItems() {

        LayoutInflater inflater = LayoutInflater.from(AdvancedPrintOptionActivity.this);

        for(PrinterOptionItem item: printerOptionItems){
            int def = item.getDef();
            String name = item.getName();
            List<String> list = item.getOption();

            TableRow row = (TableRow) inflater.inflate(R.layout.item_advanced_printer_option, null);
            tableLayout_options.addView(row);
            TextView textView_option_name = (TextView) row.findViewById(R.id.textView_option_name);
            Spinner spinner_option = (Spinner) row.findViewById(R.id.spinner_option);

            textView_option_name.setText(item.getName());
            spinner_option.setTag(item);

            ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
            spinner_option.setAdapter(adapter);
            spinner_option.setSelection(item.getDef());
            spinner_option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    PrinterOptionItem optionItem = (PrinterOptionItem) ((View)view.getParent()).getTag();
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

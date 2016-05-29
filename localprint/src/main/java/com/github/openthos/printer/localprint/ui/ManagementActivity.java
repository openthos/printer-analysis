package com.github.openthos.printer.localprint.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.ModelsItem;
import com.github.openthos.printer.localprint.model.PPDItem;
import com.github.openthos.printer.localprint.task.AddPrinterTask;
import com.github.openthos.printer.localprint.task.SearchModelsTask;
import com.github.openthos.printer.localprint.ui.adapter.ManagementAdapter;
import com.github.openthos.printer.localprint.ui.adapter.ManagementListItem;
import com.github.openthos.printer.localprint.ui.fragment.ConfigPrinterDialogFragment;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagementActivity extends BaseActivity {

    private static final String TAG = "ManagementActivity";
    private boolean IS_DETECTING = false;       //是否正在检测新打印机
    private ListView listview;
    private ManagementAdapter adapter;
    private final List<ManagementListItem> listItem = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        LogUtils.d(TAG, "onNewIntent()");
        handlerIntent(getIntent());
    }

    private void init(){


        setContentView(R.layout.activity_management);
        listview = (ListView)findViewById(R.id.listView);
        adapter = new ManagementAdapter(this, listItem);
        adapter.initList();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ManagementListItem item = adapter.getItem(position);
                LogUtils.d(TAG, "onItemClick -> " + item.toString());
                if(item.getType() == ManagementListItem.TYPE_ADDED_PRINTER){
                    showConfigDialog(item);
                }else if(item.getType() == ManagementListItem.TYPE_LOCAL_PRINTER){
                    showAddLocalDialog(item);
                }
            }
        });
    }

    private void showConfigDialog(ManagementListItem item) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(ConfigPrinterDialogFragment.ITEM);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ConfigPrinterDialogFragment.newInstance(item.getPrinteritem());
        ft.add(newFragment, ConfigPrinterDialogFragment.ITEM);
        ft.commitAllowingStateLoss();
    }

    /**
     * 显示添加本地打印机Dialog
     * @param deviceItem
     */
    private void showAddLocalDialog(final ManagementListItem deviceItem) {

        final Map<String, List<PPDItem>> models = new HashMap<>();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        TextView tipName = new TextView(this);
        tipName.setText(R.string.set_name);
        final EditText name = new EditText(this);
        name.setText(deviceItem.getPrinteritem().getNickName());
        TextView tipBrand = new TextView(this);
        tipBrand.setText(R.string.select_brand);
        Spinner brand = new Spinner(this);
        final List<String> brandList = new ArrayList<String>();
        final ArrayAdapter<String> brandAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, brandList);
        brand.setAdapter(brandAdapter);

        TextView tipModel = new TextView(this);
        tipModel.setText(R.string.select_model);
        final Spinner model = new Spinner(this);
        final List<PPDItem> modelList = new ArrayList<PPDItem>();
        final ArrayAdapter<PPDItem> modelAdapter=new ArrayAdapter<PPDItem>(this, android.R.layout.simple_spinner_dropdown_item, modelList);
        model.setAdapter(modelAdapter);

        brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelList.clear();
                modelList.addAll(models.get(brandList.get(position)) );
                modelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layout.addView(tipName);
        layout.addView(name);
        layout.addView(tipBrand);
        layout.addView(brand);
        layout.addView(tipModel);
        layout.addView(model);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.add_a_local_printer)
                .setView(layout)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.show();

        //手动设置监听器，使得dialog点击不消失
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            private boolean PRESSED = false;

            @Override
            public void onClick(View v) {
                if(PRESSED){
                    Toast.makeText(ManagementActivity.this, R.string.adding, Toast.LENGTH_SHORT).show();
                    return;
                }

                //传入参数
                Map<String,String> p = new HashMap<>();
                p.put("name", name.getText().toString());
                p.put("model", modelList.get(model.getSelectedItemPosition()).getModel());
                p.put("url", deviceItem.getPrinteritem().getURL());

                PRESSED = true;

                AddPrinterTask<Void> task = new AddPrinterTask<Void>() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(ManagementActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();
                            adapter.refreshAddedPrinters();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ManagementActivity.this, R.string.add_fail, Toast.LENGTH_SHORT).show();
                        }
                        PRESSED = false;
                    }
                };

                task.start(p);

            }

        });


        new SearchModelsTask<Void, Void>(){
            @Override
            protected void onPostExecute(ModelsItem modelsItem) {

                if(modelsItem == null){
                    Toast.makeText(ManagementActivity.this, getResources().getString(R.string.query_error) + " " + ERROR, Toast.LENGTH_SHORT).show();
                    return;
                }

                brandList.addAll(modelsItem.getBrand());
                models.putAll(modelsItem.getModels());
                brandAdapter.notifyDataSetChanged();
            }
        }.start();


    }

    @Override
    protected String bindTAG() {
        return "ManagementActivity";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d(TAG, "onNewIntent()");
        handlerIntent(intent);
    }

    private void handlerIntent(Intent intent) {

        if(intent == null){
            return;
        }

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
        LogUtils.d(TAG, "Intent -> task = " + task);

        switch (task){
            case APP.TASK_ADD_NEW_PRINTER:
                detect_printers();
                break;
            case APP.TASK_REFRESH_ADDED_PRINTERS:
                adapter.refreshAddedPrinters();
                break;
            case APP.TASK_DEFAULT:
                break;
        }
    }

    /**
     * 搜索打印机
     */
    private void detect_printers() {

        if(IS_DETECTING){
            Toast.makeText(ManagementActivity.this, R.string.searching, Toast.LENGTH_SHORT).show();
            return;
        }

        IS_DETECTING = true;

        adapter.startDetecting();

    }

    /**
     * 设置是否正在检测
     * @param IS_DETECTING
     */
    public void setIS_DETECTING(boolean IS_DETECTING) {
        this.IS_DETECTING = IS_DETECTING;
    }

    @Override
    protected void onResume() {
        super.onResume();
        APP.MANAGEMENT_ACTIVITY_ON_TOP = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        APP.MANAGEMENT_ACTIVITY_ON_TOP = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ManagementActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_system_print_service){
            Intent intent = new Intent(Settings.ACTION_PRINT_SETTINGS);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

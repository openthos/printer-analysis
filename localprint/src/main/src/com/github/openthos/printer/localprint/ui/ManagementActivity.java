package com.github.openthos.printer.localprint.ui;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManagementActivity extends BaseActivity {

    private static final String TAG = "ManagementActivity";

    private final List<ManagementListItem> mListItem = new LinkedList<>();

    private ListView mListview;
    private ManagementAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        LogUtils.d(TAG, "onNewIntent()");
        handlerIntent(getIntent());
    }

    private void init() {

        setContentView(R.layout.activity_management);
        mListview = (ListView) findViewById(R.id.listView);
        mAdapter = new ManagementAdapter(this, mListItem);
        mAdapter.initList();
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ManagementListItem item = mAdapter.getItem(position);
                LogUtils.d(TAG, "onItemClick -> " + item.toString());
                if (item.getType() == ManagementListItem.TYPE_ADDED_PRINTER) {
                    showConfigDialog(item);
                } else if (item.getType() == ManagementListItem.TYPE_LOCAL_PRINTER) {
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
     * Show the Dialog of adding local printers.
     *
     * @param deviceItem ManagementListItem
     */
    private void showAddLocalDialog(final ManagementListItem deviceItem) {

        final Map<String, List<PPDItem>> models = new HashMap<>();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        TextView textViewTipName = new TextView(this);
        textViewTipName.setText(R.string.set_name);
        final EditText editTextName = new EditText(this);
        editTextName.setText(deviceItem.getPrinteritem().getNickName());
        TextView textViewTipBrand = new TextView(this);
        textViewTipBrand.setText(R.string.select_brand);
        Spinner spinnerBrand = new Spinner(this);
        final List<String> brandList = new ArrayList<String>();
        final ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this,
                                       android.R.layout.simple_spinner_dropdown_item, brandList);
        spinnerBrand.setAdapter(brandAdapter);

        TextView textViewTipModel = new TextView(this);
        textViewTipModel.setText(R.string.select_model);
        final Spinner spinnerModel = new Spinner(this);
        final List<PPDItem> modelList = new ArrayList<PPDItem>();
        final ArrayAdapter<PPDItem> modelAdapter = new ArrayAdapter<PPDItem>(this,
                                        android.R.layout.simple_spinner_dropdown_item, modelList);
        spinnerModel.setAdapter(modelAdapter);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelList.clear();
                modelList.addAll(models.get(brandList.get(position)));
                modelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final CheckBox cbxSharePrinter = new CheckBox(this);
        cbxSharePrinter.setText(R.string.share_printer);

        layout.addView(textViewTipName);
        layout.addView(editTextName);
        layout.addView(textViewTipBrand);
        layout.addView(spinnerBrand);
        layout.addView(textViewTipModel);
        layout.addView(spinnerModel);
        layout.addView(cbxSharePrinter);

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this).setTitle(R.string.add_a_local_printer)
                .setView(layout)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_frame_shadow);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.primary_text_dark));
        //Manually set the listener, aim to click dialog does not disappear
        final Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setEnabled(false);
        buttonPositive.setTextColor(getResources().getColor(R.color.primary_button_light));
        buttonPositive.setOnClickListener(new View.OnClickListener() {
            private boolean PRESSED = false;

            @Override
            public void onClick(View v) {
                if (PRESSED) {
                    Toast.makeText(ManagementActivity.this,R.string.adding,
                                   Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextName.getText().toString().isEmpty()) {
                    Toast.makeText(ManagementActivity.this,
                                   R.string.name_error_with_null,
                                   Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextName.getText().toString().contains(" ")) {
                    Toast.makeText(ManagementActivity.this,
                                   R.string.name_error_with_space,
                                   Toast.LENGTH_SHORT).show();
                    return;
                }

                PRESSED = true;

                Map<String, String> p = new HashMap<>();
                p.put("name", editTextName.getText().toString());
                p.put("model",
                        modelList.get(spinnerModel.getSelectedItemPosition()).getModel());
                p.put("url", deviceItem.getPrinteritem().getURL());
                p.put("isShare", cbxSharePrinter.isChecked() ? "true" : "false");

                AddPrinterTask<Void> task = new AddPrinterTask<Void>() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(ManagementActivity.this,
                                           R.string.add_success, Toast.LENGTH_SHORT).show();
                            mAdapter.refreshAddedPrinters();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ManagementActivity.this,
                                           R.string.add_fail, Toast.LENGTH_SHORT).show();
                        }
                        PRESSED = false;
                    }
                };

                task.start(p);

            }

        });

        new SearchModelsTask<Void, Void>() {
            @Override
            protected void onPostExecute(ModelsItem modelsItem) {

                if (modelsItem == null) {
                    Toast.makeText(ManagementActivity.this,
                                   getResources().getString(R.string.query_error)
                                   + " " + ERROR, Toast.LENGTH_SHORT).show();
                    return;
                }
                buttonPositive.setEnabled(true);
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

        if (intent == null) {
            return;
        }

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
        LogUtils.d(TAG, "Intent -> task = " + task);

        switch (task) {
            case APP.TASK_ADD_NEW_PRINTER:
                detectPrinters();
                break;
            case APP.TASK_REFRESH_ADDED_PRINTERS:
                mAdapter.refreshAddedPrinters();
                break;
            case APP.TASK_ADD_NEW_NET_PRINTER:
                addNetPrinter();
                break;
            case APP.TASK_DEFAULT:
                break;
        }
    }

    private void addNetPrinter() {
        final Map<String, List<PPDItem>> models = new HashMap<>();

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        TextView textViewTipName = new TextView(this);
        textViewTipName.setText(R.string.set_name);
        textViewTipName.setTextColor(Color.BLACK);
        final EditText editTextName = new EditText(this);
        editTextName.setText("netprinter");

        TextView textViewTipURL = new TextView(this);
        textViewTipURL.setText(R.string.set_netprinter_url);
        textViewTipURL.setTextColor(Color.BLACK);
        textViewTipURL.setError(getString(R.string.hint_windows_netprinter) + "\n"
                                + getString(R.string.hint_Linux_netprinter) + "\n"
                                + getString(R.string.hint_built_in_netprinter) + "\n"
                                + getString(R.string.hint_other_printer));
        textViewTipURL.setFocusable(true);
        textViewTipURL.setClickable(true);
        textViewTipURL.setFocusableInTouchMode(true);
        final EditText editTextUrl = new EditText(this);

        TextView textViewTipBrand = new TextView(this);
        textViewTipBrand.setText(R.string.select_brand);
        textViewTipBrand.setTextColor(Color.BLACK);
        Spinner spinnerBrand = new Spinner(this);
        final List<String> brandList = new ArrayList<String>();
        final ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, brandList);
        spinnerBrand.setAdapter(brandAdapter);

        TextView textViewTipModel = new TextView(this);
        textViewTipModel.setText(R.string.select_model);
        textViewTipModel.setTextColor(Color.BLACK);
        final Spinner spinnerModel = new Spinner(this);
        final List<PPDItem> modelList = new ArrayList<PPDItem>();
        final ArrayAdapter<PPDItem> modelAdapter = new ArrayAdapter<PPDItem>(this,
                android.R.layout.simple_spinner_dropdown_item, modelList);
        spinnerModel.setAdapter(modelAdapter);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelList.clear();
                modelList.addAll(models.get(brandList.get(position)));
                modelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final CheckBox cbxSharePrinter = new CheckBox(this);
        cbxSharePrinter.setText(R.string.share_printer);

        layout.addView(textViewTipName);
        layout.addView(editTextName);
        layout.addView(textViewTipURL);
        layout.addView(editTextUrl);
        layout.addView(textViewTipBrand);
        layout.addView(spinnerBrand);
        layout.addView(textViewTipModel);
        layout.addView(spinnerModel);
        layout.addView(cbxSharePrinter);

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this).setTitle(R.string.add_a_network_printer)
                .setView(layout)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_frame_shadow);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.primary_text_dark));
        final Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setEnabled(false);
        buttonPositive.setTextColor(getResources().getColor(R.color.primary_button_light));
        buttonPositive.setOnClickListener(new View.OnClickListener() {
            private boolean CLICKED = false;

            @Override
            public void onClick(View v) {
                if (CLICKED) {
                    Toast.makeText(ManagementActivity.this, R.string.adding,
                                   Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextName.getText().toString().isEmpty()) {
                    Toast.makeText(ManagementActivity.this,
                                   R.string.name_error_with_null, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextName.getText().toString().contains(" ")) {
                    Toast.makeText(ManagementActivity.this,
                                   R.string.name_error_with_space, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (editTextUrl.getText().toString().isEmpty()) {
                    Toast.makeText(ManagementActivity.this,
                                   R.string.url_error_with_null, Toast.LENGTH_SHORT).show();
                    return;
                }

                CLICKED = true;

                Map<String, String> p = new HashMap<>();
                p.put("name", editTextName.getText().toString());
                p.put("model", modelList.get(spinnerModel.getSelectedItemPosition()).getModel());
                p.put("url", editTextUrl.getText().toString());
                p.put("isShare", cbxSharePrinter.isChecked() ? "true" : "false");

                AddPrinterTask<Void> task = new AddPrinterTask<Void>() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Toast.makeText(ManagementActivity.this, R.string.add_success,
                                    Toast.LENGTH_SHORT).show();
                            mAdapter.refreshAddedPrinters();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ManagementActivity.this, R.string.add_fail,
                                    Toast.LENGTH_SHORT).show();
                        }
                        CLICKED = false;
                    }
                };
                task.start(p);
            }
        });

        new SearchModelsTask<Void, Void>() {
            @Override
            protected void onPostExecute(ModelsItem modelsItem) {
                if (modelsItem == null) {
                    Toast.makeText(ManagementActivity.this, R.string.query_error + " " + ERROR,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                buttonPositive.setEnabled(true);
                brandList.addAll(modelsItem.getBrand());
                models.putAll(modelsItem.getModels());
                brandAdapter.notifyDataSetChanged();
            }
        }.start();

    }

    private void detectPrinters() {

        mAdapter.startDetecting();

    }

    @Override
    protected void onResume() {
        super.onResume();
        APP.IS_MANAGEMENT_ACTIVITY_ON_TOP = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        APP.IS_MANAGEMENT_ACTIVITY_ON_TOP = false;
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

        if (id == R.id.action_print_job) {
            Intent intent = new Intent(ManagementActivity.this, JobManagerActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(ManagementActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_system_print_service) {
            Intent intent = new Intent(Settings.ACTION_PRINT_SETTINGS);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

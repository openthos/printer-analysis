package com.github.openthos.printer.localprint.ui.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterItem;
import com.github.openthos.printer.localprint.model.PrinterOptionItem;
import com.github.openthos.printer.localprint.task.DeletePrinterTask;
import com.github.openthos.printer.localprint.task.PrintTask;
import com.github.openthos.printer.localprint.task.QueryPrinterOptionsTask;
import com.github.openthos.printer.localprint.task.UpdatePrinterOptionsTask;
import com.github.openthos.printer.localprint.ui.AdvancedPrintOptionActivity;
import com.github.openthos.printer.localprint.ui.ManagementActivity;

import java.util.HashMap;
import java.util.Map;

public class ConfigPrinterDialogFragment extends DialogFragment {

    public static final String ITEM = "item";

    private boolean IS_UPDATING = false;
    private boolean IS_DELETEING = false;

    private PrinterItem mItem;
    private Button mButtonCancel;
    private Button mButtonOk;
    private PrinterOptionItem mOptionItem;
    private ArrayAdapter<String> mSpinnerColorAdapter;
    private ArrayAdapter<String> mMediaSizeAdapter;
    private ArrayAdapter<String> mSpinnerDuplexAdapter;
    private Spinner mSpinnerMediaSize;
    private Spinner mSpinnerColorMode;
    private Spinner mSpinnerDuplexMode;
    private CheckBox mCheckboxSharePrinter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog_config_printer, container, false);

        //getDialog().setTitle(mItem.getNickName());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        //toolbar.setTitle(R.string.printer_setting);
        toolbar.setTitle(mItem.getNickName());
        toolbar.inflateMenu(R.menu.menu_config_printer_dialog);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    delete();
                } else if (item.getItemId() == R.id.action_test_page) {
                    testPage();
                } else if (item.getItemId() == R.id.action_tuning) {
                    tuning();
                }

                return true;
            }
        });

        mButtonCancel = (Button) v.findViewById(R.id.button_cancel);
        mButtonOk = (Button) v.findViewById(R.id.button_ok);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigPrinterDialogFragment.this.dismiss();
            }
        });
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        mButtonOk.setEnabled(false);

        mSpinnerMediaSize = (Spinner) v.findViewById(R.id.spinner_media_size);
        mSpinnerColorMode = (Spinner) v.findViewById(R.id.spinner_color_mode);

        /**
         * Have not handle it temporarily.
         */
        mSpinnerDuplexMode = (Spinner) v.findViewById(R.id.spinner_duplex_mode);
        mCheckboxSharePrinter = (CheckBox) v.findViewById(R.id.checkbox_shareprinter);
        mCheckboxSharePrinter.setOnCheckedChangeListener(
                                  new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOptionItem.setmSharePrinter(b);
            }
        });


        initData();

        return v;
    }

    private void initData() {
        QueryPrinterOptionsTask<Void> task = new QueryPrinterOptionsTask<Void>() {
            @Override
            protected void onPostExecute(PrinterOptionItem printerOptionItem) {

                mOptionItem = printerOptionItem;

                mCheckboxSharePrinter.setChecked(mOptionItem.ismSharePrinter());
                mCheckboxSharePrinter.setClickable(false);

                mMediaSizeAdapter = new ArrayAdapter<String>(getActivity()
                        , android.R.layout.simple_spinner_dropdown_item
                        , mOptionItem.getMediaSizeCupsList()) {

                    @Override
                    public String getItem(int position) {
                        return mOptionItem.getMediaSizeList().get(position).getId();
                    }
                };

                mSpinnerColorAdapter = new ArrayAdapter<String>(getActivity()
                        , android.R.layout.simple_spinner_dropdown_item
                        , mOptionItem.getColorModeCupsList()) {
                    @Override
                    public String getItem(int position) {
                        String colorMode = getResources().getString(R.string.black_white);
                        if (mOptionItem.getColorModeList().get(position)
                                == PrintAttributes.COLOR_MODE_COLOR) {
                            colorMode = getResources().getString(R.string.color);
                        }
                        return colorMode;
                    }
                };

                mSpinnerDuplexAdapter = new ArrayAdapter<>(getActivity(),
                                                 android.R.layout.simple_spinner_dropdown_item,
                                                 new String[]{getString(R.string.no_double_side)});

                mSpinnerMediaSize.setAdapter(mMediaSizeAdapter);
                mSpinnerColorMode.setAdapter(mSpinnerColorAdapter);
                mSpinnerDuplexMode.setAdapter(mSpinnerDuplexAdapter);

                mSpinnerMediaSize.setOnItemSelectedListener(
                                      new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView,
                                               View view, int i, long l) {
                        mOptionItem.setMediaSizeSelected(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                mSpinnerColorMode.setOnItemSelectedListener(
                                      new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView,
                                               View view, int i, long l) {
                        mOptionItem.setColorModeSelected(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                mSpinnerDuplexMode.setOnItemSelectedListener(
                                       new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView,
                                    View view, int i, long l) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                mSpinnerMediaSize.setSelection(mOptionItem.getMediaSizeSelected());
                mSpinnerColorMode.setSelection(mOptionItem.getColorModeSelected());

                mButtonOk.setEnabled(true);
            }
        };

        task.start(mItem.getNickName());
    }

    /**
     * Enter advaneced options.
     */
    private void tuning() {
        Intent intent = new Intent(getActivity(), AdvancedPrintOptionActivity.class);
        intent.putExtra(APP.PRINTER_NAME, mItem.getNickName());
        getActivity().startActivity(intent);

        dismissNoUpdate();
    }

    /**
     * Print a test page.
     */
    private void testPage() {
        PrintTask<Void> task = new PrintTask<Void>() {
            @Override
            protected String bindFileName() {
                return null;
            }

            @Override
            protected String bindPrinterName() {
                return mItem.getNickName();
            }

            @Override
            protected void onPostExecute(String jobId) {
                if (jobId == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.print_error)
                            + " " + ERROR, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.printing, Toast.LENGTH_SHORT).show();
                }

            }
        };

        Map<String, String> map = new HashMap<>();
        map.put(PrintTask.LP_PRINTER, mItem.getNickName());
        //map.put(PrintTask.LP_FILE, "/docu3.pdf");
        map.put(PrintTask.LP_FILE, "/usr/share/cups/data/testprint");

        task.start(map);
        Toast.makeText(getActivity(), R.string.start_printing, Toast.LENGTH_SHORT).show();
    }

    /**
     * delete the printer
     */
    private void delete() {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (IS_DELETEING) {
                            Toast.makeText(getActivity(), R.string.deleting, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DeletePrinterTask<Void> task = new DeletePrinterTask<Void>() {
                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                if (aBoolean) {
                                    Toast.makeText(getActivity(), R.string.deleted_successfully
                                            , Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.delete_failed
                                            , Toast.LENGTH_SHORT).show();
                                }
                                IS_DELETEING = false;
                                dismiss();
                            }
                        };
                        task.start(mItem.getNickName());
                        IS_DELETEING = true;

                    }
                })
                .create().show();


    }

    private void save() {

        if (IS_UPDATING) {
            Toast.makeText(getActivity(), R.string.updating, Toast.LENGTH_SHORT).show();
            return;
        }

        UpdatePrinterOptionsTask<Void> task = new UpdatePrinterOptionsTask<Void>() {

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                IS_UPDATING = false;

                if (aBoolean) {
                    Toast.makeText(getActivity(), R.string.update_success,
                                   Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.update_failed) + " "
                            + ERROR, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String getPrinter() {
                return mItem.getNickName();
            }
        };
        task.start(mOptionItem);
        IS_UPDATING = true;
    }

    @Override
    public void dismiss() {
        //avoid report : Can not perform this action after onSaveInstanceState.
        super.dismissAllowingStateLoss();
        Intent intent = new Intent(getActivity(), ManagementActivity.class);
        intent.putExtra(APP.TASK, APP.TASK_REFRESH_ADDED_PRINTERS);
        getActivity().startActivity(intent);
    }

    /**
     * Close the UI without updating ManagementActivity.
     */
    public void dismissNoUpdate() {
        //avoid report : Can not perform this action after onSaveInstanceState.
        super.dismissAllowingStateLoss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = getArguments().getParcelable(ITEM);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);
    }

    public static ConfigPrinterDialogFragment newInstance(PrinterItem item) {
        ConfigPrinterDialogFragment f = new ConfigPrinterDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(ITEM, item);
        f.setArguments(args);
        return f;
    }

}

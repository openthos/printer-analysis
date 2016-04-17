package com.github.openthos.printer.openthosprintservice.util;

import android.content.DialogInterface;
import android.app.AlertDialog;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;

/**
 * Created by bboxh on 2016/4/15.
 */
public class DialogUtils {

        private void showialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(APP.getApplicatioContext());
            builder.setTitle(R.string.print_service_notification)
                    .setMessage(R.string.ready_to_initialize)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //start_init();
                                }
                            }).start();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //exit();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }

/*    public static AlertDialog.Builder Builer(){
        AlertDialog.Builder builder = new AlertDialog.Builder(APP.getApplicatioContext());
        return builder;
    }*/

}

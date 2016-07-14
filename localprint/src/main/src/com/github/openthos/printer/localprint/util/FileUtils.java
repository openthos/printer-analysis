package com.github.openthos.printer.localprint.util;


import android.os.ParcelFileDescriptor;

import com.github.openthos.printer.localprint.APP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileUtils
 * Created by bboxh on 2016/4/12.
 */
public class FileUtils {


    private static final String TAG = "FileUtils";

    /**
     * @param docu_file_path String
     * @param data           ParcelFileDescriptor
     * @return success or failure
     */
    public static boolean copyFile(String docu_file_path, ParcelFileDescriptor data) {

        boolean flag = false;

        /*if (!printJob.isQueued()) {
            return;
        }*/

        File outfile = new File(docu_file_path);
        LogUtils.d(TAG, "copyfile ->" + docu_file_path);
        outfile.delete();

        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(data);

        byte[] bbuf = new byte[1024];

        //save the actual size of reading
        int hasRead = 0;

        try {

            FileOutputStream outStream = new FileOutputStream(outfile);

            while ((hasRead = file.read(bbuf)) > 0) {

                //Converts the byte array to a string and send out
                //System.out.print(new String(bbuf, 0, hasRead));
                outStream.write(bbuf);

            }

            flag = true;
            LogUtils.d(TAG, "copyfile finished");
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            //Close the stream in finally ,may be safety.
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return flag;

    }

    /**
     * Get the absolute path of the file to be printed
     *
     * @param s generate a file name by s
     * @return
     */
    public static String getDocuFilePath(String s) {
        return getComponentPath() + getDocuFileName(s);
    }

    public static String getDocuFileName(String s) {
        return "/" + s + "_" + APP.DOCU_FILE;
    }

    /**
     * Get the CUPS running path.
     *
     * @return
     */
    public static String getComponentPath() {
        return getFilePath() + APP.COMPONENT_PATH;
    }

    /**
     * Get the path of the app files.
     *
     * @return
     */
    public static String getFilePath() {
        return APP.getApplicatioContext().getFilesDir().getAbsolutePath();
    }
}

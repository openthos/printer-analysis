package com.github.openthos.printer.localprint.util;


import android.os.ParcelFileDescriptor;

import com.github.openthos.printer.localprint.APP;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by bboxh on 2016/4/12.
 */
public class FileUtils {


    private static final String TAG = "FileUtils";

    /**
     *
     * @param docu_file_path
     * @param data
     * @return 成功 失败
     */
    public static boolean copyFile(String docu_file_path,ParcelFileDescriptor data){

        boolean flag = false;

        /*if (!printJob.isQueued()) {
            return;
        }*/

        File outfile = new File(docu_file_path);
        LogUtils.d(TAG, "copyfile ->" + docu_file_path);
        outfile.delete();

        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(data);
        //创建一个长度为1024的内存空间
        byte[] bbuf = new byte[1024];

        //用于保存实际读取的字节数
        int hasRead = 0;
        //使用循环来重复读取数据
        try {

            FileOutputStream outStream = new FileOutputStream(outfile);

            while ((hasRead = file.read(bbuf)) > 0) {

                //将字节数组转换为字符串输出
                //System.out.print(new String(bbuf, 0, hasRead));
                outStream.write(bbuf);

            }

            flag = true;
            LogUtils.d(TAG, "copyfile finished");
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        }finally {
            //关闭文件输出流，放在finally块里更安全
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return flag;

    }

    /**
     * 获得待打印文件的绝对路径
     * @return
     * @param s 根据s值自动生成文件名
     */
    public static String getDocuFilePath(String s){
        return getComponentPath() + getDocuFileName(s) ;
    }

    public static String getDocuFileName(String s){
        return  "/" + s + "_" + APP.DOCU_FILE;
    }

    /**
     * 获得CUPS所在路径
     * @return
     */
    public static String getComponentPath(){
        return getFilePath() + APP.COMPONENT_PATH;
    }

    /**
     *获得文件存放路径
     * @return
     */
    public static String getFilePath() {
        return APP.getApplicatioContext().getFilesDir().getAbsolutePath();
    }
}

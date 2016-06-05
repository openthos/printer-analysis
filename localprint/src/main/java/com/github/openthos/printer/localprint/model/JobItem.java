package com.github.openthos.printer.localprint.model;

/**
 * 打印任务
 * Created by bboxh on 2016/6/4.
 */
public class JobItem {

    //发现新的状态时，再补充
    public static final int STATUS_READY = 1;         //就绪状态，排队打印
    public static final int STATUS_PRINTING = 2;      //打印中
    public static final int STATUS_HOLDING = 3;       //暂停
    public static final int STATUS_ERROR = 4;         //错误，需要在ERROR中叙述具体错误内容
    public static final int STATUS_WAITING_FOR_PRINTER = 5;   //等待打印机可用

    private String fileName;        //文档名
    private String printer;        //打印机
    private int status;        //状态
    private String size;        //文档大小
    private int jobId;        //任务编号
    private String ERROR = "";        //错误信息

    public JobItem() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    /**
     * 如果是 STATUS_ERROR 错误状态，还需要自行读取ERROR变量
     * @return
     */
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getERROR() {
        return ERROR;
    }

    public void setERROR(String ERROR) {
        this.ERROR = ERROR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobItem jobItem = (JobItem) o;

        if (status != jobItem.status) return false;
        if (jobId != jobItem.jobId) return false;
        if (fileName != null ? !fileName.equals(jobItem.fileName) : jobItem.fileName != null)
            return false;
        if (printer != null ? !printer.equals(jobItem.printer) : jobItem.printer != null)
            return false;
        if (size != null ? !size.equals(jobItem.size) : jobItem.size != null) return false;
        return ERROR != null ? ERROR.equals(jobItem.ERROR) : jobItem.ERROR == null;

    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (printer != null ? printer.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + jobId;
        result = 31 * result + (ERROR != null ? ERROR.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JobItem{" +
                "fileName='" + fileName + '\'' +
                ", printer='" + printer + '\'' +
                ", status=" + status +
                ", size='" + size + '\'' +
                ", jobId=" + jobId +
                ", ERROR='" + ERROR + '\'' +
                '}';
    }
}

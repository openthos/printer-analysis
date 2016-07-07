package com.github.openthos.printer.localprint.model;

/**
 * Printer job item.
 * Created by bboxh on 2016/6/4.
 */
public class JobItem {

    /**
     * Add more when we found a new state.
     * STATUS_READY                 Ready to print , may be in the queue.
     * STATUS_PRINTING              Printing.
     * STATUS_HOLDING               Pause/Hold.
     * STATUS_ERROR                 Error occurred , more info in ERROR variable.
     * STATUS_WAITING_FOR_PRINTER   Waiting for printing available.
     */
    public static final int STATUS_READY = 1;
    public static final int STATUS_PRINTING = 2;
    public static final int STATUS_HOLDING = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_WAITING_FOR_PRINTER = 5;

    private String fileName;
    private String printer;
    private int status;
    private String size;
    private int jobId;
    private String ERROR = "";

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
     * Need to read ERROR variable manually for more details
     * when the status is STATUS_ERROR or STATUS_PRINTING.
     *
     * @return status
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

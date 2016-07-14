package com.github.openthos.printer.localprint.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.JobItem;
import com.github.openthos.printer.localprint.task.JobCancelTask;
import com.github.openthos.printer.localprint.task.JobPauseTask;
import com.github.openthos.printer.localprint.task.JobResumeTask;

import java.util.List;

/**
 * Jobs' List adapter
 * Created by bboxh on 2016/6/5.
 */
public class JobAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<JobItem> mList;

    public JobAdapter(Context context, List<JobItem> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder;
        final JobItem item = mList.get(position);

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_job, null);

            holder = new Holder();

            holder.textview_name = (TextView) convertView.findViewById(R.id.textview_name);
            holder.textview_device = (TextView) convertView.findViewById(R.id.textview_device);
            holder.textview_status = (TextView) convertView.findViewById(R.id.textview_status);
            holder.textview_size = (TextView) convertView.findViewById(R.id.textview_size);
            holder.button_pause = (Button) convertView.findViewById(R.id.button_pause);
            holder.button_remove = (Button) convertView.findViewById(R.id.button_remove);

            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.textview_name.setText(item.getFileName());
        holder.textview_device.setText(item.getPrinter());
        holder.textview_size.setText(item.getSize());

        int status = item.getStatus();
        switch (status) {
            case JobItem.STATUS_ERROR:
                holder.textview_status.setText(mContext.getResources().getString(R.string.error)
                        + " " + mList.get(position).getERROR());
                break;
            case JobItem.STATUS_HOLDING:
                holder.textview_status.setText(R.string.pause);
                break;
            case JobItem.STATUS_PRINTING:
                holder.textview_status.setText(R.string.printing);
                break;
            case JobItem.STATUS_READY:
                holder.textview_status.setText(R.string.ready);
                break;
            case JobItem.STATUS_WAITING_FOR_PRINTER:
                holder.textview_status.setText(R.string.waiting_for_printer);
                break;
            default:
                holder.textview_status.setText(R.string.unknown);
                break;
        }

        //判断状态
        if (item.getStatus() == JobItem.STATUS_HOLDING) {
            holder.button_pause.setText(R.string.resume);
        } else {
            holder.button_pause.setText(R.string.pause);
        }

        holder.button_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getStatus() == JobItem.STATUS_HOLDING) {
                    resumeJob(item, v);
                } else {
                    pauseJob(item, v);
                }
            }
        });

        holder.button_remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                removeJob(item);
            }
        });

        return convertView;

    }

    private void resumeJob(final JobItem jobItem, final View v) {
        JobResumeTask<Void> task = new JobResumeTask<Void>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    //Toast.makeText(mContext, R.string.resumed, Toast.LENGTH_SHORT).show();
                    jobItem.setStatus(JobItem.STATUS_READY);
                    Button button = (Button) v;
                    button.setText(R.string.pause);
                } else {
                    Toast.makeText(mContext, R.string.resume_error, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.start(jobItem);
    }

    private void removeJob(JobItem jobItem) {
        JobCancelTask<Void> task = new JobCancelTask<Void>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    //Toast.makeText(mContext, R.string.canceled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, R.string.cancel_error, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.start(jobItem);
    }

    private void pauseJob(final JobItem jobItem, final View v) {
        JobPauseTask<Void> task = new JobPauseTask<Void>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    //Toast.makeText(mContext, R.string.paused, Toast.LENGTH_SHORT).show();
                    jobItem.setStatus(JobItem.STATUS_HOLDING);
                    Button button = (Button) v;
                    button.setText(R.string.resume);
                } else {
                    Toast.makeText(mContext, R.string.pause_error, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.start(jobItem);
    }


    class Holder {
        TextView textview_name;
        TextView textview_device;
        TextView textview_status;
        TextView textview_size;
        Button button_pause;
        Button button_remove;
    }

}

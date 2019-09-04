package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.model.LogModel;

import java.util.List;

/**
 * Created by Daryll Sabate on 10/23/2017.
 */
public class LogsAdapter extends BaseAdapter {
    private Context c;
    List<LogModel> logModelList;
    TextView tvDateTime, tvMessage;

    public LogsAdapter(Context c, List<LogModel> logModelList) {
        this.c = c;
        this.logModelList = logModelList;
    }

    @Override
    public int getCount() {
        return logModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return logModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return logModelList.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int[] listItemBackground = new int[]{R.drawable.bg_odd, R.drawable.bg_even};

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_log, null);
        }
        tvDateTime = (TextView) convertView.findViewById(R.id.tvDateTime);
        tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
        // SET DATA TO THEM
        tvDateTime.setText(logModelList.get(position).getDateTime());
        tvMessage.setText(logModelList.get(position).getMessage());

        int colorPos = position % listItemBackground.length;
        convertView.setBackgroundResource(listItemBackground[colorPos]);
        return convertView;
    }
}

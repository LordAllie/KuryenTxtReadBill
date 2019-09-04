package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.model.RateMasterStatus;

import java.util.List;

/**
 * Created by Daryll Sabate on 10/23/2017.
 */
public class RateWarningAdapter extends BaseAdapter {
    private Context c;
    List<RateMasterStatus> masterStatuses;
    TextView rateName;

    public RateWarningAdapter(Context c, List<RateMasterStatus> masterStatuses) {
        this.c = c;
        this.masterStatuses = masterStatuses;
    }

    @Override
    public int getCount() {
        return masterStatuses.size();
    }

    @Override
    public Object getItem(int position) {
        return masterStatuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return masterStatuses.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_rate_name, null);
        }
        rateName = (TextView) convertView.findViewById(R.id.tvRateName);
        rateName.setText(masterStatuses.get(position).getRateName());
        return convertView;
    }
}

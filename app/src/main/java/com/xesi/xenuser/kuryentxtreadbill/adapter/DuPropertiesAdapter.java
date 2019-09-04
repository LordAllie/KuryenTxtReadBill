package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.model.download.DUProperty;

import java.util.List;

/**
 * Created by xenuser on 6/23/2016.
 */
public class DuPropertiesAdapter extends BaseAdapter {
    private Context c;
    List<DUProperty> duProperties;
    TextView tvPropertyName, tvPropertyValue;

    public DuPropertiesAdapter(Context c, List<DUProperty> duProperties) {
        this.c = c;
        this.duProperties = duProperties;
    }

    @Override
    public int getCount() {
        return duProperties.size();
    }

    @Override
    public Object getItem(int position) {
        return duProperties.get(position);
    }

    @Override
    public long getItemId(int position) {
        return duProperties.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int[] listItemBackground = new int[]{R.drawable.bg_odd, R.drawable.bg_even};

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_du_properties, null);
        }

        tvPropertyName = (TextView) convertView.findViewById(R.id.tvPropertyName);
        tvPropertyValue = (TextView) convertView.findViewById(R.id.tvPropertyValue);

        // SET DATA TO THEM
        tvPropertyName.setText(duProperties.get(position).getPropertyName());
        tvPropertyValue.setText(duProperties.get(position).getPropertyValue());

        int colorPos = position % listItemBackground.length;
        convertView.setBackgroundResource(listItemBackground[colorPos]);
        return convertView;
    }
}

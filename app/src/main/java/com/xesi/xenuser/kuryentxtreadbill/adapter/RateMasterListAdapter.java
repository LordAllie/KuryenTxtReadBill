package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.model.download.RateMaster;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import java.util.List;

/**
 * Created by xenuser on 6/23/2016.
 */
public class RateMasterListAdapter extends BaseAdapter {
    private Context c;
    private UniversalHelper helper;
    List<RateMaster> rateMasterDetailsList;
    TextView tvrateName, tvPerkwCharge, tvFixedCharge;

    public RateMasterListAdapter(Context c, List<RateMaster> rateMasterDetailsList) {
        this.c = c;
        this.rateMasterDetailsList = rateMasterDetailsList;
        this.helper = new UniversalHelper(c);
    }

    @Override
    public int getCount() {
        return rateMasterDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return rateMasterDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rateMasterDetailsList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int[] listItemBackground = new int[]{R.drawable.bg_odd, R.drawable.bg_even};

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_rate_master, null);
        }

        tvrateName = (TextView) convertView.findViewById(R.id.tvrateName);
        tvPerkwCharge = (TextView) convertView.findViewById(R.id.tvPerkwCharge);
        tvFixedCharge = (TextView) convertView.findViewById(R.id.tvFixedCharge);


        // SET DATA TO THEM
        tvrateName.setText(rateMasterDetailsList.get(position).getRateName());
        tvPerkwCharge.setText(helper.dformat.format(rateMasterDetailsList.get(position).getTotalPerKwCharge()));
        tvFixedCharge.setText(helper.dformat.format(  rateMasterDetailsList.get(position).getTotalFixedCharge()));

        int colorPos = position % listItemBackground.length;
        convertView.setBackgroundResource(listItemBackground[colorPos]);
        return convertView;
    }
}

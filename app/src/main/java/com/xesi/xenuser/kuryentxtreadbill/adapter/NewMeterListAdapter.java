package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.model.NewMeterModel;

import java.util.List;

/**
 * Created by Daryll Sabate on 7/24/2017.
 */
public class NewMeterListAdapter extends BaseAdapter {
    private GenericDao genericDao;
    private Context context;
    private List<NewMeterModel> newMeterModelList;
    TextView tvRouteCode, tvMsn, tvMeterReading, tvDate, tvTime;
    private TextView tvIsUploaded;

    public NewMeterListAdapter(Context context, List<NewMeterModel> newMeterModelList) {
        this.context = context;
        this.genericDao = new GenericDao(context);
        this.newMeterModelList = newMeterModelList;
    }

    @Override
    public int getCount() {
        return newMeterModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return newMeterModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return newMeterModelList.indexOf(position);
    }

    public String getSelectedItem(int pos) {
        return newMeterModelList.get(pos).getMsn();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int[] listItemBackground = new int[]{R.drawable.bg_odd, R.drawable.bg_even};
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_list_new_meter, null);
        }

        tvRouteCode = (TextView) convertView.findViewById(R.id.tvRouteCode);
        tvMsn = (TextView) convertView.findViewById(R.id.tvMsn);
        tvMeterReading = (TextView) convertView.findViewById(R.id.tvMeterReading);
        tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        tvIsUploaded = (TextView) convertView.findViewById(R.id.tvIsUploaded);
        String routeCode = genericDao.getOneField("DISTINCT(routeCode)","arm_account","WHERE idRoute=", String.valueOf(newMeterModelList.get(position).getIdRoute()),"limit 1","");
        // SET DATA TO THEM
        tvRouteCode.setText(routeCode);
        tvMsn.setText(newMeterModelList.get(position).getMsn());
        tvMeterReading.setText(Integer.toString(newMeterModelList.get(position).getReading()));
        tvDate.setText(newMeterModelList.get(position).getDateRead());
        tvTime.setText(newMeterModelList.get(position).getTimeRead());
        tvIsUploaded.setText(Integer.toString(newMeterModelList.get(position).getIsUploaded()));

        int colorPos = position % listItemBackground.length;
        convertView.setBackgroundResource(listItemBackground[colorPos]);
        genericDao.close();

        return convertView;
    }

}

package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.NewMeter;
import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.model.RouteObj;

import java.util.List;

/**
 * Created by Daryll Sabate on 7/21/2017.
 */
public class MySpinnerAdapter extends ArrayAdapter<RouteObj> {

    private Context context;
    private List<RouteObj> routeObjs;

    public MySpinnerAdapter(Context context, int textViewResourceId,
                            List<RouteObj> routeObjs) {
        super(context, textViewResourceId,  routeObjs);
        this.context = context;
        this.routeObjs = routeObjs;
    }

    public int getCount(){
        return routeObjs.size();
    }

    public RouteObj getItem(int position){
        return routeObjs.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextSize(16f);
        label.setTextColor(Color.BLACK);
        label.setPadding(10,0,10,0);
        label.setText(routeObjs.get(position).getRouteCode());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextSize(16f);
        label.setTextColor(Color.BLACK);
        label.setPadding(10,10,10,10);
        label.setText(routeObjs.get(position).getRouteCode());
        return label;
    }
}

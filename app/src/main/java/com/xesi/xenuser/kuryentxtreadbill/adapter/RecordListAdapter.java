package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 6/21/2016.
 */
public class RecordListAdapter extends BaseAdapter implements Filterable {
    private Context c;
    List<BillHeader> billHeaders;
    //    AcctFilter filter;
    List<BillHeader> filterList;
    LinearLayout llAcctBg;
    TextView tvAcctNo, tvAcctName, tvmeterNo, tvDate, tvReading, tvReports, tvIsPrinted;
    private BillFilter filter;


    public RecordListAdapter(Context c, List<BillHeader> billHeaders) {
        this.c = c;
        this.billHeaders = billHeaders;
        this.filterList = billHeaders;
    }

    @Override
    public int getCount() {
        return billHeaders.size();
    }

    @Override
    public Object getItem(int position) {
        return billHeaders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return billHeaders.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int[] listItemBackground = new int[]{R.drawable.bg_odd, R.drawable.bg_even};

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_account_record, null);
        }
        llAcctBg = (LinearLayout) convertView.findViewById(R.id.llAcctBg);
        tvIsPrinted = (TextView) convertView.findViewById(R.id.tvIsPrinted);
        tvAcctNo = (TextView) convertView.findViewById(R.id.tvAcctNo);
        tvAcctName = (TextView) convertView.findViewById(R.id.tvAcctName);
        tvmeterNo = (TextView) convertView.findViewById(R.id.tvmeterNo);
        tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        tvReading = (TextView) convertView.findViewById(R.id.tvReading);
        tvReports = (TextView) convertView.findViewById(R.id.tvReports);

        // SET DATA TO THEM
        if (billHeaders.get(position).getIsUploaded() == 1) {
            llAcctBg.setBackgroundColor(Color.DKGRAY);
            tvAcctName.setTextColor(Color.WHITE);
            tvAcctName.setText(WordUtils.capitalizeFully(billHeaders.get(position).getAcctName()));
        } else if (billHeaders.get(position).getIsPrinted() == 1 && billHeaders.get(position).getIsUploaded() == 0) {
            llAcctBg.setBackgroundResource(R.drawable.border_bottom);
            //tvAcctName.setTextColor(Color.BLUE);
            tvAcctName.setTextColor(Color.parseColor("#08639A"));
            tvAcctName.setText(WordUtils.capitalizeFully(billHeaders.get(position).getAcctName()));

        } else if (billHeaders.get(position).getIsPrinted() == 0 && billHeaders.get(position).getIsUploaded() == 0) {
            llAcctBg.setBackgroundResource(R.drawable.border_bottom);
            tvAcctName.setTextColor(Color.RED);
            tvAcctName.setTextColor(Color.parseColor("#810c38"));
            tvAcctName.setText(WordUtils.capitalizeFully(billHeaders.get(position).getAcctName()));
        }

        if (billHeaders.get(position).getIsPrinted() == 1) {
            tvIsPrinted.setText("YES");
            tvIsPrinted.setTextColor(Color.parseColor("#003300"));
        } else {
            tvIsPrinted.setText("NO");
            tvIsPrinted.setTextColor(Color.RED);
        }

        tvAcctNo.setText(billHeaders.get(position).getOldAccountNo());
        tvmeterNo.setText(billHeaders.get(position).getMeterNo());
        tvDate.setText(billHeaders.get(position).getRunDate());
        tvReading.setText(Double.toString(billHeaders.get(position).getCurReading()));
        tvReports.setText(billHeaders.get(position).getRemarks());

        int colorPos = position % listItemBackground.length;
        convertView.setBackgroundResource(listItemBackground[colorPos]);
        return convertView;
    }

    public String getSelectedItem(int pos) {
        return billHeaders.get(pos).getBillNo();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new BillFilter();
        }
        return filter;
    }

    class BillFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toLowerCase();
                ArrayList<BillHeader> filters = new ArrayList<>();

                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getBillNo().toLowerCase().contains(constraint)
                            || filterList.get(i).getOldAccountNo().toLowerCase().contains(constraint)
                            || filterList.get(i).getAcctName().toLowerCase().contains(constraint)
                            || filterList.get(i).getMeterNo().toLowerCase().contains(constraint)
                            || filterList.get(i).getAcctNo().toLowerCase().contains(constraint)) {

                        BillHeader list = new BillHeader(filterList.get(i).getRunDate(), filterList.get(i).getBillNo(),
                                filterList.get(i).getOldAccountNo(), filterList.get(i).getSequenceNo(),
                                filterList.get(i).getAcctName(), filterList.get(i).getMeterNo(),
                                filterList.get(i).getAcctNo(), filterList.get(i).getCurReading(), filterList.get(i).getRemarks());
                        filters.add(list);
                    }

                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            billHeaders = (ArrayList<BillHeader>) results.values;
            notifyDataSetChanged();
        }
    }
}

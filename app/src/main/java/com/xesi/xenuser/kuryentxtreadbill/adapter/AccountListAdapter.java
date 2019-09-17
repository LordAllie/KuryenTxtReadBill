package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xenuser on 5/17/2016.
 */
public class AccountListAdapter extends BaseAdapter implements Filterable {
    List<AccountModelV2> acctsV2s;
    GenericDao genericDao;
    AcctFilter filter;
    List<AccountModelV2> filterList;
    TextView tvAddress, tvSeqNo, tvAccountName, tvMeterNo, tvOldAccountNo, tvGreenCheck, tvIsActive, tvIsPrinted, lblIsPrinted, lblIsReset, tvIsReset;
    private Context c;
    private BillHeaderDAO billHeaderDAO;

    public AccountListAdapter(Context c, List<AccountModelV2> acctsV2s) {
        this.c = c;
        this.acctsV2s = acctsV2s;
        this.filterList = acctsV2s;
        billHeaderDAO=new BillHeaderDAO(c);
        billHeaderDAO.instantiateDb();
    }

    @Override
    public int getCount() {
        return acctsV2s.size();
    }

    @Override
    public Object getItem(int position) {
        return acctsV2s.get(position);
    }

    @Override
    public long getItemId(int position) {
        return acctsV2s.indexOf(getItem(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Holder holder = new Holder();
        int[] listItemBackground = new int[]{R.drawable.bg_odd, R.drawable.bg_even};
        genericDao = new GenericDao(c);


        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_accounts, null);
        }
        lblIsPrinted = (TextView) convertView.findViewById(R.id.lblIsPrinted);
        tvIsPrinted = (TextView) convertView.findViewById(R.id.tvIsPrinted);
        tvGreenCheck = (TextView) convertView.findViewById(R.id.tvGreenCheck);
        tvSeqNo = (TextView) convertView.findViewById(R.id.seqno);
        tvOldAccountNo = (TextView) convertView.findViewById(R.id.tvOldAccountNo);
        tvAccountName = (TextView) convertView.findViewById(R.id.tvAccountName);
        tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
        tvMeterNo = (TextView) convertView.findViewById(R.id.tvMeterNo);
        tvIsActive = (TextView) convertView.findViewById(R.id.tvIsActive);
        lblIsReset = (TextView) convertView.findViewById(R.id.lblIsReset);
        tvIsReset = (TextView) convertView.findViewById(R.id.tvIsReset);

        // SET DATA TO THEM
        String isPrinted = genericDao.getOneField("isPrinted","armBillHeader","WHERE oldAcctNo =",acctsV2s.get(position).getOldAccountNumber(),"ORDER BY _id DESC","");
        List<Double> billHeader1 = billHeaderDAO.getRdg(acctsV2s.get(position).getOldAccountNumber());
        String acctName = WordUtils.capitalizeFully(acctsV2s.get(position).getAccountName());
        String add2 = WordUtils.capitalizeFully(acctsV2s.get(position).getAddressLn1());
        tvSeqNo.setText(String.valueOf(acctsV2s.get(position).getSequenceNumber()));
        tvOldAccountNo.setText(acctsV2s.get(position).getOldAccountNumber());
        tvAccountName.setText(acctName);
        tvAddress.setText(add2);
        tvMeterNo.setText(acctsV2s.get(position).getMeterNumber());
        tvIsReset.setVisibility(View.GONE);
        lblIsReset.setVisibility(View.GONE);
        int[] check = {R.drawable.greencheck, R.drawable.recross};
        if (acctsV2s.get(position).getIsRead() == 1) {
            tvGreenCheck.setText("YES");
            tvGreenCheck.setTextColor(Color.parseColor("#003300"));
            lblIsPrinted.setVisibility(View.VISIBLE);
            tvIsPrinted.setVisibility(View.VISIBLE);
            if (isPrinted.equals("1")) {
                tvIsPrinted.setText("YES");
                tvIsPrinted.setTextColor(Color.parseColor("#003300"));
            } else {
                tvIsPrinted.setText("NO");
                tvIsPrinted.setTextColor(Color.RED);
            }

            if(billHeader1.get(0)<billHeader1.get(1)){
                tvIsReset.setVisibility(View.VISIBLE);
                lblIsReset.setVisibility(View.VISIBLE);
            } else {
                tvIsReset.setVisibility(View.GONE);
                lblIsReset.setVisibility(View.GONE);
            }

        } else {
            lblIsPrinted.setVisibility(View.GONE);
            tvIsPrinted.setVisibility(View.GONE);
            tvGreenCheck.setText("NO");
            tvGreenCheck.setTextColor(Color.RED);
        }
        tvIsActive.setTypeface(null, Typeface.BOLD);
        if (acctsV2s.get(position).getIsActive().equals("Y")) {
            tvIsActive.setText("ACTIVE");
            tvIsActive.setTextColor(Color.parseColor("#003300"));
        } else {
            tvIsActive.setText("DISCO");
            tvIsActive.setTextColor(Color.RED);
        }

        int colorPos = position % listItemBackground.length;
        convertView.setBackgroundResource(listItemBackground[colorPos]);
        return convertView;
    }


    public AccountModelV2 getSelectedItem(int pos) {
        AccountModelV2 account = new AccountModelV2();
        account.setId(acctsV2s.get(pos).getId());
        account.setIdRateMaster(acctsV2s.get(pos).getIdRateMaster());
        account.setIdRoute(acctsV2s.get(pos).getIdRoute());
        account.setIdRDM(acctsV2s.get(pos).getIdRDM());
        account.setIdArea(acctsV2s.get(pos).getIdArea());
        account.setSequenceNumber(acctsV2s.get(pos).getSequenceNumber());
        account.setOldSequenceNumber(acctsV2s.get(pos).getOldSequenceNumber());
        account.setOldAccountNumber(acctsV2s.get(pos).getOldAccountNumber());
        account.setAccountNumber(acctsV2s.get(pos).getAccountNumber());
        account.setMeterNumber(acctsV2s.get(pos).getMeterNumber());
        account.setAccountName(acctsV2s.get(pos).getAccountName());
        account.setAddressLn1(acctsV2s.get(pos).getAddressLn1());
        account.setAddressLn2(acctsV2s.get(pos).getAddressLn2());
        account.setMobileNo(acctsV2s.get(pos).getMobileNo());
        account.setEmailAdd(acctsV2s.get(pos).getEmailAdd());
        account.setSin(acctsV2s.get(pos).getSin());
        account.setSerialNo(acctsV2s.get(pos).getSerialNo());
        account.setRouteCode(acctsV2s.get(pos).getRouteCode());
        account.setPreviousBill(acctsV2s.get(pos).getPreviousBill());
        account.setLastPaymentDate(acctsV2s.get(pos).getLastPaymentDate());
        account.setLastPaymentAmt(acctsV2s.get(pos).getLastPaymentAmt());
        account.setLastDepositPayment(acctsV2s.get(pos).getLastDepositPayment());
        account.setLastDepositPaymentDate(acctsV2s.get(pos).getLastDepositPaymentDate());
        account.setBillDepositInterest(acctsV2s.get(pos).getBillDepositInterest());
        account.setBillDeposit(acctsV2s.get(pos).getBillDeposit());
        account.setTotalBillDeposit(acctsV2s.get(pos).getTotalBillDeposit());
        account.setC01(acctsV2s.get(pos).getC01());
        account.setC02(acctsV2s.get(pos).getC02());
        account.setC03(acctsV2s.get(pos).getC03());
        account.setC04(acctsV2s.get(pos).getC04());
        account.setC05(acctsV2s.get(pos).getC05());
        account.setC06(acctsV2s.get(pos).getC06());
        account.setC07(acctsV2s.get(pos).getC07());
        account.setC08(acctsV2s.get(pos).getC08());
        account.setC09(acctsV2s.get(pos).getC09());
        account.setC10(acctsV2s.get(pos).getC10());
        account.setC11(acctsV2s.get(pos).getC11());
        account.setC12(acctsV2s.get(pos).getC12());
        account.setCurrentReading(acctsV2s.get(pos).getCurrentReading());
        account.setCurrentConsumption(acctsV2s.get(pos).getCurrentConsumption());
        account.setMoAvgConsumption(acctsV2s.get(pos).getMoAvgConsumption());
        account.setStopMeterFixedConsumption(acctsV2s.get(pos).getStopMeterFixedConsumption());
        account.setMeterMultiplier(acctsV2s.get(pos).getMeterMultiplier());
        account.setRemarks(acctsV2s.get(pos).getRemarks());
        account.setIsSeniorCitizen(acctsV2s.get(pos).getIsSeniorCitizen());
        account.setIsActive(acctsV2s.get(pos).getIsActive());
        account.setIsRead(acctsV2s.get(pos).getIsRead());
        account.setMinimumContractedEnergy(acctsV2s.get(pos).getMinimumContractedEnergy());
        account.setMinimumContractedDemand(acctsV2s.get(pos).getMinimumContractedDemand());
        account.setArrears(acctsV2s.get(pos).getArrears());
        account.setArrearsAsOf(acctsV2s.get(pos).getArrearsAsOf());
        account.setIsForAverage(acctsV2s.get(pos).getIsForAverage());
        account.setAutoComputeMode(acctsV2s.get(pos).getAutoComputeMode());
        return account;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new AcctFilter();
        }
        return filter;
    }

    class AcctFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) throws NullPointerException {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<AccountModelV2> filters = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < filterList.size(); i++) {
                    if (filterList.get(i).getAccountNumber().toLowerCase().contains(constraint)
                            || filterList.get(i).getMeterNumber().toLowerCase().contains(constraint)
                            || filterList.get(i).getAccountName().toLowerCase().contains(constraint)
                            || String.valueOf(filterList.get(i).getSequenceNumber()).toLowerCase().contains(constraint)
                            || filterList.get(i).getOldAccountNumber().toLowerCase().contains(constraint)) {
                        AccountModelV2 list = new AccountModelV2(filterList.get(i).getId(), filterList.get(i).getIdRateMaster()
                                , filterList.get(i).getIdRoute(), filterList.get(i).getIdRDM(), filterList.get(i).getIdArea()
                                , filterList.get(i).getSequenceNumber(), filterList.get(i).getOldSequenceNumber(), filterList.get(i).getOldAccountNumber()
                                , filterList.get(i).getAccountNumber(), filterList.get(i).getMeterNumber(), filterList.get(i).getAccountName(), filterList.get(i).getAddressLn1()
                                , filterList.get(i).getAddressLn2(), filterList.get(i).getMobileNo(), filterList.get(i).getEmailAdd(), filterList.get(i).getSin()
                                , filterList.get(i).getSerialNo(), filterList.get(i).getRouteCode(), filterList.get(i).getPreviousBill(), filterList.get(i).getLastPaymentDate()
                                , filterList.get(i).getLastPaymentAmt(), filterList.get(i).getLastDepositPayment(), filterList.get(i).getLastDepositPaymentDate(), filterList.get(i).getBillDepositInterest()
                                , filterList.get(i).getBillDeposit(), filterList.get(i).getTotalBillDeposit(), filterList.get(i).getC01(), filterList.get(i).getC02()
                                , filterList.get(i).getC03(), filterList.get(i).getC04(), filterList.get(i).getC05(), filterList.get(i).getC06(), filterList.get(i).getC07(), filterList.get(i).getC08()
                                , filterList.get(i).getC09(), filterList.get(i).getC10(), filterList.get(i).getC11(), filterList.get(i).getC12(), filterList.get(i).getCurrentReading(),filterList.get(i).getCurrentConsumption()
                                , filterList.get(i).getMoAvgConsumption(), filterList.get(i).getStopMeterFixedConsumption(), filterList.get(i).getMeterMultiplier(), filterList.get(i).getIsSeniorCitizen()
                                , filterList.get(i).getIsActive(), filterList.get(i).getIsRead(), filterList.get(i).getRemarks(), filterList.get(i).getMinimumContractedEnergy(),filterList.get(i).getMinimumContractedDemand()
                                , filterList.get(i).getArrears(), filterList.get(i).getArrearsAsOf(), filterList.get(i).getIsForAverage(),filterList.get(i).getAutoComputeMode());
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
            acctsV2s = (ArrayList<AccountModelV2>) results.values;
            notifyDataSetChanged();
        }
    }
}

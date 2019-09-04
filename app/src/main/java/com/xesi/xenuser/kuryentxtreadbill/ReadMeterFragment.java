package com.xesi.xenuser.kuryentxtreadbill;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.nbbse.mobiprint3.Printer;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.RateMasterDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.RemarksDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.helper.BillGeneration;
import com.xesi.xenuser.kuryentxtreadbill.helper.ComputeConsumption;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;
import com.xesi.xenuser.kuryentxtreadbill.util.PrintReceipt;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Raymond P. Barrinuevo on 9/11/2016.
 */
public class ReadMeterFragment extends Fragment implements View.OnClickListener {
    public static final String APP_PROPERTY_SETTING = "app_config";
    public static DecimalFormat df = new DecimalFormat("#0.00");
    int TAKE_PHOTO_CODE = 0;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private PrintReceipt receipt;
    private TextView tvAcctNo, tvAcctName, tvRateCode, tvPrevRdg,
            tvSeqNo, tvRoute, readCounter, tvMeterNo, tvOldSeqNo;
    private ImageView ivMode;
    private LinearLayout llRemarks, llShowOtherBtn, llPrintNotificationRemarks, llPrintBill,llSaveMore;
    private Button btnPrev, btnNext, btnSave, btnShowRemarks,
            btnHideRemarks, btnShowOtherBtn, btnHideOtherBtn, btnAddAvg, btnPrintNotification,btnCopyLastMonth,btnPrintBill;
    private EditText etCurRdg, etRemarks;
    private Spinner spRouteList;
    //  private String seqNoParam;
    private double previousReading;
    private int getReadingCountYes;
    private int getReadingCountNo;
    // DAO
    private BillGeneration billGen;
    private BillHeader billHeader;
    private Printer print;
    private View view;
    private View vOldSeqNo;
    private int firstRecordSeqNo, lastRecordSeqNo;
    private MsgDialog msgDialog;
    private double curRdg;
    private double prevRdg;
    private GenericDao genericDao;
    private AccountModelV2 accountModelV2;
    private Context context;
    private String name,showAvgWarning,btnValue,consumerType,currentReading,billNo,imageDIR,result = "", isSpikeDrop = "N";
    private boolean isChecked, reOrder, isRead;
    private RemarksDao remarksDao;
    private AccountDao accountDao;
    private RateMasterDao rateMasterDao;
    private BillHeaderDAO billHeaderDAO;
    private UniversalHelper helper;
    private DUPropertyDAO duPropertyDAO;
    private ComputeConsumption dataCalculation;
    private int idRoute;
    private LinearLayout llGraph;
    private BarChart consumptionGraph;
    private UniversalHelper universalHelper;

    public void onCreate(Bundle savedStanceState) {
        super.onCreate(savedStanceState);
        getActivity().setTitle("Read Meter");
        msgDialog = new MsgDialog(getActivity());
    }

    private void instantiatingDAO() {
        genericDao = new GenericDao(getActivity());
        billGen = new BillGeneration(getActivity());
        remarksDao = new RemarksDao(getActivity());
        accountDao = new AccountDao(getActivity());
        rateMasterDao = new RateMasterDao(getActivity());
        billHeaderDAO = new BillHeaderDAO(getActivity());
        billHeaderDAO.instantiateDb();
    }

    private void initializedVar() {
        tvAcctNo = (TextView) view.findViewById(R.id.tvAcctNo);
        tvMeterNo = (TextView) view.findViewById(R.id.tvMeterNo);
        tvAcctName = (TextView) view.findViewById(R.id.tvAcctName);
        tvRateCode = (TextView) view.findViewById(R.id.tvRateCode);
        tvPrevRdg = (TextView) view.findViewById(R.id.tvPrevRdg);
        tvSeqNo = (TextView) view.findViewById(R.id.tvSeqNo);
        tvOldSeqNo = (TextView) view.findViewById(R.id.tvOldSeqNo);
        tvRoute = (TextView) view.findViewById(R.id.tvRoute);
        readCounter = (TextView) view.findViewById(R.id.readCounter);
        btnPrev = (Button) view.findViewById(R.id.btnPrev);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        ivMode = (ImageView) view.findViewById(R.id.ivMode);
        llRemarks = (LinearLayout) view.findViewById(R.id.llRemarks);
        llShowOtherBtn = (LinearLayout) view.findViewById(R.id.llShowOtherBtn);
        llPrintNotificationRemarks  = (LinearLayout) view.findViewById(R.id.llPrintNotificationRemarks);
        llPrintBill  = (LinearLayout) view.findViewById(R.id.llPrintBill);
        llSaveMore  = (LinearLayout) view.findViewById(R.id.llSaveMore);
        spRouteList = (Spinner) view.findViewById(R.id.spRouteList);
        vOldSeqNo = view.findViewById(R.id.vOldSeqNo);
        etCurRdg = (EditText) view.findViewById(R.id.etCurRdg);
        etCurRdg.setFilters(new InputFilter[]{
            new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
                int beforeDecimal = 6, afterDecimal = 2;

                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    String temp = etCurRdg.getText() + source.toString();

                    if (temp.equals(".")) {
                        return "0.";
                    } else if (temp.toString().indexOf(".") == -1) {
                        // no decimal point placed yet
                        if (temp.length() > beforeDecimal) {
                            return "";
                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }
                    }

                    return super.filter(source, start, end, dest, dstart, dend);
                }
            }
        });
        etCurRdg.requestFocus();
        etRemarks = (EditText) view.findViewById(R.id.etRemarks);
        btnShowRemarks = (Button) view.findViewById(R.id.btnShowRemarks);
        btnHideRemarks = (Button) view.findViewById(R.id.btnHideRemarks);
        btnHideOtherBtn = (Button) view.findViewById(R.id.btnHideOtherBtn);
        btnShowOtherBtn = (Button) view.findViewById(R.id.btnShowOtherBtn);
        btnPrintBill = (Button) view.findViewById(R.id.btnPrintBill);
        btnAddAvg = (Button) view.findViewById(R.id.btnAddAvg);
        btnHideRemarks = (Button) view.findViewById(R.id.btnHideRemarks);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnPrintNotification = (Button) view.findViewById(R.id.btnPrintNotification);
        btnCopyLastMonth =(Button) view.findViewById(R.id.btnCopyLastMonth);
        llGraph = (LinearLayout) view.findViewById(R.id.chartView);
        llShowOtherBtn.setVisibility(View.GONE);
        llPrintNotificationRemarks.setVisibility(View.GONE);
        llGraph.setVisibility(View.INVISIBLE);
    }

    private void setBarChart(AccountModelV2 accounts) {
        consumptionGraph = (BarChart) view.findViewById(R.id.consumptionGraph);
        consumptionGraph.setDrawBarShadow(false);
        consumptionGraph.setDrawValueAboveBar(false);
        consumptionGraph.setMaxVisibleValueCount(12);
        consumptionGraph.setPinchZoom(false);
        consumptionGraph.setDrawGridBackground(true);

        List<String> month = universalHelper.generateMonth(new Date());

        ArrayList<BarEntry> monthlyConsumption = new ArrayList<>();

        monthlyConsumption.add(new BarEntry(0f, accounts.getC01() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC01().floatValue()));
        monthlyConsumption.add(new BarEntry(1f, accounts.getC02() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC02().floatValue()));
        monthlyConsumption.add(new BarEntry(2f, accounts.getC03() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC03().floatValue()));
        monthlyConsumption.add(new BarEntry(3f, accounts.getC04() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC04().floatValue()));
        monthlyConsumption.add(new BarEntry(4f, accounts.getC05() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC05().floatValue()));
        monthlyConsumption.add(new BarEntry(5f, accounts.getC06() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC06().floatValue()));
        monthlyConsumption.add(new BarEntry(6f, accounts.getC07() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC07().floatValue()));
        monthlyConsumption.add(new BarEntry(7f, accounts.getC08() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC08().floatValue()));
        monthlyConsumption.add(new BarEntry(8f, accounts.getC09() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC09().floatValue()));
        monthlyConsumption.add(new BarEntry(9f, accounts.getC10() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC10().floatValue()));
        monthlyConsumption.add(new BarEntry(10f, accounts.getC11() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC11().floatValue()));
        monthlyConsumption.add(new BarEntry(11f, accounts.getC12() == null ? BigDecimal.valueOf(0).floatValue() : accounts.getC12().floatValue()));

        XAxis xAxis = consumptionGraph.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setLabelCount(month.size(), false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter((value, axis) -> month.get((int) value));


        consumptionGraph.getAxisRight().setEnabled(false);
        consumptionGraph.getLegend().setEnabled(false);


        BarDataSet barDataSet = new BarDataSet(monthlyConsumption, "");
        barDataSet.setDrawValues(false);
        barDataSet.setColor(R.color.black);
        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.5f);

        consumptionGraph.setData(data);
        consumptionGraph.setFitBars(true);
        consumptionGraph.getLegend().setEnabled(false);
        consumptionGraph.getDescription().setEnabled(false);
        consumptionGraph.getAxisRight().setEnabled(false);
        consumptionGraph.setMinimumWidth(300);
        consumptionGraph.setMinimumHeight(200);

        universalHelper.saveToBitmap(llGraph);
        consumptionGraph.invalidate();
    }

    /* Manage UI Here */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg_readmeter, container, false);
        print = Printer.getInstance();
        duPropertyDAO = new DUPropertyDAO(this.getActivity());
        universalHelper = new UniversalHelper(this.getActivity());
        sharedPref = this.getActivity().getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        context = this.getActivity();
        dataCalculation = new ComputeConsumption();
        receipt = new PrintReceipt(this.getActivity(), print);
        helper = new UniversalHelper(this.getActivity());
        if(duPropertyDAO.getPropertyValue("IS_PREVIOUS_READING_HIDDEN").equals("Y")){
            view.findViewById(R.id.tvPrevRdg).setVisibility(view.GONE);
            view.findViewById(R.id.lblPrevRdg).setVisibility(view.GONE);
        }
        Bundle args = getArguments();
        if (args != null) {
            accountModelV2 = args.getParcelable("accountModel");
            isChecked = args.getBoolean("isCheck");
            reOrder = args.getBoolean("reOrder");
            isRead = args.getBoolean("isRead");
            idRoute = args.getInt("idRoute");

        }
        initializedVar();
        instantiatingDAO();
        billGen.instantiateDb();
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnPrintNotification.setOnClickListener(this);
        btnCopyLastMonth.setOnClickListener(this);
        btnShowRemarks.setOnClickListener(this);
        btnHideRemarks.setOnClickListener(this);
        btnShowOtherBtn.setOnClickListener(this);
        btnHideOtherBtn.setOnClickListener(this);
        btnAddAvg.setOnClickListener(this);
        btnPrintBill.setOnClickListener(this);

        List<String> list = remarksDao.getRemarsList();
        list.add(0, "Select Remarks");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, list);
        spRouteList.setAdapter(adapter);
        spRouteList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (!spRouteList.getSelectedItem().toString().equals("Select Remarks")) {
                    etRemarks.setText(spRouteList.getSelectedItem().toString());
                    int pos = etRemarks.getText().length();
                    etRemarks.setSelection(pos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                etRemarks.setText("");
            }
        });
        String addQuery = "";
        if (isChecked)
            addQuery = "isActive = 'Y' AND ";
        firstRecordSeqNo = Integer.parseInt(genericDao.getOneField("sequenceNumber","arm_account","WHERE " +addQuery+"isRead = 0 AND routeCode=",accountModelV2.getRouteCode(),"ORDER BY sequenceNumber LIMIT 1","0"));
        lastRecordSeqNo = Integer.parseInt(genericDao.getOneField("sequenceNumber","arm_account","WHERE " +addQuery+"isRead = 0 AND routeCode=",accountModelV2.getRouteCode(),"ORDER BY sequenceNumber DESC LIMIT 1","0"));
        consumerType = genericDao.getOneField("rateName","arm_ratemaster","where rateMasterID = ", String.valueOf(accountModelV2.getIdRateMaster()),"","");
        setValue(accountModelV2, consumerType);
        editor.putString("remarks", etRemarks.getText().toString());
        editor.putInt("imageProbability", Integer.parseInt(duPropertyDAO.getPropertyValue("CAPTURE_IMAGE_PROBABILITY", "0")));
        editor.commit();
        view.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    private void saveGenRdg() {
        currentReading = etCurRdg.getText().toString();
        if (currentReading.isEmpty()) {
            etCurRdg.requestFocus();
            etCurRdg.setError("Please enter current meter reading");
        } else {
            curRdg = Double.parseDouble(currentReading);
            prevRdg = previousReading;
            int spike = Integer.parseInt(duPropertyDAO.getPropertyValue("MO_AVG_SPIKE"));
            int drop = Integer.parseInt(duPropertyDAO.getPropertyValue("MO_AVG_DROP"));
            if (spike > 0 && drop > 0) {
                showAvgWarning = dataCalculation.checkMonthlyAvgConsumption(curRdg - prevRdg,
                        accountModelV2.getMoAvgConsumption(),
                        spike, drop);
                if (showAvgWarning.equals("SPIKE")) {
                    isSpikeDrop = "Y";
                    msgDialog.showConfirmDialog("The consumption exceeds " + spike + "% threshold, please verify.", value -> {
                        if (value.equals("Yes")) proceedReading();
                        else etCurRdg.setText("");
                    });
                } else if (showAvgWarning.equals("DROP")) {
                    isSpikeDrop = "Y";
                    msgDialog.showConfirmDialog("The consumption drop " + drop + "% threshold, please verify.", value -> {
                        if (value.equals("Yes")) ReadMeterFragment.this.proceedReading();
                        else etCurRdg.setText("");
                    });
                } else
                    proceedReading();
            } else
                proceedReading();
        }
    }

    private void proceedReading() {
        double kwhConsumption;
        if (curRdg < prevRdg) {
            msgDialog.showConfirmDialogVoid("Is this a meter reset", value -> {
                if (value.equals("Yes")) {
                    msgDialog.promptReadingEntry(curRdg, value1 -> {
                        if (value1.equals("Yes")) {
                            double dKwhConsumption = helper.calculateResetMeterConsumption(curRdg, prevRdg);
                            editor.putString("remarks", "Meter Reset");
                            editor.commit();
                            confirmReading(prevRdg, curRdg, dKwhConsumption, "This is a meter reset");
                        } else {
                            etCurRdg.setText("");
                        }
                    });
                } else if(value.equals("Void")) {
                    double dKwhConsumption = helper.calculateResetMeterConsumption(curRdg, prevRdg);
                    editor.putString("remarks", "Meter Reset");
                    editor.commit();
                    printBill(prevRdg, curRdg, dKwhConsumption,"");

                    billHeaderDAO.updateOneFieldByBillNo("Y",billNo,"isVoid");
                    billGen.updateBillHeader(billNo);
                    Toast.makeText(context, "Bill Void", Toast.LENGTH_LONG).show();
                } else {
                    etCurRdg.setText("");
                }
            });
                /**/
        } else {
            kwhConsumption = curRdg - prevRdg;
            editor.putString("remarks", etRemarks.getText().toString());
            editor.commit();
            confirmReading(prevRdg, curRdg, kwhConsumption, "");
        }
    }

    private void accountNavigator(String methodParam, boolean isChecked) {

        try {
            Method method = accountDao.getClass().getDeclaredMethod(methodParam, int.class, int.class, boolean.class, int.class);
            accountModelV2 = (AccountModelV2) method.invoke(accountDao, accountModelV2.getSequenceNumber(), accountModelV2.getIdRoute(),
                    isChecked, accountModelV2.getId());
            consumerType = genericDao.getOneField("rateName","arm_ratemaster","WHERE rateMasterID = ", String.valueOf(accountModelV2.getIdRateMaster()),"","");
            etCurRdg.requestFocus();
            etCurRdg.setError(null);
            etCurRdg.setText("");
            setValue(accountModelV2, consumerType);
            btnHideRemarks.setVisibility(View.GONE);
            llRemarks.setVisibility(View.GONE);
            btnShowRemarks.setVisibility(View.VISIBLE);
            etRemarks.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValue(AccountModelV2 accountsV2, String consumerType) {
        if (accountsV2.getIsSeniorCitizen().equals("Y"))
            name = "(SR) " + WordUtils.capitalizeFully(accountsV2.getAccountName());
        else
            name = WordUtils.capitalizeFully(accountsV2.getAccountName());

        if (name.length() > 20)
            name = name.substring(0, 19) + "...";
        if (accountsV2.getIsActive().equals("N"))
            ivMode.setBackgroundResource(R.drawable.ic_close);
        else
            ivMode.setBackgroundResource(R.drawable.ic_check);
        tvMeterNo.setText(accountsV2.getMeterNumber());
        tvAcctNo.setText(accountsV2.getOldAccountNumber());
        tvAcctName.setText(name);
        tvRateCode.setText(consumerType);
        if (consumerType.length() > 11)
            tvRateCode.setText(consumerType.substring(0, 11) + "...");
        tvPrevRdg.setText(UniversalHelper.df.format(accountsV2.getCurrentReading()));
        tvSeqNo.setText("SEQ " + accountsV2.getSequenceNumber());
        if (duPropertyDAO.getPropertyValue("IS_PRINT_OLD_SEQ_NO").equals("Y"))
            tvOldSeqNo.setText("Old SEQ " + String.valueOf(accountsV2.getOldSequenceNumber()));
        else {
            tvOldSeqNo.setVisibility(View.GONE);
            vOldSeqNo.setVisibility(View.GONE);
        }
        previousReading = accountsV2.getCurrentReading();
        String addQuery = "";
        if (isChecked)
            addQuery = "isActive = 'Y' AND ";
        getReadingCountYes = Integer.parseInt(genericDao.getOneField("COUNT(id)","arm_account","WHERE " + addQuery +"isRead = 1 AND idRoute=", String.valueOf(accountsV2.getIdRoute()),"","0"));
        getReadingCountNo = Integer.parseInt(genericDao.getOneField("COUNT(id)","arm_account","WHERE " + addQuery +"idRoute=", String.valueOf(accountsV2.getIdRoute()),"","0"));
        readCounter.setText(getReadingCountYes + "/" + getReadingCountNo);
        tvRoute.setText("Route " + accountsV2.getRouteCode());

        if(accountModelV2.getAutoComputeMode() == 1){
            etCurRdg.setText(df.format(accountModelV2.getCurrentReading()));
            etCurRdg.setFocusable(false);
            llPrintBill.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnShowOtherBtn.setVisibility(View.GONE);
            llSaveMore.setVisibility(View.GONE);
            llPrintNotificationRemarks.setVisibility(View.GONE);
            llShowOtherBtn.setVisibility(View.GONE);
        }else {
            llSaveMore.setVisibility(View.VISIBLE);
            if(btnHideOtherBtn.getVisibility() == View.VISIBLE){
                llShowOtherBtn.setVisibility(View.VISIBLE);
                llPrintNotificationRemarks.setVisibility(View.VISIBLE);
                btnShowOtherBtn.setVisibility(View.GONE);
            }else
                btnShowOtherBtn.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            etCurRdg.setFocusableInTouchMode(true);
            etCurRdg.setFocusable(true);
            llPrintBill.setVisibility(View.GONE);
            if(accountModelV2.getIsForAverage().equals("Y")) {
                if (accountModelV2.getMoAvgConsumption() > 0) {
                    double avgReading = accountModelV2.getCurrentReading() + accountModelV2.getMoAvgConsumption();
                    etCurRdg.setText(df.format(avgReading));
                    int pos = etCurRdg.getText().length();
                    etCurRdg.setSelection(pos);
                    etCurRdg.requestFocus();
                } else {
                    etCurRdg.requestFocus();
                    etCurRdg.setError("No average consumption found");
                }
            }
        }

        if (firstRecordSeqNo == accountsV2.getSequenceNumber())
            btnPrev.setEnabled(false);
        else
            btnPrev.setEnabled(true);

        if (lastRecordSeqNo == accountsV2.getSequenceNumber())
            btnNext.setEnabled(false);
        else
            btnNext.setEnabled(true);
        if (consumerType.equals("NO_VALUE"))
            msgDialog.showErrDialog("Rate Code not found!");
    }

    private void catchErr(String s) {
        msgDialog.showErrDialog(s);
        ReadMeterFragment.this.accountNavigator("getReadMeterDataNext", isChecked);
    }

    private void confirmReading(double dPrevRdg, double dCurRdg, double dKwhConsumption, String note) {
        msgDialog.showConfirmReading("Confirm Reading", dPrevRdg, dCurRdg, dKwhConsumption, note,
                value -> {
                    printBill(dPrevRdg, dCurRdg, dKwhConsumption,value);
                }
        );
    }

    public void generateBill(String value){

        billNo = genericDao.getOneField("billNo","armBillHeader","","","ORDER BY _id DESC LIMIT 1","");
        if (!result.equals(billNo)) {
            msgDialog.showErrDialog(result);
        } else {
            accountDao.updateReadingAndIsRead(curRdg, accountModelV2.getSequenceNumber(), accountModelV2.getIdRoute(), accountModelV2.getId());
            etCurRdg.setText("");
            boolean lastSeq = lastRecordSeqNo == accountModelV2.getSequenceNumber();
            if (value.equals("Print")) {
                billHeader = billHeaderDAO.findBillByBillNo(billNo);
                receipt.callPrintReadingBackground(billHeader,lastSeq);
            } else if (value.equals("Generate")){
                receipt.callPrintBackground(billNo,lastSeq, isSpikeDrop);
                isSpikeDrop = "N";
            }else
                Toast.makeText(getActivity().getApplicationContext(), "Reading saved", Toast.LENGTH_SHORT).show();

            if (lastSeq) {
                btnNext.setEnabled(false);
                showReadingDialog("You reach the last record on the list.",value);
            } else
                accountNavigator("getReadMeterDataNext", isChecked);
            String addQuery = "";
            if (isChecked)
                addQuery = "isActive = 'Y' AND ";
            firstRecordSeqNo =  Integer.parseInt(genericDao.getOneField("sequenceNumber","arm_account","WHERE " +addQuery+"isRead = 0 AND routeCode=",accountModelV2.getRouteCode(),"ORDER BY sequenceNumber LIMIT 1","0"));
            if (firstRecordSeqNo == accountModelV2.getSequenceNumber())
                btnPrev.setEnabled(false);
        }
    }
    public void showReadingDialog(String msg,String value) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if (value.equals("Save")){
                Intent i = new Intent(context, Transaction.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrev:
                accountNavigator("getReadMeterDataPrev", isChecked);
                if (firstRecordSeqNo == accountModelV2.getSequenceNumber())
                    btnPrev.setEnabled(false);
                if (lastRecordSeqNo == accountModelV2.getSequenceNumber())
                    btnNext.setEnabled(false);
                break;
            case R.id.btnNext:
                accountNavigator("getReadMeterDataNext", isChecked);
                if (firstRecordSeqNo == accountModelV2.getSequenceNumber())
                    btnPrev.setEnabled(false);
                if (lastRecordSeqNo == accountModelV2.getSequenceNumber())
                    btnNext.setEnabled(false);
                break;
            case R.id.btnSave:
                saveGenRdg();
                setBarChart(accountModelV2);
                break;
            case R.id.btnShowOtherBtn:
                btnHideOtherBtn.setVisibility(View.VISIBLE);
                llShowOtherBtn.setVisibility(View.VISIBLE);
                llPrintNotificationRemarks.setVisibility(View.VISIBLE);
                btnShowOtherBtn.setVisibility(View.GONE);
                btnShowRemarks.requestFocus();
                if (duPropertyDAO.getPropertyValue("IS_FORCE_AVG_ENABLED").equals("Y"))
                    btnAddAvg.setEnabled(true);
                break;
            case R.id.btnHideOtherBtn:
                btnShowOtherBtn.setVisibility(View.VISIBLE);
                btnHideOtherBtn.setVisibility(View.GONE);
                llShowOtherBtn.setVisibility(View.GONE);
                llPrintNotificationRemarks.setVisibility(View.GONE);
                llRemarks.setVisibility(View.GONE);
                break;
            case R.id.btnShowRemarks:
                btnHideRemarks.setVisibility(View.VISIBLE);
                llRemarks.setVisibility(View.VISIBLE);
                btnShowRemarks.setVisibility(View.GONE);
                etRemarks.requestFocus();
                break;
            case R.id.btnHideRemarks:
                btnHideRemarks.setVisibility(View.GONE);
                llRemarks.setVisibility(View.GONE);
                btnShowRemarks.setVisibility(View.VISIBLE);
                etCurRdg.requestFocus();
                break;
            case R.id.btnAddAvg:
                if (accountModelV2.getMoAvgConsumption() > 0) {
                    double avgReading = accountModelV2.getCurrentReading() + accountModelV2.getMoAvgConsumption();
                    etCurRdg.setText(df.format(avgReading));
                    int pos = etCurRdg.getText().length();
                    etCurRdg.setSelection(pos);
                    etCurRdg.setError(null);
                    etCurRdg.requestFocus();
                } else {
                    etCurRdg.requestFocus();
                    etCurRdg.setError("No average consumption found");
                }
                break;
            case R.id.btnPrintNotification:
                receipt.callPrintNotificationOnBackground(accountModelV2);
                break;
            case R.id.btnPrintBill:
                currentReading = etCurRdg.getText().toString();
                curRdg = Double.parseDouble(currentReading);
                printBill(previousReading, curRdg, curRdg - previousReading,"Generate");
                break;
            case R.id.btnCopyLastMonth:
                if (accountModelV2.getCurrentConsumption() > 0) {
                    double copyLastMonth = accountModelV2.getCurrentReading() + accountModelV2.getCurrentConsumption();
                    etCurRdg.setText(df.format(copyLastMonth));
                    int pos = etCurRdg.getText().length();
                    etCurRdg.setSelection(pos);
                    etCurRdg.setError(null);
                    etCurRdg.requestFocus();
                } else {
                    etCurRdg.requestFocus();
                    etCurRdg.setError("No last month consumption found");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        billGen.close();
        billGen.close();
        remarksDao.close();
        accountDao.close();
        rateMasterDao.close();
        billHeaderDAO.close();
        genericDao.close();
        super.onDestroy();
    }

    public void printBill(double dPrevRdg, double dCurRdg, double dKwhConsumption, String btnChoose){
        editor.putString("remarks", etRemarks.getText().toString());
        editor.commit();
        if (accountModelV2 == null || (accountModelV2.getRouteCode() == null && accountModelV2.getIdRateMaster() == 0
                && accountModelV2.getIdRoute() == 0 && accountModelV2.getAccountNumber() == null))
            msgDialog.showNavErrDialog("Problem encountered: Cannot retrieve the accountModel.\n This will refresh the app");
        else
            result = billGen.readingComputation(accountModelV2, dCurRdg, consumerType, false, dPrevRdg, dKwhConsumption);
        if (result == "-1") {
            catchErr(
                    ReadMeterFragment.this.getActivity().getResources().getString(R.string.no_rate_err_msg1)
                            + consumerType + " " + ReadMeterFragment.this.getResources().getString(R.string.no_rate_err_msg2));
        } else if (result == "-2") {
            catchErr("Bill Header not save");
        } else if (result == "-3") {
            catchErr("Failed to generate bill json");
        } else if (result.substring(0, 12).equals("SQLException")) {
            catchErr(result);
        } else {
            btnValue = btnChoose;
            if (getRandom(sharedPref.getInt("imageProbability", 0))) takePicture(result);
            else generateBill(btnValue);
        }

    }
    /* TO BE USED FOR RANDOM PHOTO */
    public void takePicture(String fileName) {
        if(UniversalHelper.externalMemoryAvailable())
            imageDIR= "/sdcard";
        else
            imageDIR= String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        final String dir = imageDIR + "/RnB/";
        File newdir = new File(dir);
        newdir.mkdirs();
        File newfile = new File(dir + fileName + ".jpg");
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Log.d("TAKEPHOTO", e.getMessage());
        }
        Uri outputFileUri = Uri.fromFile(newfile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK){
            Log.d("TAKEPHOTO", "Pic saved");
        }
        generateBill(btnValue);
    }

    public static boolean getRandom(int probability) {
        Random random = new Random();
        List<Integer> randomList = new ArrayList<>();
        while (randomList.size() < probability) {
            int x = random.nextInt(100) + 1;
            if (randomList.indexOf(x) == -1)
                randomList.add(x);
        }
        return randomList.indexOf(random.nextInt(100) + 1) != -1;
    }

}
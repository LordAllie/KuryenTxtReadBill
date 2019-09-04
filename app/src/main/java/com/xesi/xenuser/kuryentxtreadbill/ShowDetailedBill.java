package com.xesi.xenuser.kuryentxtreadbill;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.nbbse.mobiprint3.Printer;
import com.xesi.xenuser.kuryentxtreadbill.adapter.BaseActivity;
import com.xesi.xenuser.kuryentxtreadbill.adapter.TblDatas;
import com.xesi.xenuser.kuryentxtreadbill.dao.AccountDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.base.GenericDao;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillAddonChargeDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillAddonKwhDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillChargeGroupDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.helper.HeaderFooterInfo;
import com.xesi.xenuser.kuryentxtreadbill.helper.MsgDialog;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonCharge;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonKwh;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroup;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillHeader;
import com.xesi.xenuser.kuryentxtreadbill.model.download.AccountModelV2;
import com.xesi.xenuser.kuryentxtreadbill.util.PrintReceipt;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import org.apache.commons.lang3.text.WordUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShowDetailedBill extends BaseActivity {
    public static final String APP_PROPERTY_SETTING = "app_config";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Printer print;
    private TextView tvRunDate, tvBillId, tvMeterNo, tvAcctNo, tvName, tvCurRdg, tvPreviousConsumption, tvRouteCode, tvMCE,
            tvPrevRdg, tvKwhUsed, tvTotalBill, tvDevId,
            tvReader1, tvPeriodFr, tvPeriodTo, tvBillMo, tvDueDate, tvDateYear, tvConsumerType, tvCoreloss,
            tvMultiplier, tvTotalKwhUsed, tvDiscoDate, tvTotalBillAfterDue, tvSurcharge, tvAddressLn1, tvAddressLn2,lblRouteCode,lblMCE;
    private LinearLayout llCoreloss, llKwhAddon2, llMultiplier, llAddonCharges, llAfterDueDate, llkWh, llSurcharge;
    //Object Model
    private BillHeader billHeader;
    private PrintReceipt printReceipt;
    private List<BillAddonCharge> billAddonCharges;
    private List<BillAddonKwh> billAddonKwhs;
    private TblDatas tblDatas;
    //DAO
    private BillHeaderDAO billHeaderDAO;
    private BillChargeGroupDAO billChargeGroupDao;
    private BillAddonChargeDAO addonChargeDAO;
    private BillAddonKwhDAO billAddonKwhDAO;
    private String billNumber;
    private TextView tvCurrentBill;
    private HeaderFooterInfo headerFooterInfo;
    private MsgDialog msgDialog;
    private DUPropertyDAO duPropertyDAO;
    private AccountDao accountDao;
    private SimpleDateFormat dueDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private BarChart consumptionGraph;
    private LinearLayout view;
    private UniversalHelper universalHelper;
    private GenericDao genericDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_detailed_bill);
        sharedPref = getSharedPreferences(APP_PROPERTY_SETTING, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        headerFooterInfo = new HeaderFooterInfo(this);
        universalHelper = new UniversalHelper(this);
        accountDao = new AccountDao(this);
        genericDao = new GenericDao(this);
        headerFooterInfo.setHeaderInfo();
        headerFooterInfo.setFooterInfo();
        print = Printer.getInstance();
        printReceipt = new PrintReceipt(this, print);
        tblDatas = new TblDatas(this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                billNumber = null;
            } else {
                billNumber = extras.getString("billNo");
            }
        } else {
            billNumber = (String) savedInstanceState.getSerializable("billNo");
        }
        instantiateObject();
        instantiateDao();
        setValue();
        setAdapter();
        view = (LinearLayout) findViewById(R.id.chartView);
        view.setVisibility(View.VISIBLE);
        setBarChart(accountDao.getAccountByAcctNo(billHeader.getOldAccountNo()));
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

        universalHelper.saveToBitmap(view);
        consumptionGraph.invalidate();
    }

    public void instantiateObject() {
        //TextView
        tvRunDate = (TextView) findViewById(R.id.tvRunDate);
        tvBillId = (TextView) findViewById(R.id.tvBillId);
        tvMeterNo = (TextView) findViewById(R.id.tvMeterNo);
        tvAcctNo = (TextView) findViewById(R.id.tvAcctNo);
        tvName = (TextView) findViewById(R.id.tvName);
        tvAddressLn1 = (TextView) findViewById(R.id.tvAddressLn1);
        tvAddressLn2 = (TextView) findViewById(R.id.tvAddressLn2);
        tvPreviousConsumption = (TextView) findViewById(R.id.tvPreviousConsumption);
        tvCurRdg = (TextView) findViewById(R.id.tvCurRdg);
        tvPrevRdg = (TextView) findViewById(R.id.tvPrevRdg);
        tvKwhUsed = (TextView) findViewById(R.id.tvKwhUsed);
        tvCurrentBill = (TextView) findViewById(R.id.tvCurrentBill);
        tvTotalBill = (TextView) findViewById(R.id.tvTotalBill);
        tvDiscoDate = (TextView) findViewById(R.id.tvDiscoDate);
        tvDevId = (TextView) findViewById(R.id.tvDevId);
        tvReader1 = (TextView) findViewById(R.id.tvReader1);
        tvDueDate = (TextView) findViewById(R.id.tvDueDate);
        tvDateYear = (TextView) findViewById(R.id.tvDateYear);
        tvConsumerType = (TextView) findViewById(R.id.tvConsumerType);
        tvPeriodFr = (TextView) findViewById(R.id.tvPeriodFr);
        tvPeriodTo = (TextView) findViewById(R.id.tvPeriodTo);
        tvBillMo = (TextView) findViewById(R.id.tvBillMo);
        tvCoreloss = (TextView) findViewById(R.id.tvCoreloss);
        tvTotalKwhUsed = (TextView) findViewById(R.id.tvTotalKwhUsed);
        tvMultiplier = (TextView) findViewById(R.id.tvMultiplier);
        tvSurcharge = (TextView) findViewById(R.id.tvSurcharge);
        tvTotalBillAfterDue = (TextView) findViewById(R.id.tvTotalBillAfterDue);
        lblRouteCode = (TextView) findViewById(R.id.lblRouteCode);
        tvRouteCode = (TextView) findViewById(R.id.tvRouteCode);
        lblMCE = (TextView) findViewById(R.id.lblMCE);
        tvMCE = (TextView) findViewById(R.id.tvMCE);

        //Linear
        llCoreloss = (LinearLayout) findViewById(R.id.llCoreloss);
        llMultiplier = (LinearLayout) findViewById(R.id.llMultiplier);
        llKwhAddon2 = (LinearLayout) findViewById(R.id.llKwhAddon2);
        llAddonCharges = (LinearLayout) findViewById(R.id.llAddonCharges);
        llAfterDueDate = (LinearLayout) findViewById(R.id.llAfterDueDate);
        llkWh = (LinearLayout) findViewById(R.id.llkWh);
        llSurcharge = (LinearLayout) findViewById(R.id.llSurcharge);
        view = (LinearLayout) findViewById(R.id.chartView);
        view.setVisibility(View.VISIBLE);
    }

    public void instantiateDao() {
        billHeaderDAO = new BillHeaderDAO(getApplication());
        billHeaderDAO.instantiateDb();
        billChargeGroupDao = new BillChargeGroupDAO(getApplication());
        addonChargeDAO = new BillAddonChargeDAO(getApplication());
        billAddonKwhDAO = new BillAddonKwhDAO(getApplication());
        duPropertyDAO = new DUPropertyDAO(getApplication());
    }

    public void setValue() {
        try {
            msgDialog = new MsgDialog(this);
            billHeader = new BillHeader();
            billHeader = billHeaderDAO.findBillByBillNo(billNumber);
            List<String> address = accountDao.getAddressByAcctNo(billHeader.getOldAccountNo());
            if (billHeader == null && billHeader.getBillNo().equals(null) || billHeader.getAcctName().equals(null)) {
                msgDialog.showErrDialog("Bill Header cannot be null");
                throw new NullPointerException("Bill Header cannot be null");
            } else {
                String acctName = WordUtils.capitalizeFully(billHeader.getAcctName());
                billNumber = billHeader.getBillNo();
                tvRunDate.setText(billHeader.getRunDate());
                tvBillId.setText(billNumber);
                tvMeterNo.setText(billHeader.getMeterNo());
                tvAcctNo.setText(billHeader.getOldAccountNo());
                tvName.setText(acctName);
                if (duPropertyDAO.getPropertyValue("IS_PRINT_ROUTE_CODE").equals("Y")){
                    tvRouteCode.setVisibility(view.VISIBLE);
                    lblRouteCode.setVisibility(view.VISIBLE);
                    tvRouteCode.setText(billHeader.getRouteCode());
                }
                if (billHeader.getMinimumContractedEnergy() > 0){
                    tvMCE.setVisibility(view.VISIBLE);
                    lblMCE.setVisibility(view.VISIBLE);
                    tvMCE.setText(String.valueOf(billHeader.getMinimumContractedEnergy()));
                }
                if (address.size() > 1) {
                    tvAddressLn1.setText(WordUtils.capitalizeFully(address.get(0)));
                    tvAddressLn2.setText(WordUtils.capitalizeFully(address.get(1)));
                } else if (address.size() == 1) {
                    tvAddressLn1.setText(WordUtils.capitalizeFully(address.get(0)));
                    tvAddressLn2.setVisibility(View.GONE);
                }
                if (billHeader.getConsumerType().length() > 11)
                    tvConsumerType.setText(billHeader.getConsumerType().substring(0, 11) + "...");
                else
                    tvConsumerType.setText(billHeader.getConsumerType());
                tvPreviousConsumption.setText(UniversalHelper.df.format(Double.parseDouble(
                        genericDao.getOneField("currentConsumption","arm_account","WHERE oldAccountNumber =",billHeader.getOldAccountNo(),"","0"))));
                tvCurRdg.setText(UniversalHelper.df.format(billHeader.getCurReading()));
                tvPrevRdg.setText(UniversalHelper.df.format(billHeader.getPrevReading()));
                if (billHeader.getConsumption() == billHeader.getTotalConsumption())
                    llkWh.setVisibility(View.GONE);
                else
                    tvKwhUsed.setText(UniversalHelper.df.format(billHeader.getConsumption()));
                if (billHeader.getMeterMultiplier() > 1)
                    tvMultiplier.setText(UniversalHelper.df.format(billHeader.getMeterMultiplier()));
                else
                    llMultiplier.setVisibility(View.GONE);
                if (billHeader.getCoreloss() > 0)
                    tvCoreloss.setText(UniversalHelper.df.format(billHeader.getCoreloss()));
                else
                    llCoreloss.setVisibility(View.GONE);
                tvTotalKwhUsed.setText(UniversalHelper.df.format(billHeader.getTotalConsumption()));
                String[] date;
                try {
                    String sdate = "0000-00-00";
                    if (!billHeader.getPeriodFrom().equals(null)) {
                        sdate = billHeader.getPeriodFrom();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date periodDateFrom = sdf.parse(sdate);
                    date = sdf.format(periodDateFrom).split("-");
                    tvDateYear.setText("FOR THE MONTH OF (" + date[0] + "-" + date[1] + ")");
                    tvPeriodFr.setText(billHeader.getPeriodFrom());
                    tvPeriodTo.setText(billHeader.getPeriodTo());
                    tvBillMo.setText(billHeader.getBillingMonth());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvCurrentBill.setText(UniversalHelper.dformatter.format(Double.parseDouble(billHeader.getCurBill())));
                tvTotalBill.setText(UniversalHelper.dformatter.format(Double.parseDouble(billHeader.getTotalAmountDue())));
                if (duPropertyDAO.getPropertyValue("IS_PRINT_BILL_AFTER_DUE").equals("Y")) {
                    if (duPropertyDAO.getPropertyValue("IS_PRINT_SURCHARGE").equals("Y")) {
                        llSurcharge.setVisibility(View.VISIBLE);
                        BigDecimal totalAmountDue = new BigDecimal(billHeader.getTotalAmountDue()).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal totalAmountAfterDue = new BigDecimal(billHeader.getTotalBillAfterDueDate()).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal surcharge = totalAmountAfterDue.subtract(totalAmountDue);
                        //  double surcharge = Double.parseDouble(billHeader.getTotalBillAfterDueDate()) - Double.parseDouble(billHeader.getTotalAmountDue());
                        if (totalAmountDue.compareTo(BigDecimal.ZERO) > 0)
                            tvSurcharge.setText(UniversalHelper.dformatter.format(surcharge));
                        else
                            llSurcharge.setVisibility(View.GONE);
                    }
                    llAfterDueDate.setVisibility(View.VISIBLE);
                    tvTotalBillAfterDue.setText(UniversalHelper.dformatter.format(new BigDecimal(billHeader.getTotalBillAfterDueDate()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    //tvTotalBillAfterDue.setText(billHeader.getTotalBillAfterDueDate());
                }

                tvDevId.setText(Integer.toString(billHeader.getDeviceId()));
                tvReader1.setText(billHeader.getReader());
                try {
                    tvDueDate.setText(dueDateFormat.format(sdf.parse(billHeader.getDueDate())));
                    tvDiscoDate.setText(dueDateFormat.format(sdf.parse(billHeader.getDiscoDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            msgDialog.showErrDialog("Bill Header cannot be null");
            e.printStackTrace();
        }
    }

    public void setAdapter() {

        TableLayout tblChargeGroup = (TableLayout) findViewById(R.id.tbl_details);
        List<BillChargeGroup> billChargeGroups = billChargeGroupDao.getAllChargeGroups(billHeader.getBillNo());
        if (billChargeGroups != null && billChargeGroups.size() > 0) {
            tblDatas.initChargeGroup(billChargeGroups, tblChargeGroup);
        } else {
            tblChargeGroup.setVisibility(View.GONE);
        }
        TableLayout tblAddonCharge = (TableLayout) findViewById(R.id.tblAddonCharge);
        billAddonCharges = addonChargeDAO.getAllBillAddonCharges(billHeader.getBillNo());
        if (billAddonCharges != null && billAddonCharges.size() > 0) {
            tblDatas.initChargeAddon(billAddonCharges, tblAddonCharge);
        } else {
            llAddonCharges.setVisibility(View.GONE);
        }

        TableLayout tblAddonKwh = (TableLayout) findViewById(R.id.table_main);
        billAddonKwhs = billAddonKwhDAO.getAllBillAddonKwh(billNumber);
        if (billAddonKwhs != null && billAddonKwhs.size() > 0) {
            tblDatas.initKwhAddon(billAddonKwhs, tblAddonKwh);
        } else {
            llKwhAddon2.setVisibility(View.GONE);
        }

    }

    public void btnPrint(View view) {
        msgDialog = new MsgDialog(ShowDetailedBill.this);
        printReceipt.callPrintBackground(billNumber,false,"N");
    }

    public void btnBackToDetailedBill(View view) {
        Intent intent = new Intent(getApplicationContext(), Reports.class);
        startActivity(intent);
        finish();
    }

    public void home(View view) {
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        billHeaderDAO.close();
        billChargeGroupDao.close();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), Reports.class);
        startActivity(i);
        finish();
    }
}
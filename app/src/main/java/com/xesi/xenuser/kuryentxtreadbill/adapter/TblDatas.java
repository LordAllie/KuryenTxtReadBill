package com.xesi.xenuser.kuryentxtreadbill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillChargeGroupDetailDAO;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;
import com.xesi.xenuser.kuryentxtreadbill.model.Diagnostic;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonCharge;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillAddonKwh;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroup;
import com.xesi.xenuser.kuryentxtreadbill.model.bill.BillChargeGroupDetail;

import org.apache.commons.lang3.text.WordUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by xenuser on 2/24/2017.
 */
public class TblDatas {
    private static final String STATUS = "Status";
    private static final String TOTALREC = "Total Record(s)";
    private static final String TS = "Timestamp";
    private final Context context;
    private BillChargeGroupDetailDAO billChargeGroupDetailDao;
    private DUPropertyDAO duPropertyDAO;
    private String printZero = "Y";

    public TblDatas(Context context) {
        this.context = context;
        billChargeGroupDetailDao = new BillChargeGroupDetailDAO(context);
        duPropertyDAO = new DUPropertyDAO(context);
    }

    public void initKwhAddon(List<BillAddonKwh> queryResult, TableLayout table) {
        TableLayout stk = table;
        for (BillAddonKwh billAddonKwh : queryResult) {
            TableRow tbrow = new TableRow(context);
            tbrow.setWeightSum(5);
            TextView t1v = new TextView(context.getApplicationContext());
            t1v.setText(WordUtils.capitalizeFully(billAddonKwh.getAddonKwh()));
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.START);
            if (Build.VERSION.SDK_INT < 23) {
                t1v.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            } else {
                t1v.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            tbrow.addView(t1v, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.2f));
            TextView t2v = new TextView(context);
            t2v.setText(" " + UniversalHelper.df.format(billAddonKwh.getValue()));
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.END);
            if (Build.VERSION.SDK_INT < 23) {
                t2v.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            } else {
                t2v.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            tbrow.addView(t2v, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            TextView t3v = new TextView(context.getApplicationContext());
            t3v.setText("kwh");
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.END);
            if (Build.VERSION.SDK_INT < 23) {
                t3v.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            } else {
                t3v.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
            tbrow.addView(t3v, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, .8f));
            stk.addView(tbrow);
        }
    }

    public void initChargeAddon(List<BillAddonCharge> queryResult, TableLayout table) {
        TableLayout stk = table;
        for (BillAddonCharge billAddonCharge : queryResult) {
            TableRow tbrow = new TableRow(context);
            tbrow.setWeightSum(5);
            TextView t1v = new TextView(context.getApplicationContext());
            t1v.setText(WordUtils.capitalizeFully(billAddonCharge.getAddonCharge()));
            t1v.setTextSize(17f);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.START);
            tbrow.addView(t1v, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));

            TextView t2v = new TextView(context);
            t2v.setText(UniversalHelper.dformatter.format(billAddonCharge.getValue()));
            t1v.setTextSize(17f);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.END);

            tbrow.addView(t2v, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
            stk.addView(tbrow);
        }
    }

    public void initChargeGroup(List<BillChargeGroup> billChargeGroups, TableLayout tblChargeGroup) {
        UniversalHelper.dformat.setRoundingMode(RoundingMode.FLOOR);
        TableLayout tcg = tblChargeGroup;
        for (BillChargeGroup billChargeGroup : billChargeGroups) {
            View v = new View(context);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 3));
            v.setBackgroundColor(Color.BLACK);

            View v1 = new View(context);
            v1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 3));
            v1.setBackgroundColor(Color.BLACK);

            TableRow trChargeGroup = new TableRow(context);
            TextView tvGroupName = new TextView(context.getApplicationContext());
            if (duPropertyDAO.getPropertyValue("IS_PRINT_CHARGE_HEADER", "Y").equals("Y")) {
                tvGroupName.setText(billChargeGroup.getChargeTypeName());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    tvGroupName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                } else {
                    tvGroupName.setTextAppearance(android.R.style.TextAppearance_Medium);
                }
                tvGroupName.setTypeface(null, Typeface.BOLD);
                tvGroupName.setTextColor(Color.BLACK);
                tvGroupName.setGravity(Gravity.CENTER);
                tvGroupName.setPadding(0, 2, 0, 2);

                trChargeGroup.addView(tvGroupName, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));
            }
                /*Details*/
            tcg.addView(trChargeGroup);
            List<BillChargeGroupDetail> billChargeGroupDetails = billChargeGroupDetailDao.getAllChargeGroupDetails(billChargeGroup.getBillNumber(), billChargeGroup.getPrintOrder());
            printZero = duPropertyDAO.getPropertyValue("IS_PRINT_ZERO");
            for (BillChargeGroupDetail detail : billChargeGroupDetails) {
                if (printZero.equals("N")) {
                    if (detail.getChargeTotal().compareTo(BigDecimal.ZERO) != 0) {
                        View v2 = new View(context);
                        v2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                        v2.setBackgroundColor(Color.rgb(211, 211, 211));
                        TableRow trChargeDetail = new TableRow(context);
                    /* Per Kw Name */
                        TextView tvPerKwName = new TextView(context.getApplicationContext());
                        tvPerKwName.setText(detail.getChargeName());
                        tvPerKwName.setTextSize(17f);
                        tvPerKwName.setTextColor(Color.BLACK);
                        tvPerKwName.setGravity(Gravity.LEFT);
                        trChargeDetail.addView(tvPerKwName, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));

                    /*Sub Total */
                        TextView tvSubTotal = new TextView(context.getApplicationContext());
                        tvSubTotal.setText(UniversalHelper.dformat.format(detail.getChargeAmount()));
                        tvSubTotal.setTextSize(17f);
                        tvSubTotal.setTextColor(Color.BLACK);
                        tvSubTotal.setGravity(Gravity.RIGHT);
                        trChargeDetail.addView(tvSubTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.8f));
                        // tcg.addView(trChargeDetail);

                      /*Total */
                        TextView tvTotal = new TextView(context.getApplicationContext());
                        tvTotal.setText(UniversalHelper.dformatter.format(detail.getChargeTotal().doubleValue()));
                        tvTotal.setTextSize(17f);
                        tvTotal.setTextColor(Color.BLACK);
                        tvTotal.setGravity(Gravity.RIGHT);
                        trChargeDetail.addView(tvTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2f));

                        tcg.addView(trChargeDetail);
                        tcg.addView(v2);
                    }
                } else {
                    View v2 = new View(context);
                    v2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                    v2.setBackgroundColor(Color.rgb(211, 211, 211));
                    TableRow trChargeDetail = new TableRow(context);
                    /* Per Kw Name */
                    TextView tvPerKwName = new TextView(context.getApplicationContext());
                    tvPerKwName.setText(detail.getChargeName());
                    tvPerKwName.setTextSize(17f);
                    tvPerKwName.setTextColor(Color.BLACK);
                    tvPerKwName.setGravity(Gravity.LEFT);
                    trChargeDetail.addView(tvPerKwName, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));

                    /*Sub Total */
                    TextView tvSubTotal = new TextView(context.getApplicationContext());
                    tvSubTotal.setText(UniversalHelper.dformat.format(detail.getChargeAmount()));
                    tvSubTotal.setTextSize(17f);
                    tvSubTotal.setTextColor(Color.BLACK);
                    tvSubTotal.setGravity(Gravity.RIGHT);
                    trChargeDetail.addView(tvSubTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.8f));
                    // tcg.addView(trChargeDetail);

                      /*Total */
                    TextView tvTotal = new TextView(context.getApplicationContext());
                    tvTotal.setText(UniversalHelper.dformatter.format(detail.getChargeTotal().doubleValue()));
                    tvTotal.setTextSize(17f);
                    tvTotal.setTextColor(Color.BLACK);
                    tvTotal.setGravity(Gravity.RIGHT);
                    trChargeDetail.addView(tvTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2f));

                    tcg.addView(trChargeDetail);
                    tcg.addView(v2);
                }
            }

            TableRow trChargeSub = new TableRow(context);
            TextView tvSubText = new TextView(context.getApplicationContext());
            tvSubText.setText("Sub Total");
            tvSubText.setTextSize(17f);
            tvSubText.setTextColor(Color.BLACK);
            tvSubText.setGravity(Gravity.LEFT);
            tvSubText.setTypeface(null, Typeface.BOLD);
            trChargeSub.addView(tvSubText, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3f));

            TextView tvSubTotal = new TextView(context.getApplicationContext());
            tvSubTotal.setText(UniversalHelper.dformat.format(billChargeGroup.getSubtotalCharges()));
            tvSubTotal.setTextSize(17f);
            tvSubTotal.setTextColor(Color.BLACK);
            tvSubTotal.setGravity(Gravity.RIGHT);
            tvSubTotal.setTypeface(null, Typeface.BOLD);
            trChargeSub.addView(tvSubTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.8f));

            TextView tvTotal = new TextView(context.getApplicationContext());
            tvTotal.setText(UniversalHelper.dformatter.format(billChargeGroup.getTotalCharges().doubleValue()));
            tvTotal.setTextSize(17f);
            tvTotal.setTextColor(Color.BLACK);
            tvTotal.setGravity(Gravity.RIGHT);
            tvTotal.setTypeface(null, Typeface.BOLD);
            trChargeSub.addView(tvTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2f));
            tcg.addView(v1);
            tcg.addView(trChargeSub);
            tcg.addView(v);
        }
    }

    public void initDiagnostic(List<Diagnostic> diagnostics, TableLayout tblDiagnostic) {
        TableLayout tdiagnostic = tblDiagnostic;
        for (Diagnostic diagnostic : diagnostics) {

            View v = new View(context);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 3));
            v.setBackgroundColor(Color.BLACK);

                /*Status*/
            TableRow trStatus = new TableRow(context);
            TextView tvStat = new TextView(context.getApplicationContext());
            tvStat.setText(STATUS);
            tvStat.setTextSize(17f);
            tvStat.setTextColor(Color.BLACK);
            tvStat.setGravity(Gravity.LEFT);
            trStatus.addView(tvStat, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));

            TextView tvStatus = new TextView(context.getApplicationContext());
            tvStatus.setText(diagnostic.getTitle());
            tvStatus.setTextSize(17f);
            tvStatus.setTextColor(Color.BLACK);
            tvStatus.setGravity(Gravity.RIGHT);
            trStatus.addView(tvStatus, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));

                /*Total Records*/
            TableRow trTotal = new TableRow(context);
            TextView tvTotalTxt = new TextView(context.getApplicationContext());
            tvTotalTxt.setText(TOTALREC);
            tvTotalTxt.setTextSize(17f);
            tvTotalTxt.setTextColor(Color.BLACK);
            tvTotalTxt.setGravity(Gravity.LEFT);
            trTotal.addView(tvTotalTxt, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));

            TextView tvTotal = new TextView(context.getApplicationContext());
            tvTotal.setText(String.valueOf(diagnostic.getTotal()));
            tvTotal.setTextSize(17f);
            tvTotal.setTextColor(Color.BLACK);
            tvTotal.setGravity(Gravity.RIGHT);
            trTotal.addView(tvTotal, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1.8f));
            // tcg.addView(trChargeDetail);

                /*Timestamp*/
            TableRow trTimestamp = new TableRow(context);

            TextView tvTs = new TextView(context.getApplicationContext());
            tvTs.setText(TS);
            tvTs.setTextSize(17f);
            tvTs.setTextColor(Color.BLACK);
            tvTs.setGravity(Gravity.LEFT);
            trTimestamp.addView(tvTs, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3.2f));

            TextView tvTimestamp = new TextView(context.getApplicationContext());
            tvTimestamp.setText(diagnostic.getTs());
            tvTimestamp.setTextSize(17f);
            tvTimestamp.setTextColor(Color.BLACK);
            tvTimestamp.setGravity(Gravity.RIGHT);
            trTimestamp.addView(tvTimestamp, new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2f));

            tdiagnostic.addView(trStatus);
            tdiagnostic.addView(trTotal);
            tdiagnostic.addView(trTimestamp);
            tdiagnostic.addView(v);
        }
    }

}

package com.xesi.xenuser.kuryentxtreadbill.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xesi.xenuser.kuryentxtreadbill.Diagnostics;
import com.xesi.xenuser.kuryentxtreadbill.Homepage;
import com.xesi.xenuser.kuryentxtreadbill.R;
import com.xesi.xenuser.kuryentxtreadbill.Reports;
import com.xesi.xenuser.kuryentxtreadbill.Tools;
import com.xesi.xenuser.kuryentxtreadbill.Sync;
import com.xesi.xenuser.kuryentxtreadbill.Transaction;
import com.xesi.xenuser.kuryentxtreadbill.adapter.RateWarningAdapter;
import com.xesi.xenuser.kuryentxtreadbill.dao.DUPropertyDAO;
import com.xesi.xenuser.kuryentxtreadbill.dao.billdao.BillHeaderDAO;
import com.xesi.xenuser.kuryentxtreadbill.interfaces.TaskCallBack;
import com.xesi.xenuser.kuryentxtreadbill.model.Diagnostic;
import com.xesi.xenuser.kuryentxtreadbill.model.RateMasterStatus;
import com.xesi.xenuser.kuryentxtreadbill.util.UniversalHelper;

import java.io.File;
import java.util.List;

/**
 * Created by xenuser on 2/8/2017.
 */
public class MsgDialog {
    Context context;
    private Dialog dialog;
    private RateWarningAdapter ratewarning;
    private DUPropertyDAO duPropertyDAO;

    public MsgDialog(Context context) {
        this.context = context;
    }

    public void showErrDialogCancelAbleWithIntent(Context context,String msg) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            System.setProperty("res", "OK");
            Intent i = new Intent(context, context.getClass());
            context.startActivity(i);
            ((Activity) context).finish();

        });
        dialog.show();
    }

    public void showErrDialog(String msg) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            System.setProperty("res", "OK");
        });
        dialog.show();
    }

    public void showNavErrDialog(String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context.getApplicationContext(), Transaction.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        });
        dialog.show();
    }

    public void displayRateWarning(List<RateMasterStatus> rateMasterStatuses, final DialogListener dialogListener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rate_status_dialog);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOkay);
        ListView lv = (ListView) dialog.findViewById(R.id.lvRateList);
        ratewarning = new RateWarningAdapter(context, rateMasterStatuses);
        lv.setAdapter(ratewarning);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("OK");
        });

        dialog.show();
    }

    public void showWarningDialog(String title, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.callstatus_info_popup);
        dialog.setTitle(title);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context, Diagnostics.class);
            context.startActivity(intent);
            ((Activity)context).finish();
            System.setProperty("res", "OK");
        });
        dialog.show();
    }


    /* Reports */
    public void showReportSuccessDialog(String msg) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView);
        status.setText(msg);
        image.setImageResource(R.drawable.success);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(context.getApplicationContext(), Reports.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        });
        dialog.show();
    }

    public void checkConnectionDialog(String msg) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent i = new Intent(context.getApplicationContext(), Tools.class);
            context.startActivity(i);
            ((Activity) context).finish();
        });
        dialog.show();
    }

    public void showConfirmDialog(String msg, final DialogListener dialogListener) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.confirm_dialog_layout);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Yes");
        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Cancel");
        });
        dialog.show();
    }

    public void showConfirmDialogVoid(String msg, final DialogListener dialogListener) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.confirm_dialog_layout_void);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnVoid =(Button) dialog.findViewById(R.id.btnVoid);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Yes");
        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Cancel");
        });
        btnVoid.setOnClickListener(v->{
            dialog.dismiss();
            dialogListener.onReturnValue("Void");
        });
        dialog.show();
    }

    public void showUpdateDialog(String errMsg, final TaskCallBack taskCallBack) {
        dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.setContentView(R.layout.dialog_warning_confirm);
        dialog.setCancelable(false);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(errMsg);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                taskCallBack.onTaskComplete(true);
            }
        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            taskCallBack.onTaskComplete(false);
        });
        dialog.show();
    }

    public void showConfirmReading(String title, double prev, double cur, double kwhConsumption, String note, final DialogListener dialogListener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_confirm_reading);
        LinearLayout llNote = (LinearLayout) dialog.findViewById(R.id.llNote);
        TextView tvNote = (TextView) dialog.findViewById(R.id.note);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView prevReading = (TextView) dialog.findViewById(R.id.tvPrevReading);
        TextView currentReading = (TextView) dialog.findViewById(R.id.tvCurrentReading);
        TextView tvConsumption = (TextView) dialog.findViewById(R.id.tvConsumption);
        tvTitle.setText(title);
        currentReading.setText(UniversalHelper.df.format(cur));
        prevReading.setText(UniversalHelper.df.format(prev));
        tvConsumption.setText(UniversalHelper.df.format(kwhConsumption));
        if (!note.equals(""))
            tvNote.setText("Note: " + note);
        else
            llNote.setVisibility(View.GONE);

        Button btnPrintReading = (Button) dialog.findViewById(R.id.btnPrintReading);
        btnPrintReading.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Print");
        });
        duPropertyDAO = new DUPropertyDAO(context);
        if(duPropertyDAO.getPropertyValue("IS_PRINT_READING_DISABLE").equals("Y")) {
            dialog.findViewById(R.id.btnPrintReading).setBackground(context.getResources().getDrawable(R.drawable.btn_disable_v2));
            btnPrintReading.setOnClickListener(v -> {});
        }
        Button btnGenerate = (Button) dialog.findViewById(R.id.btnGenerate);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);


        btnGenerate.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Generate");
        });
        btnSave.setOnClickListener(v -> {
            dialog.dismiss();
            dialogListener.onReturnValue("Save");
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void promptReadingEntry(double curRdg, final DialogListener dialogListener) {
        final AlertDialog.Builder promptDialog = new AlertDialog.Builder(context, R.style.Theme_Dialog);
        LayoutInflater factory = LayoutInflater.from(context);
        final View f = factory.inflate(R.layout.dialog_prompt_reading, null);
        promptDialog.setTitle("Please re-enter reading");
        promptDialog.setView(f);

        AlertDialog alertDialog = promptDialog.create();
        Button btnSave = (Button) f.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            EditText etCurRdg = (EditText) f.findViewById(R.id.etReadingEntry);
            if (etCurRdg.getText().toString().equals("")) {
                etCurRdg.setError("Please re-enter the current reading");
                etCurRdg.setText("");
            } else {
                double dRdg = Double.parseDouble(etCurRdg.getText().toString());
                if (dRdg == curRdg) {
                    alertDialog.dismiss();
                    dialogListener.onReturnValue("Yes");
                } else {
                    etCurRdg.setError("Current reading did not match on the previous entry");
                    etCurRdg.setText("");
                }
            }
        });
        Button btnCancel = (Button) f.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
            dialogListener.onReturnValue("Cancel");
        });
        alertDialog.show();
    }

    public void showAlert(Context contextName, String message, final DialogListener dialogListener) {
        new AlertDialog.Builder(contextName)
                .setTitle("Alert!")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Cancel", (dlg, something) -> dialogListener.onReturnValue("No"))
                .setPositiveButton("OK", (dlg, something) -> dialogListener.onReturnValue("OK")).show();
    }

    public void showAlertNoCancel(Context contextName,String title, String message, final DialogListener dialogListener) {
        new AlertDialog.Builder(contextName)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dlg, something) ->dialogListener.onReturnValue("OK")).show();
    }

    public void showPrintSuccessDialog(String msg,boolean lastSeq) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_callstatus_popup);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView);
        status.setText(msg);
        image.setImageResource(R.drawable.success);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            if(lastSeq){
                Intent i = new Intent(context, Transaction.class);
                context.startActivity(i);
                ((Activity) context).finish();
            }
        });
        dialog.show();
    }

    public void showDialog(String title, String msg) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.callstatus_info_popup);
        dialog.setTitle(title);
        TextView status = (TextView) dialog.findViewById(R.id.tvStatus);
        status.setText(msg);
        Button okBtn = (Button) dialog.findViewById(R.id.btnOK);

        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void showReupload(Context contextName, String message, final DialogListener dialogListener) {
        new AlertDialog.Builder(contextName)
                .setTitle("Upload")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Cancel", (dlg, something) -> dialogListener.onReturnValue("No"))
                .setPositiveButton("Reupload", (dlg, something) -> dialogListener.onReturnValue("OK")).show();
    }

    public interface DialogListener {
        void onReturnValue(String value);
    }

}

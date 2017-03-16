package pers.loren.appupdate;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by loren on 2017/3/16.
 */

public class UpdateDialog {

    public static void ShowUpdateDialog(Context mContext, String content, final OnConfirmListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.ProcessDialog)
                .setCancelable(false).create();
        View view = LayoutInflater.from(mContext).inflate(R.layout.update_dialog_layout, null);
        alertDialog.show();
        alertDialog.setContentView(view);
        ((TextView) alertDialog.findViewById(R.id.content_tv)).setText(content);
        alertDialog.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                listener.onConfirm();
            }
        });
        alertDialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public static void ShowUpdateForceDialog(Context mContext, String content, final OnConfirmListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.ProcessDialog)
                .setCancelable(false).create();
        View view = LayoutInflater.from(mContext).inflate(R.layout.update_dialog_layout, null);
        alertDialog.show();
        alertDialog.setContentView(view);
        ((TextView) alertDialog.findViewById(R.id.content_tv)).setText(content);
        alertDialog.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                v.setBackgroundColor(Color.GRAY);
                listener.onConfirm();
            }
        });
        alertDialog.findViewById(R.id.cancel_tv).setVisibility(View.GONE);
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

}

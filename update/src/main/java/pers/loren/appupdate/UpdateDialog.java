package pers.loren.appupdate;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import pers.loren.appupdate.view.DownloadProcessView;

/**
 * Created by loren on 2017/3/16.
 */
class UpdateDialog {

    private static AlertDialog forceUpdateDialog = null;

    static void showUpdateDialog(Context mContext, String content, final OnConfirmListener listener) {
        final AlertDialog updateDialog = new AlertDialog.Builder(mContext, R.style.ProcessDialog)
                .setCancelable(false).create();
        View view = View.inflate(mContext, R.layout.update_dialog_layout, null);
        updateDialog.show();
        updateDialog.setContentView(view);
        updateDialog.findViewById(R.id.process_view).setVisibility(View.GONE);
        ((TextView) updateDialog.findViewById(R.id.content_tv)).setText(content);
        updateDialog.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
                listener.onConfirm();
            }
        });
        updateDialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });
    }

    static void showUpdateForceDialog(Context mContext, String content, final OnConfirmListener listener) {
        forceUpdateDialog = new AlertDialog.Builder(mContext, R.style.ProcessDialog)
                .setCancelable(false).create();
        View view = View.inflate(mContext, R.layout.update_dialog_layout, null);
        forceUpdateDialog.show();
        forceUpdateDialog.setContentView(view);
        ((TextView) forceUpdateDialog.findViewById(R.id.content_tv)).setText(content);
        forceUpdateDialog.findViewById(R.id.confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                v.setBackgroundColor(Color.GRAY);
                forceUpdateDialog.findViewById(R.id.process_view).setVisibility(View.VISIBLE);
                listener.onConfirm();
            }
        });
        forceUpdateDialog.findViewById(R.id.cancel_tv).setVisibility(View.GONE);
    }

    static void setForceUpdateDialogProcess(int process) {
        if (null != forceUpdateDialog) {
            ((DownloadProcessView) forceUpdateDialog.findViewById(R.id.process_view)).setCurrentProcess(process);
        }
    }

    protected interface OnConfirmListener {
        void onConfirm();
    }

}

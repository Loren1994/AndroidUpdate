package pers.loren.appupdate;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by loren on 2017/3/16.
 */

public class UpdateDialog {
    private static AlertDialog dialog;

    public static void ShowUpdateDialog(Context mContext, String content, View.OnClickListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.ProcessDialog)
                .setCancelable(false).create();
        dialog = alertDialog;
        alertDialog.show();
        alertDialog.setContentView(R.layout.update_dialog_layout);
        ((TextView) alertDialog.findViewById(R.id.content_tv)).setText(content);
        alertDialog.findViewById(R.id.confirm_tv).setOnClickListener(listener);
        alertDialog.findViewById(R.id.cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}

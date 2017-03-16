package com.lishide.nohttputils.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import com.lishide.nohttputils.R;

/**
 * 加载等待 Dialog
 */
public class WaitDialog extends ProgressDialog {

    public WaitDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setProgressStyle(STYLE_SPINNER);
        setMessage(context.getText(R.string.wait_dialog_title));
    }

}

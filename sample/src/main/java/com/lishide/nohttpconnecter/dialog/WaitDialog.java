package com.lishide.nohttpconnecter.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import com.lishide.nohttpconnecter.R;


/**
 * Created in Oct 23, 2015 1:19:04 PM.
 *
 * @author Yan Zhenjie.
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

package com.lishide.nohttpconnecter.view.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

public class ImageDialog extends AlertDialog.Builder {

    private AlertDialog alertDialog;

    private ImageView imageView;

    public ImageDialog(Context context) {
        super(context);
        imageView = new ImageView(getContext());
        setView(imageView);
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/
        imageView.setOnClickListener(v -> dismiss());
    }

    public void setImage(int image) {
        imageView.setImageResource(image);
    }

    public void setImage(Drawable image) {
        imageView.setImageDrawable(image);
    }

    public void setImage(Bitmap image) {
        imageView.setImageBitmap(image);
    }

    public void dismiss() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }

    public void cancel() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.cancel();
    }

    @Override
    public AlertDialog show() {
        if (alertDialog == null)
            alertDialog = create();
        if (!alertDialog.isShowing())
            alertDialog.show();
        return alertDialog;
    }
}

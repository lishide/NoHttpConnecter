package com.lishide.nohttpconnecter.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;

public class Toast {

    public static void show(Context context, CharSequence msg) {
        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show();
    }

    public static void show(Context context, @StringRes int stringId) {
        android.widget.Toast.makeText(context, stringId, android.widget.Toast.LENGTH_LONG).show();
    }

    public static void show(View view, CharSequence msg) {
        show(view.getContext(), msg);
    }

    public static void show(View view, @StringRes int stringId) {
        show(view.getContext(), stringId);
    }

}

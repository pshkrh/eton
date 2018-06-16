package com.pshkrh.eton.Helper;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarHelper {
    public static void snackLong(View view, String message){
        Snackbar.make(view,message,Snackbar.LENGTH_LONG).show();
    }

    public static void snackShort(View view, String message){
        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
    }
}

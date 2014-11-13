package com.jforce.chapelhillnextbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by justinforsyth on 10/25/14.
 */
public class LicenseDialogPreference extends DialogPreference {

    public LicenseDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        //super(context, attrs, R.style.CustomDialogTheme);
        setDialogLayoutResource(R.layout.dialog_license);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle("Licenses");
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getDialog().dismiss();
            }
        });

        builder.setNegativeButton(null, null);



        super.onPrepareDialogBuilder(builder);
    }


}

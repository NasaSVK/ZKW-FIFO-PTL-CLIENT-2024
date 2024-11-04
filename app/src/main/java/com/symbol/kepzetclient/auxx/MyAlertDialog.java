package com.symbol.kepzetclient.auxx;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

//https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
//https://stackoverflow.com/questions/16596264/what-is-handler-class

public class MyAlertDialog {

    AlertDialogResult MyDialogResult;
    private enum AlertDialogResult{
        YES,NO;
    }

    private AlertDialog.Builder builder;
    public void createDialog(Context pContext, Runnable pRunnable, String pTitle, String pMessage){
        builder = new AlertDialog.Builder(pContext);
        builder.setTitle("Confirm");
        builder.setMessage("Wrong format, are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, but close the dialog
                pRunnable.run();
                MyDialogResult = AlertDialogResult.YES;
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                MyDialogResult = AlertDialogResult.NO;
                dialog.dismiss();
            }
        });
    }

    public void showDialog(){
        AlertDialog alert = builder.create();
        alert.show();
    }
}

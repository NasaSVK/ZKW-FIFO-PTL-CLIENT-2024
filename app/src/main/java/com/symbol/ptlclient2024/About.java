package com.symbol.ptlclient2024;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


//http://tekeye.uk/android/examples/ui/about-box-in-android-app
//https://www.youtube.com/watch?v=plnLs6aST1M
public class About {
    static String VersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }
    @SuppressLint("ResourceType")
    public static void Show(Activity callingActivity) {
        //Use a Spannable to allow for links highlighting
        /*
        SpannableString aboutText = new SpannableString("Version " + VersionName(callingActivity)+ "\n"
                + callingActivity.getString(R.string.about));*/
        //String aboutText = new String("Ver." + VersionName(callingActivity) + " ("+ Helpers.getAppTimeStamp(callingActivity)+")");
        //String aboutText = new String(Helpers.getAppTimeStamp2(callingActivity))+ " ("+ Helpers.getAppTimeStamp(callingActivity)+")";
        String aboutText = new String(Helpers.getAppTimeStampVer(callingActivity));

        //Generate views to pass to AlertDialog.Builder and to set the text

        View aboutLayout;
        TextView tvVerzia;
        TextView tvPodpora;

        //try {
            //Inflate the custom view
            LayoutInflater inflater = callingActivity.getLayoutInflater();
            aboutLayout = inflater.inflate(R.layout.about_dlg, (ViewGroup) callingActivity.findViewById(R.id.aboutLayout));
            tvVerzia = (TextView) aboutLayout.findViewById(R.id.tvVerzia);
            tvPodpora = (TextView) aboutLayout.findViewById(R.id.tvPodpora);
        //}
        //catch(InflateException e) {
            //Inflater can throw exception, unlikely but default to TextView if it occurs
             //tvVerzia = new TextView(callingActivity);
            //tvPodpora = (TextView) aboutLayout.findViewById(R.id.tvPodpora);
        //}

        //Set the about text
        tvVerzia.setText(aboutText);

        // Now Linkify the text
        //Linkisfy.addLinks(tvVerzia, Linkify.ALL);
        Linkify.addLinks(tvPodpora, Linkify.ALL);

        //Build and show the dialog
        new AlertDialog.Builder(callingActivity)
                .setTitle(callingActivity.getString(R.string.app_name))
                .setCancelable(true)
                //.setIcon(R.mipmap.slon)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("OK", null)
                .setView(aboutLayout)
                .show();    //Builder method returns allow for method chaining
    }
}

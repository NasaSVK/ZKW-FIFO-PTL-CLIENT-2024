package com.symbol.ptlclient2024;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.symbol.ptlclient2024.tcp.Utils;

import java.io.File;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Helpers {

    public enum eHALA { DVOJKA, TROJKA, JEDNOTKA };

    public static final int INTERVAL = 4000;

    private static ArrayList<View> Childrens;

    public static String parsujKod(String pKOD){
        int index  = pKOD.lastIndexOf(":");
        String result = pKOD.substring(index + 2);
        return result;
    }

    public static String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm:ss");
        Date date = new Date();
        return (dateFormat.format(date));
        //System.out.println(dateFormat.format(date));
    }

    //https://www.youtube.com/watch?v=L-1lwnjZFGg
    //https://stackoverflow.com/questions/6687666/android-how-to-set-the-colour-of-a-toasts-text
    public static void redToast(Context pContext, String pText){

        Toast toast = Toast.makeText(pContext, pText, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.red_toast_style);
        TextView v = (TextView)view.findViewById(android.R.id.message);
        v.setTextColor(ContextCompat.getColor(pContext, R.color.green)); //https://stackoverflow.com/questions/5271387/get-color-int-from-color-resource
        //view.setBackgroundColor(Color.BLACK);
        //TextView text = (TextView) view.findViewById(android.R.id.message);
        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
        toast.show();
    }

    public static void greyToast(Context pContext, String pText, int ToastLength){

        Toast toast = Toast.makeText(pContext, pText, ToastLength);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.grey_toast_style);
        TextView v = (TextView)view.findViewById(android.R.id.message);
        v.setTextColor(ContextCompat.getColor(pContext, R.color.back)); //https://stackoverflow.com/questions/5271387/get-color-int-from-color-resource
        //view.setBackgroundColor(Color.BLACK);
        //TextView text = (TextView) view.findViewById(android.R.id.message);
        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
        toast.show();
    }



    public static String  OrezString(String pStr){
        return pStr.trim();
    }

    public static String timestampAsString(Timestamp timestamp) {
//        long TSLong = timestamp.getTime();
//        return DateTimeFormat.forPattern("dd/MM/yy HH:mm:ss").print(TSLong);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        if (timestamp == null) return "N/A";
        String dateString = formatter.format(timestamp);
        return dateString;
    }

    public static String getLocalIP(){

        return Utils.getIPAddress(true);

//        try {
//            return InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//
//            throw new RuntimeException(e);
//        }
    }

    public static eHALA getHALLfromIP(){
        String localIP = getLocalIP();
        int pocet = 0;
        int index = 0;
        while (pocet < 3 ) {
            if (localIP.charAt(index) == '.')
                pocet++;
            index++;
        }
       String result =  localIP.substring(index, localIP.length());
        Integer resultInt =  Integer.parseInt(result);

        Integer[] hala2IP = {241, 242, 243, 40};
        Integer[] hala3IP = {244, 245, 148, 77};

        for (int cislo: hala2IP) {
            if(cislo == resultInt) return eHALA.DVOJKA;
        }

        for (int cislo: hala3IP) {
            if(cislo == resultInt) return eHALA.TROJKA;
        }

        return eHALA.JEDNOTKA;
    }




    public static String getId(View view) {
        if (view.getId() == View.NO_ID) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }


        public static String getNow() {

            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.now().format(formatter);
            }
            return "1970-01-01 01:00:00";
        }


    private static  void show_children(View v) {

        ViewGroup viewgroup=(ViewGroup)v;
        for (int i=0;i<viewgroup.getChildCount();i++) {
            View v1=viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup) show_children(v1);
            Log.d("APPNAME",v1.toString());
            Helpers.Childrens.add(v1);
        }
    }

    public  static ArrayList<View> getChildrens(View pV){
        Childrens = new ArrayList<View>();
        show_children(pV);
        return Helpers.Childrens;
    }


    public static boolean Ping(String pIP){
            try{
                InetAddress address = InetAddress.getByName(pIP);
                boolean reachable = address.isReachable(600);
                System.out.println("Is host reachable? " + reachable);
                return reachable;
            }
            catch (Exception e){
                e.printStackTrace();
                return false;
            }
    }

    //https://stackoverflow.com/questions/26311470/what-is-the-equivalent-of-javascript-settimeout-in-java
    //Asynchronous implementation with JDK 1.8:
    public static Thread setTimeout(Runnable runnable, int delay){
        Thread thread = null;
        thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (InterruptedException e){
                System.err.println(e);
            }
        });
        thread.start();
        return  thread;
    }

    //https://stackoverflow.com/questions/26311470/what-is-the-equivalent-of-javascript-settimeout-in-java
    //To deal with the current running thread only use a synchronous version:
    public static void setTimeoutSync(Runnable runnable, int delay) {
        try {
            Thread.sleep(delay);
            runnable.run();
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    //https://stackoverflow.com/questions/22979806/display-the-android-application-apk-creation-date-in-application?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public static String getAppTimeStamp(Context context) {
        String timeStamp = "";

        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long time = new File(appFile).lastModified();

            //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd-MM-yyyy");
            timeStamp = formatter.format(time);

        } catch (Exception e) {

        }

        return timeStamp;
    }


    public static String getAppTimeStamp2(Context context) {

        String timeStamp = null;
        try
        {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long time = new File(appFile).lastModified();

            //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yy.MM");
            timeStamp = formatter.format(time);
        }
        catch (Exception e) {
            Helpers.redToast(context, "getAppTimeStamp2: " + e.getMessage());
        }

        return timeStamp;
    }

    public static String getAppTimeStampVer(Context context) {

        String timeStamp = null;
        try
        {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long time = new File(appFile).lastModified();

            //SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyyMMdd");
            timeStamp = formatter.format(time);
        }
        catch (Exception e) {
            Helpers.redToast(context, "getAppTimeStamp2: " + e.getMessage());
        }

        return timeStamp;
    }


}

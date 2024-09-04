package com.symbol.kepzetclient;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.symbol.kepzetclient.tcp.Utils;

import org.joda.time.format.DateTimeFormat;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Helpers {

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

        Toast toast = Toast.makeText(pContext, pText, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.red_toast_style);
        TextView v = (TextView)view.findViewById(android.R.id.message);
        v.setTextColor(ContextCompat.getColor(pContext, R.color.green)); //https://stackoverflow.com/questions/5271387/get-color-int-from-color-resource
        //view.setBackgroundColor(Color.BLACK);
        //TextView text = (TextView) view.findViewById(android.R.id.message);
        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
        toast.show();
    }

    public static String  OrezString(String pStr){
        return pStr.trim();
    }

    public static String timestampAsString(Timestamp timestamp) {
        return DateTimeFormat.forPattern("dd/MM/yy HH:mm:ss").print(timestamp.getTime());
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


    public static String getId(View view) {
        if (view.getId() == View.NO_ID) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }



        public static boolean Ping(String pAddress){
            try{
                InetAddress address = InetAddress.getByName(pAddress);
                boolean reachable = address.isReachable(10000);
                return reachable;
                //System.out.println("Is host reachable? " + reachable);
            } catch (Exception e){

                e.printStackTrace();
                return false;
            }
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



}

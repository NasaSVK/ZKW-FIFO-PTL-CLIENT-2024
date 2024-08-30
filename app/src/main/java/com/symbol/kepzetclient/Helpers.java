package com.symbol.kepzetclient;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.joda.time.format.DateTimeFormat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;

public class Helpers {

    public static String parsujKod(String pKOD){
        int index  = pKOD.lastIndexOf(":");
        String result = pKOD.substring(index + 2);
        return result;
    }

    public static String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
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

    public static InetAddress getLocalIP(){

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {

            throw new RuntimeException(e);
        }
    }


}

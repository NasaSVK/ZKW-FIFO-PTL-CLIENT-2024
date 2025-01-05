package com.symbol.ptlclient2024.auxx;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.symbol.ptlclient2024.Helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

public class Settings {



    public static Settings getSELF()
    {
        if (SELF == null)
        {
            //SELF = new Settings(MainActivity.getContext());
            SELF = new Settings();
        }
        return SELF;
    }

    private static Settings SELF;
    private static Settings _static;

    public static Settings getSTATIC(){
        return  _static;
    }

    private final static String SUBOR = "settings.txt";

    public String ServerIP = "10.16.0.141";
    public  int ServerPort = 9999;

    public  String ClientIP = "localhost";
    public int ClientPort = 9999;

    public int Time = 3000;

    public int Volume = 100;
    public String Password = "9876";

    public String DbName = "zkwPBL";
    public String DbUser = "sknasa";
    public Integer DbPort = 53563;

    public int MaxX =  99;
    public int MaxY =  9;

    public boolean ManualAccepting = true;


    //TODO: IGNORE LIST<PalNr>
    public boolean SaveToFile(Context ctx) {
        Gson gson = new Gson();
        //Type type = new TypeToken<List<Device>>() {}.getType();
        Type type = Settings.class;
        String json = gson.toJson(this, type);

        try {
            FileOutputStream fOut = ctx.openFileOutput(SUBOR, Context.MODE_PRIVATE);
            //FileOutputStream fOut = new FileOutputStream (new File(SUBOR));
            fOut.write(json.getBytes());
            fOut.close();
            //Toast.makeText(ctx, "Settings saved", Toast.LENGTH_SHORT).show();
            Helpers.greyToast(ctx,"Settings saved");
            return true;
        } catch (FileNotFoundException e) {
            Helpers.redToast(ctx, "Setting File not Found!!");
            return false;
            //throw new RuntimeException(e);
        } catch (IOException e) {
            Helpers.redToast(ctx, "UnknownI/O Error!");
            return false;
            //throw new RuntimeException(e);
        }
    }

    public boolean LoadFromFile(Context ctx){

        String temp="";

        FileInputStream is = null;
        try {
            is = ctx.openFileInput(SUBOR);
            File file = new File(SUBOR);
            Log.i("PATH", "Path: " + file.getCanonicalPath());
            //Log.i("PATH", "Dir Path: " + ctx.getFilesDir());

        } catch (FileNotFoundException e) {
            //throw new RuntimeException(e);
            return false;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            temp = rd.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //if (temp == null) return false;
        //https://stackoverflow.com/questions/47536005/cant-toast-on-a-thread-that-has-not-called-looper-prepare
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Toast toast = Toast.makeText(ctx, "file read", Toast.LENGTH_SHORT);
                //toast.show();
            }
        });

        //Toast.makeText(ctx,"file read",Toast.LENGTH_SHORT).show();

//        catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }

        Gson gson = new Gson();
        //Type type = new TypeToken<List<Device>>() {}.getType();
        Type type = Settings.class;
        //List<Device> fromJson = gson.fromJson(temp, type);
        Settings result  = gson.fromJson(temp, type);
        _static  = gson.fromJson(temp, type);
        this.ServerIP =  result.ServerIP;
        this.ClientIP =  result.ClientIP;
        this.ServerPort =  result.ServerPort;
        this.ClientPort=  result.ClientPort;
        this.ManualAccepting = result.ManualAccepting;
        this.Password = result.Password;
        this.Time = result.Time;
        this.Volume = result.Volume;
        this.DbName = result.DbName;
        this.DbPort = result.DbPort;
        this.DbUser = result.DbUser;
        this.MaxX = result.MaxX;
        this.MaxY = result.MaxY;
        //this.devices = fromJson;
        return true;
    }
}




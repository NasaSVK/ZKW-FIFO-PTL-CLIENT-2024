package com.symbol.ptlclient2024.auxx;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.symbol.ptlclient2024.Helpers;
import com.symbol.ptlclient2024.MainActivity;

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
            if (!SELF.LoadFromFile(MainActivity.getContext()))
                Helpers.redToast(MainActivity.getContext(),"ERROR: CONFIGURATION FILE WAS NOT LOADED");
        }
        return SELF;
    }
    //#################################################################################
    //TIETO DATA SA NACITAJU DO KONFIGU OBRAZOVKY BEZPROSTREDNE PO INSTALACII APLIKACIE
    //#################################################################################
    private static Settings SELF;
    private static Settings _static;

    public static Settings getSTATIC(){
        return  _static;
    }

    private final static String SUBOR = "settings.txt";

    public String ServerIP = "10.16.0.999";
    public  int ServerPort = 9999;

    public  String ClientIP = "10.16.0.999";
    public int ClientPort = 9999;

    public int Time = 3000;

    public int Volume = 100;
    public String Password = "8765";

    public String DbName = "zkwPBLxx";
    public String DbUser = "skxxxx";
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
            //Helpers.redToast(ctx, "Setting File not Found!!");
            Log.e("EXCEPTION", "FileNotFoundException (Setting.java):" +e.getMessage());
            return false;

        } catch (IOException e) {
            //Helpers.redToast(ctx, "Setting file I/O ERROR!");
            Log.e("EXCEPTION", "IOException (Setting.java):" +e.getMessage());
            return false;
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
            Log.e("EXCEPTION", "FileNotFoundException (Setting.java):" +e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("EXCEPTION", "Exception (Setting.java):" +e.getMessage());
            return false;
        }

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("EXCEPTION", "UnsupportedEncodingException (Setting.java):" +e.getMessage());
            return false;
        }
        try {
            temp = rd.readLine();
        } catch (IOException e) {
            Log.e("EXCEPTION", "IOException (Setting.java):" +e.getMessage());
            return false;
        }

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

        try {
            rd.close();
        } catch (IOException e) {
            Log.e("EXCEPTION", "IOException (Setting.java): " + e.getMessage());
        }

        try {
            is.close();
        } catch (IOException e) {
            Log.e("EXCEPTION", "IOException (Setting.java): " + e.getMessage());
        }
        return true;
    }
}




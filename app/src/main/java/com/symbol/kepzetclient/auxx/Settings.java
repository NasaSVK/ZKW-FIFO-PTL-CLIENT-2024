package com.symbol.kepzetclient.auxx;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.symbol.kepzetclient.Helpers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

public class Settings {

    public static Settings SELF = new Settings();

    private final static String SUBOR = "settings.txt";

    public String ServerIP = "10.0.0.1";
    public int ServerPort = 1433;

    public String ClientIP = "10.0.0.2";
    public int ClientPort = 2020;

    public int Time = 1500;

    public int Volume = 5;

    public String Password = "sknasa";

    public boolean ManualAccepting = true;

    //TODO: IGNORE LIST<PalNr>
    public boolean SaveToFile(Context ctx) {
        Gson gson = new Gson();
        //Type type = new TypeToken<List<Device>>() {}.getType();
        Type type = Settings.class;
        String json = gson.toJson(this, type);

        try {
            FileOutputStream fOut = ctx.openFileOutput(SUBOR, Context.MODE_PRIVATE);
            fOut.write(json.getBytes());
            fOut.close();
            Toast.makeText(ctx, "Settings saved", Toast.LENGTH_SHORT).show();
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

    public boolean LoadToFile(Context ctx){

        String temp="";

        FileInputStream is = null;
        try {
            is = ctx.openFileInput(SUBOR);
        } catch (FileNotFoundException e) {
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
            Toast.makeText(ctx,"file read",Toast.LENGTH_SHORT).show();

//        catch(Exception e){
//            e.printStackTrace();
//            return false;
//        }

        Gson gson = new Gson();
        //Type type = new TypeToken<List<Device>>() {}.getType();
        Type type = Settings.class;
        //List<Device> fromJson = gson.fromJson(temp, type);
        Settings result  = gson.fromJson(temp, type);
        this.ServerIP =  result.ServerIP;
        this.ClientIP =  result.ClientIP;
        this.ServerPort =  result.ServerPort;
        this.ClientPort=  result.ClientPort;
        this.ManualAccepting = result.ManualAccepting;
        this.Password = result.Password;
        this.Time = result.Time;
        this.Volume = result.Volume;

        //this.devices = fromJson;
        return true;
    }
}




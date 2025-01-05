package com.symbol.ptlclient2024.auxx;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.symbol.ptlclient2024.Helpers;
import com.symbol.ptlclient2024.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LogStream {

    //private static LogStream LS;
// SINGLETON SA V PRIPADE NUTNEHO  DODANIA KONTEXTU NEHODI
//    public LogStream getInstance(){
//        if (LS == null)
//            return LS = new LogStream();
//        else
//            return LS;
//    }

    private Context _context;

    //https://stackoverflow.com/questions/8152125/how-to-create-text-file-and-insert-data-to-that-file-on-android
    private String logFileName;
    //HODNOTY INDEXOV POLA NASTAVENI a ICH VYZNAM
    public int
            serverIp = 0,
            serverPort = 1,
            loclaPort = 2,
            userPass = 3,
            adminPass = 4,
            lightTime = 5,
            audioVolume = 6,
            allowManual = 7,
            dbConnStr = 8;


    public LogStream(Context pContext) {

        _context = pContext;
    }

    String getLogFileName(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //https://stackoverflow.com/questions/28177370/how-to-format-localdate-to-string
                //https://www.w3schools.com/java/java_date.asp
                return "LOG_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+".txt";
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
            Log.e("RUNTIME EXCEPTION", String.join(", ", e.toString()));
            Helpers.redToast(_context,"Exception at LogStream.getLogFileName()");
            MainActivity.getInstance().logStream.logData("EXCEPTION: Exception at LogStream.getLogFileName()");
            return null;
        }
        return null;
    }

    private void checkIfLogExists()
    {
        ///data/user/0/com.symbol.kepzetclient/LOG_2024-10-02.txt
        //https://stackoverflow.com/questions/5527764/get-application-directory
        logFileName = MainActivity.getInstance().getApplicationInfo().dataDir + "/" + getLogFileName();
        File logFile = new File(logFileName);
        if (!logFile.exists())
        {
            try {
                //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                //FileWriter writer = new FileWriter(logFile);
                //writer.flush();
                //writer.close();
                //Toast.makeText(pContext, "Saved", Toast.LENGTH_SHORT).show();
                FileOutputStream fOut = new FileOutputStream(logFile, false);
                //fOut.write(json.getBytes());
                fOut.close();


            }
            catch (IOException e) {
                e.printStackTrace();
                Log.e("KEPZET EXCEPTION", e.toString());
                Helpers.redToast(_context,"IOException at LogStream.checkIfLogExists()");
                MainActivity.getInstance().logStream.logData("IOEXCEPTION: at LogStream.getLogFileName()");
            }
        }
    }

    public void logData(String pData)
    {
        checkIfLogExists();
        try {
            FileWriter writer = new FileWriter(logFileName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //https://jenkov.com/tutorials/java-internationalization/simpledateformat.html
                String CurrentDateTime  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                writer.append( CurrentDateTime + " : " + pData + "\n");
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            //throw new RuntimeException(e);
            Log.e("KEPZET EXCEPTION", String.join(", ", e.toString()));
            Helpers.redToast(_context,"IOExcepton at LogStream.logData(String pData)");
            MainActivity.getInstance().logStream.logData("IOEXCEPTION: at LogStream.logData(String pData)");
        }
        catch (Exception e) {
            Log.e("KEPZET EXCEPTION", String.join(", ", e.toString()));
            Helpers.redToast(_context,"Exception at LogStream.logData(String pData)");
            MainActivity.getInstance().logStream.logData("EXCEPTION: at LogStream.logData(String pData)");
        }
    }
}

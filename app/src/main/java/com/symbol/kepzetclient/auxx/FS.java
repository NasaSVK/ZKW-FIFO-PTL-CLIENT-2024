package com.symbol.kepzetclient.auxx;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class FS {
    private String logFileName;
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

    public void logData(String data){
        _checkIfLogExists();
    }

    private void _checkIfLogExists() {
    }

    public FS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                logFileName = "\\Program Files\\barcodereader_ce_cf2\\"+"LOG_"+ new SimpleDateFormat("yyyy-MM-dd").parse(LocalDateTime.now().toString())  + ".txt";
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

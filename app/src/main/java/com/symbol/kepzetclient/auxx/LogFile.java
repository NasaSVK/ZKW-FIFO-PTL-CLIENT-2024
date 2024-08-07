package com.symbol.kepzetclient.auxx;

import android.content.Context;
import android.os.Build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogFile {

    String route;
    Context context;

    private LocalDate assignedDate;


    private DateTimeFormatter formatter;


    public LogFile(Context context, LocalDate pDate) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.route= context.getFilesDir() + "/log_"+ pDate.getDayOfMonth()+"-"+pDate.getMonth()+"-"+pDate.getYear()+".txt";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        assignedDate = pDate;
    }

    public void appendLog(String text){

        File logFile = new File(route);

        if (!logFile.exists()){
            try{
                logFile.createNewFile();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        try{
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buf.append(LocalDateTime.now().format(this.formatter) + ": " + text);
            }
            buf.newLine();
            buf.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public LocalDate getAssignedDate() {
        return assignedDate;
    }
}


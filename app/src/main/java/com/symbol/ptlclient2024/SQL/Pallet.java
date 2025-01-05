package com.symbol.ptlclient2024.SQL;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

public class Pallet {

    public int ID;
    public String PALLET_NUMBER;
    //GETTER
    public String getPalletNumber(){
        return PALLET_NUMBER;
    }
    //SETTER
    public void setPalletNumber(String newPalletNumber){
        this.PALLET_NUMBER = newPalletNumber;
    }

    //private LocalDateTime DATE_TIME;
    private Timestamp DATE_TIME;

    //public LocalDateTime getDateTime(){
    //    return DATE_TIME;
    //}

    public Timestamp getDateTime(){

        return this.DATE_TIME;
    }

    //    public void setDateTime(LocalDateTime newDateTime){
//        this.DATE_TIME = newDateTime;
//    }
    public void setDateTime(Timestamp newDateTime){

        //this.DATE_TIME = newDateTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(newDateTime.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long time = date.getTime();
        this.DATE_TIME =  new Timestamp(time);
    }

    private boolean ACTIVE;

    public boolean getActive(){
        return ACTIVE;
    }

    public void setActive(Boolean newActive){
        this.ACTIVE = newActive;
    }

}

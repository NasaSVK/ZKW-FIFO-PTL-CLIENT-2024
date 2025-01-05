package com.symbol.ptlclient2024.SQL;

import java.sql.Timestamp;

public class FIFO {

    private int ID;
    public int getID() {return ID;}
    public void setID(int pID) {

        this.ID = pID;
    }

    private int posX;
    public int getPosX() {
        return posX;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }

    private int posY;
    public int getPosY() {
        return posY;
    }
    public void setPosY(int posY) {
        this.posY = posY;
    }

    private String partNr;
    public String getPartNr(){
        return partNr; }
    public void setPartNr(String pPartNr) {
        this.partNr = pPartNr;}


    private int channel;
    public int getChannel() {
        return channel;}
    public void setChannel(int channel) {
        this.channel = channel;
    }


    private int count;
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    private int maxCount;
    public int getMaxCount() {
        return maxCount;
    }
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    private int pack;
    public int getPack() {
        return pack;
    }
    public void setPack(int pack) {
        this.pack = pack;
    }

    private int aLTime;
    public int getaLTime() {
        return aLTime;
    }
    public void setaLTime(int aLTime) {
        this.aLTime = aLTime;
    }

    private boolean aLCheck;
    public boolean isaLCheck() {
        return aLCheck;
    }
    public void setaLCheck(boolean aLCheck) {
        this.aLCheck = aLCheck;
    }

    private int aLBPTime;
    public int getaLBPTime() {
        return aLBPTime;
    }
    public void setaLBPTime(int aLBPTime) {
        this.aLBPTime = aLBPTime;
    }

    private Timestamp aLBPStart;
    public Timestamp getaLBPStart() {
        return aLBPStart;
    }
    public void setaLBPStart(Timestamp aLBPStart) {
        this.aLBPStart = aLBPStart;
    }

    private boolean aLBP;
    public boolean isaLBP() {
        return aLBP;
    }
    public void setaLBP(boolean aLBP) {
        this.aLBP = aLBP;
    }



}

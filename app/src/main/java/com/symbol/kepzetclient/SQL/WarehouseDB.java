package com.symbol.kepzetclient.SQL;

import java.sql.Timestamp;

public class WarehouseDB {

    int ID;
    int getID() {return ID;}

    private String partNr;
    String getPartNr(){
        return partNr; }

    public void setPartNr(String pPartNr) {
        this.partNr = pPartNr;}

    private String partNrFixed;
    public String getPartNrFixed() {
        return partNrFixed; }
    public void setPartNrFixed(String partNrFixed) {
        this.partNrFixed = partNrFixed;
    }

    private int channel;
    public int getChannel() {
        return channel;}
    public void setChannel(int channel) {
        this.channel = channel;
    }

    private String palleteNr;
    public String getPalleteNr() {
        return palleteNr; }
    public void setPalleteNr(String palleteNr) {
        this.palleteNr = palleteNr;
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


    private int posZ;
    public int getPosZ() {
        return posZ;
    }
    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    private int section;
    public int getSection() {
        return section;
    }
    public void setSection(int section) {
        this.section = section;
    }

    private Timestamp FIFODateTime;
    public Timestamp getFIFODateTime() {
        return FIFODateTime;
    }
    public void setFIFODateTime(Timestamp FIFODateTime) {
        this.FIFODateTime = FIFODateTime;
    }

    private boolean rem;
    public boolean isRem() {
        return rem;
    }
    public void setRem(boolean rem) {
        this.rem = rem;
    }

    private String prevPaletteNr;
    public String getPrevPaletteNr() {
        return prevPaletteNr;
    }
    public void setPrevPaletteNr(String prevPaletteNr) {
        this.prevPaletteNr = prevPaletteNr;
    }

    private int maxCount;

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}

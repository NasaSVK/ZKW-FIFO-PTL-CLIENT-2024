package com.symbol.ptlclient2024.SQL;

import android.content.Context;

import com.symbol.ptlclient2024.Helpers;
import com.symbol.ptlclient2024.auxx.PalletPlus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GetData {

    static public String ConnectionResult = "";
    static Boolean isSuccess = false;
    static public Statement stmt;
    //#############################################################################################################################################
    //len incicalizacia DB data bez realizacie spojenia
    //#############################################################################################################################################
    public static ConnectionToDB DB = new ConnectionToDB();

    public static ArrayList<WarehouseDB> getAllPallets(Context pContext) {
        ArrayList<WarehouseDB> data = new ArrayList<WarehouseDB>();
        String query = Query.INSTANCE.getAllParts();
        try {
            //stmt = DB.connection.createStatement(); //nemozno vytvorit Statement na conection, ktore uz ma Statement vytvoreny
            stmt = DB.getStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                WarehouseDB pallet = new WarehouseDB();
                pallet.ID = rs.getInt("id");
                pallet.setPalleteNr(rs.getString("paletteNr"));
                pallet.setFIFODateTime(rs.getTimestamp("FIFODatetime"));
                data.add(pallet);
            }
            stmt.close();
            DB.connection.close();
        }
        catch (java.sql.SQLException ex){
            Helpers.redToast(pContext,"GetData.getAllPallets(): SQL Exception caught!");
        }
        return data;
    }

    public static int deletePallets(Context pContext, ArrayList<WarehouseDB> pPallets) {

        int dr = 0;

      ArrayList<String> delPallNrs = (ArrayList<String>) pPallets.stream().map(pallet -> pallet.getPalleteNr()).collect(Collectors.toList());

        String query = Query.INSTANCE.deleteAllPallets2(delPallNrs);
        try {
            stmt = DB.getStatement();
            dr = stmt.executeUpdate(query);
            if(dr ==  1) dr = delPallNrs.size();
            stmt.close();
            DB.connection.close();
        }
        catch (java.sql.SQLException ex){
            Helpers.redToast(pContext,"GetData.deletePallets(): SQL Exception caught!");
        }
        return dr;
    }

    public static int deletePallet(Context pContext, String pPallet) {

        int dr = 0;
        String query = Query.INSTANCE.deletePallet2(pPallet);
        try {
            stmt = DB.getStatement();
            dr = stmt.executeUpdate(query);
            stmt.close();
            DB.connection.close();
        }
        catch (java.sql.SQLException ex){
            Helpers.redToast(pContext,"GetData.deletePallet(): SQL Exception caught!");
        }
        return dr;
    }


    ///<summary>
    ///returns object array of values for selected position of selected type
    ///</summary>
    public static ArrayList<WarehouseDB> getPosWarehouse(Context pContext, Integer pX, Integer pY)
    {
        ArrayList<WarehouseDB> data = new ArrayList<WarehouseDB>();
        String query = Query.INSTANCE.getPos(pX,pY);
        try {
            //stmt = DB.connection.createStatement(); //nemozno vytvorit Statement na conection, ktore uz ma Statement vytvoreny
            stmt = DB.getStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                WarehouseDB record = new WarehouseDB();
                record.ID = rs.getInt("id");
                record.setPartNr(rs.getString("partNr"));
                record.setPartNrFixed(rs.getString("partNrFixed"));
                record.setChannel(rs.getInt("channel"));
                record.setPalleteNr(rs.getString("paletteNr"));
                record.setPosX(rs.getInt("posX"));
                record.setPosY(rs.getInt("posY"));
                record.setPosZ(rs.getInt("posZ"));
                record.setPosX(rs.getInt("section"));
                record.setFIFODateTime(rs.getTimestamp("FIFODatetime"));
                record.setRem(rs.getBoolean("rem"));
                record.setPrevPaletteNr(rs.getString("prevPaletteNr"));
                data.add(record);
            }

        stmt.close();
        DB.connection.close();
        }
        catch (SQLException e) {
            Helpers.redToast(pContext,"GetData.getPosFIFO(): SQL Exception caught");
        }
        return data;
    }

    ///<summary>
    ///returns object array of values for selected position of selected type
    ///</summary>
    public static FIFO getPosFIFO(Context pContext, Integer pX, Integer pY)
    {
        FIFO data = new FIFO();
        String query = Query.INSTANCE.getPosFIFO(pX,pY);
        if (GetData.DB.CONN()){
        try {
            stmt = DB.getStatement(); //nemozno vytvorit Statement na conection, ktore uz ma Statement vytvoreny
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                FIFO record = new FIFO();
                record.setID(rs.getInt("id"));
                record.setPosX(rs.getInt("posX"));
                record.setPosY(rs.getInt("posY"));
                record.setPartNr(rs.getString("partNr"));
                record.setChannel(rs.getInt("channel"));
                record.setCount(rs.getInt("count"));
                record.setMaxCount(rs.getInt("maxcount"));
                record.setPack(rs.getInt("pack"));
                record.setaLTime(rs.getInt("aLTime"));
                record.setaLCheck(rs.getBoolean("aLCheck"));
                record.setaLBPTime(rs.getInt("aLBPTime"));
                record.setaLBPStart(rs.getTimestamp("aLBPStart"));
                record.setaLBP(rs.getBoolean("aLBP"));
                data = record;
            }
            stmt.close();
            DB.connection.close();
        }
        catch (SQLException e) {

            Helpers.redToast(pContext,"GetData.getPosFIFO(): SQL Exception caught!");
        }
        }
        else
        {
            Helpers.redToast(pContext, "GetData.getPosFIFO(): Connection to DB FAILED!");
        }
        return data ;
    }

    public static PalletPlus getPalletPlus(Context pContext, String pPalletNr)
    {
        PalletPlus data = new PalletPlus();
        String query = Query.INSTANCE.getPallet(pPalletNr);
        if (GetData.DB.CONN()){
            try {
                stmt = DB.getStatement(); //nemozno vytvorit Statement na conection, ktore uz ma Statement vytvoreny
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()){
                    PalletPlus paleta  = new PalletPlus();
                    paleta.X = rs.getInt("posX");
                    paleta.Y = rs.getInt("posY");
                    paleta.partNr = rs.getString("partNr");
                    paleta.Z = rs.getInt("row");
                    paleta.chnl = rs.getInt("channel");
                    paleta.pack = rs.getInt("pack");
                    data = paleta;
                }
                stmt.close();
                DB.connection.close();
            }
            catch (SQLException e) {
                Helpers.redToast(pContext,"GetData.getPalletPlus(): SQL Exception caught!");
            }
        }
        else
        {
            Helpers.redToast(pContext, "GetData.getPalletPlus(): Connection to DB FAILED!");
        }
        return data ;
    }

    //int pX, int pY, String pPartNumber, int i, int i1, int i2)
    public static boolean updatePosByPos(Context pContext, FIFO pFIFO) {
        boolean result = false;
        FIFO data = null;
        String query = Query.INSTANCE.updatePos(pFIFO);
        if (GetData.DB.CONN()){
            try {
                stmt = DB.getStatement(); //nemozno vytvorit Statement na conection, ktore uz ma Statement vytvoreny
                int intResult = stmt.executeUpdate(query);
                if ( intResult > 0) result =  true;
                stmt.close();
                DB.connection.close();
            }
            catch (SQLException e) {
                Helpers.redToast(pContext,"GetData.updatePosByPos(): SQL Exception caught!");
                result = false;
                            }
        }
        else
        {
            Helpers.redToast(pContext, "GetData.updatePosByPos(): Connection to DB FAILED!");
            result = false;
        }

        return result;

    }

    public static int updatePallets(Context pContext, ArrayList<WarehouseDB> pUpdatedPallets, Integer pX, int pY) {
        int dr = 0;

        ArrayList<String> updPallNrs = (ArrayList<String>) pUpdatedPallets.stream().map(pallet -> pallet.getPalleteNr()).collect(Collectors.toList());

        String query = Query.INSTANCE.updatePallets(updPallNrs,pX,pY);
        try {
            stmt = DB.getStatement();
            dr = stmt.executeUpdate(query);
            if (dr == 1) dr = updPallNrs.size();
            stmt.close();
            DB.connection.close();
        }
        catch (java.sql.SQLException ex){
            Helpers.redToast(pContext,"GetData.getAllPallets(): SQL Exception caught!");
        }
        return dr;
    }

    public static int updateCount(Context pContext, int pMaxX, int pMaxY) {
        int dr = 0;
        int updated_records = 0;
        for (int x=1;x<=pMaxX;x++)
            for (int y=1;y<=pMaxY;y++) {
                String query = Query.INSTANCE.updateCounts(x, y);
                try {
                    stmt = DB.getStatement();
                    dr = stmt.executeUpdate(query);
                    stmt.close();
                    updated_records++;
                } catch (java.sql.SQLException ex) {
                    Helpers.redToast(pContext, "GetData.getAllPallets(): SQL Exception caught!");
                }
            }
        try {
            DB.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return updated_records;
    }

}

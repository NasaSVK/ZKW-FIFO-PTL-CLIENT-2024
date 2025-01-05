package com.symbol.ptlclient2024.SQL;

import java.util.ArrayList;

public enum Query {

    INSTANCE;

    public String getAllParts(){
        //String query = "select * from ihis_vlast_db.zariadenie,ihis_vlast_db.majetok where majetok.ID=zariadenie.ID_SKUP AND zariadenie.VLASTNIK ='Dušan Nemček'";
        String query = "SELECT * FROM [dbo].[warehouseDB]";
        return query;
    }

    String ors(ArrayList<String> pPaletteNrs){
        String result  = "";
        for (String PN: pPaletteNrs) {
            result +="[paletteNr] = '" + PN + "' OR ";
        }
        if (result.compareTo("")!=0)
            result = result.substring(0,result.length()-4);

        return result;
    }
    public String deleteAllPallets(ArrayList<String> pPaletteNrs){
                String query = "DELETE FROM [dbo].[warehouseDB] where "+ors(pPaletteNrs);
        return query;
    }

    public String deleteAllPallets2(ArrayList<String> pPaletteNrs){
        String query = "declare @posX int, @posY int, @count int; set @posX = 0; set	@posY = 0; set @count = 0; "
                + "SELECT @posX = [posX], @posY = [posY] FROM [dbo].[warehouseDB] where "+ors(pPaletteNrs) + " DELETE from [dbo].[warehouseDB]"
                + " where "+ ors(pPaletteNrs) + " SELECT @count = COUNT(*) FROM [dbo].[warehouseDB] WHERE [posX] like @posX and [posY] like @posY"
                + " UPDATE [dbo].[layout] SET [count] = @count WHERE [posX] like @posX and [posY] like @posY";
        return query;
    }

    public String deletePallet(String pPaletteNrs){
        String query = "DELETE FROM [dbo].[warehouseDB] where paletteNr = '"+ pPaletteNrs+"'";
        return query;
    }

    public String deletePallet2(String pPaletteNr){
        String query = "declare @posX int, @posY int, @count int; set @posX = 0; set	@posY = 0; set @count = 0; "
                + "SELECT @posX = [posX], @posY = [posY] FROM [dbo].[warehouseDB] WHERE [paletteNr] = '" + pPaletteNr + "' DELETE from [dbo].[warehouseDB]"
                + " where [paletteNr] = N'" + pPaletteNr + "' SELECT @count = COUNT(*) FROM [dbo].[warehouseDB] WHERE [posX] like @posX and [posY] like @posY"
                + " UPDATE [dbo].[layout] SET [count] = @count WHERE [posX] like @posX and [posY] like @posY";
        return query;
    }

    ///<summary>
    ///returns object array of values for selected position of selected type
    ///</summary>
    public String getCount(Integer px, Integer py)
    {
        String query  = "SELECT COUNT(*) FROM dbo.warehouseDB WHERE posX = " + px + " AND posY = " + py;
        return query;
    }

    public String getPos(Integer pX, Integer pY){
        String query = "SELECT * FROM [dbo].[warehouseDB] WHERE [posX] = " + pX + " AND [posY] = " + pY + " order by [FIFODatetime],[id]";
        return query;
    }


    public String getPosFIFO(Integer pX, Integer pY) {
        String query = "SELECT * FROM [dbo].[layout] WHERE [posX] = " + pX + " AND [posY] = " + pY ;
        return query;
    }

    ///<summary>
    ///returns object array of values for searched pallet
    ///</summary>
    public String getPallet(String pPalletNr)
    {
//        String query = "DECLARE @pack int, @x int, @y int, @pallet nchar(20); SET @pallet = '" + pPalletNr + "';" +
//                "SELECT @x = [posX],@y = [posY] FROM [dbo].[warehouseDB] WHERE [paletteNr] = @pallet;";
////                +
//                "SELECT @pack = [pack] FROM [dbo].[layout] WHERE [posX] = @x and [posY] = @y;";
//                +
//                "WITH temp AS(" +
//                "SELECT ROW_NUMBER() OVER (ORDER BY [fifoDatetime],[id]) AS row , [id], [paletteNr],[posX],[posY],[partNr],[channel]" +
//                "FROM [dbo].[warehouseDB] WHERE [posX] = @x and [posY] = @y)" +
//                "SELECT [posX], [posY], row, [partNr], [channel], @pack AS pack FROM [temp] WHERE [paletteNr] = @pallet";

        //object[] o = readerQuery("SELECT * FROM [dbo].[warehouseDB] WHERE [paletteNr] = N'12345672'", 10);

//        query = "DECLARE @pack int, @x int, @y int, @pallet nchar(20); SET @pallet = '" + pPalletNr + "';" +
//                "SELECT [posX],@y = [posY],[partNr] FROM [dbo].[warehouseDB] WHERE [paletteNr] = @pallet;";

        String query = "DECLARE @pack int, @x int, @y int, @pallet nchar(20); SET @pallet = '" + pPalletNr + "';" +
                "SELECT @x = [posX],@y = [posY] FROM [dbo].[warehouseDB] WHERE [paletteNr] = @pallet;" +
                "SELECT @pack = [pack] FROM [dbo].[layout] WHERE [posX] = @x and [posY] = @y;" +
                "WITH temp AS(" +
                "SELECT ROW_NUMBER() OVER (ORDER BY [fifoDatetime],[id]) AS row , [id], [paletteNr],[posX],[posY],[partNr],[channel]" +
                "FROM [dbo].[warehouseDB] WHERE [posX] = @x and [posY] = @y)" +
                "SELECT [posX], [posY], row, [partNr], [channel], @pack AS pack FROM [temp] WHERE [paletteNr] = @pallet";
//

        return query;

    }

    public String updatePos(FIFO pNewFIFO) {

//        String query = "declare @pack int;\nset @pack = " + pNewFIFO.getPack() + ";\nUPDATE[dbo].[layout]\nSET[partNr] = N'" + pNewFIFO.getPartNr() + "'\n,[channel] = " + pNewFIFO.getChannel() + "\n,[pack] = @pack\n,[maxcount] = " + pNewFIFO.getMaxCount() + "\nWHERE[posX] = " + pNewFIFO.getPosX() + " AND [posY] = " + pNewFIFO.getPosY();
//        return query;

        String query = "UPDATE [dbo].[layout] SET [partNr] = N'" + pNewFIFO.getPartNr() + "', [channel] = " + pNewFIFO.getChannel() + ",[pack] = " + pNewFIFO.getPack() + ",[maxcount] = " + pNewFIFO.getMaxCount() + " WHERE [posX] = " + pNewFIFO.getPosX() + " AND [posY] = " + pNewFIFO.getPosY();
        return query;

    }

    public String updatePallets(ArrayList<String> updPallNrs, int pX, int pY ) {

        //String query = "UPDATE [dbo].[warehouseDB] SET [posX] = " + pX + ", [posY] = " + pY + " WHERE [paletteNr] = '"+ updPallNrs.get(0) + "';";
        String query = "UPDATE [dbo].[warehouseDB] SET [posX] = " + pX + ", [posY] = " + pY + " WHERE" + ors(updPallNrs);
        return query;

    }

    public String updateCounts(int pX, int pY ) {

       String  query = "declare @posX int, @posY int, @count int; set @posX = "+pX+"; set @posY = "+pY+"; set @count = 0;"
                + "SELECT @count = COUNT(*) FROM [dbo].[warehouseDB] WHERE [posX] like @posX and [posY] like @posY"
                + " UPDATE [dbo].[layout] SET [count] = @count WHERE [posX] like @posX and [posY] like @posY";
        return query;
    }


}

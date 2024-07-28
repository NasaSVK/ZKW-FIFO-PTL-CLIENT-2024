package com.symbol.kepzetclient.SQL;

public enum Query {

    INSTANCE;

    public String getAllParts(){
        //String query = "select * from ihis_vlast_db.zariadenie,ihis_vlast_db.majetok where majetok.ID=zariadenie.ID_SKUP AND zariadenie.VLASTNIK ='Dušan Nemček'";
        String query = "SELECT * FROM [dbo].[warehouseDB]";
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
        String query = "DECLARE @pack int, @x int, @y int, @pallet nchar(20); SET @pallet = N'" + pPalletNr + "';" +
                "SELECT @x = [posX],@y = [posY] FROM [dbo].[warehouseDB] WHERE [paletteNr] = @pallet;" +
                "SELECT @pack = [pack] FROM [dbo].[layout] WHERE [posX] = @x and [posY] = @y;" +
                "WITH temp AS(" +
                "SELECT ROW_NUMBER() OVER (ORDER BY [fifoDatetime],[id]) AS row , [id], [paletteNr],[posX],[posY],[partNr],[channel]" +
                "FROM [dbo].[warehouseDB] WHERE [posX] = @x and [posY] = @y)" +
                "SELECT [posX], [posY], row, [partNr], [channel], @pack AS pack FROM [temp] WHERE [paletteNr] = @pallet";

        //object[] o = readerQuery("SELECT * FROM [dbo].[warehouseDB] WHERE [paletteNr] = N'12345672'", 10);

        return query;

    }

    public String updatePos(FIFO pNewFIFO) {

//        String query = "declare @pack int;\nset @pack = " + pNewFIFO.getPack() + ";\nUPDATE[dbo].[layout]\nSET[partNr] = N'" + pNewFIFO.getPartNr() + "'\n,[channel] = " + pNewFIFO.getChannel() + "\n,[pack] = @pack\n,[maxcount] = " + pNewFIFO.getMaxCount() + "\nWHERE[posX] = " + pNewFIFO.getPosX() + " AND [posY] = " + pNewFIFO.getPosY();
//        return query;

        String query = "UPDATE [dbo].[layout] SET [partNr] = N'" + pNewFIFO.getPartNr() + "', [channel] = " + pNewFIFO.getChannel() + ",[pack] = " + pNewFIFO.getPack() + ",[maxcount] = " + pNewFIFO.getMaxCount() + " WHERE [posX] = " + pNewFIFO.getPosX() + " AND [posY] = " + pNewFIFO.getPosY();
        return query;

    }
}

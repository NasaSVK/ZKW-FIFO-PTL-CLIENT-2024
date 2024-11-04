package com.symbol.kepzetclient.SQL;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.symbol.kepzetclient.auxx.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


//#################################################################
//SQL Server database connectivity with android app | Android Studio | java |2024
//https://www.youtube.com/watch?v=XuMP0zFnq-A
//#################################################################
public class ConnectionToDB {

    private boolean result = false;
    String classes = "net.sourceforge.jtds.jdbc.Driver";
//    protected static String ip = "192.168.1.8";
//    protected static String port = "1433";
//    protected static String db = "zkwPBL19";
//    protected  static String user_id = "root";
//    protected  static String password = "root";

    protected static String ip = "10.16.0.141";
    protected static String port = "53563";
    protected static String db = "zkwPBL";
    protected  static String user_id = "sknasa";
    protected  static String password = "root";

    public String  ConnectionResult = "";
    final static public int TIME_OUT = 10;

    public Connection connection = null;

    private Statement stmt;

    public Statement getStatement() throws SQLException
    {
        if (stmt == null){
            return stmt = connection.createStatement();
        }
        else
            if (stmt.isClosed()){
                return stmt = connection.createStatement();
            }

            return stmt;
    }


    @SuppressLint("NewApi")
    public boolean CONN(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Connection conn = null;
        //ExecutorService executorService = Executors.newSingleThreadExecutor();
        //executorService.execute(() -> {

        db = Settings.getSELF().DbName;
        port = Integer.toString(Settings.getSELF().DbPort);
        user_id = Settings.getSELF().DbUser;
        ip = Settings.getSELF().ServerIP;

        try {

                if (connection != null && !connection.isClosed()) return true;

                Class.forName(classes);

                String conUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + db;
                //String conUrl = "jdbc:jtds:sqlserver://" + ip + ";databaseName=" + db;

                connection = DriverManager.getConnection(conUrl, user_id, password);

                this.stmt = connection.createStatement();

                ConnectionResult = "Connection with DB server successful";

                result  = true;
                //return true;

            } catch (SQLException e) {
                ConnectionResult = String.format("!!" + System.lineSeparator() + "Runtime EXCEPTION here: %s", e.getMessage() + System.lineSeparator() + "!!");
                Log.e("KEPZET EXCEPTION", "Runtime EXCEPTION: " + ConnectionResult);
                result = false;
                //Helpers.redToast(ConnectionClass.this , ConnectionResult);
            } catch (ClassNotFoundException e) {
                ConnectionResult = String.format("!!" + System.lineSeparator() + "ClassNotFound EXCEPTION: here: %s", e.getMessage() + System.lineSeparator() + "!!");
                Log.e("KEPZET EXCEPTION", "ClassNotFound EXCEPTION: " + ConnectionResult);
                result = false;
            } catch (Exception e) {
                ConnectionResult = String.format("!!" + System.lineSeparator() + "UNKNOWN ERROR here: %s", e.getMessage() + System.lineSeparator() + "!!");
                Log.e("KEPZET EXCEPTION", "EXCEPTION: " + ConnectionResult);
                result = false;
            }

            //return false;
        //});

        return result;
    }
}

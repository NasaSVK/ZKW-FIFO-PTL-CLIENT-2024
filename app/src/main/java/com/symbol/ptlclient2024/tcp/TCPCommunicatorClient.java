package com.symbol.ptlclient2024.tcp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.symbol.ptlclient2024.Helpers;
import com.symbol.ptlclient2024.MainActivity;
import com.symbol.ptlclient2024.auxx.Settings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TCPCommunicatorClient {
    private static TCPCommunicatorClient uniqInstance;
    private static String serverHost;
    private static int serverPort;
    private static List<TCPClientListener> allListeners;
    private static BufferedWriter out;
    //private static BufferedReader in;
    private static Socket s;
    private static Handler UIHandler = new Handler();
    private static Context appContext;

    private TCPCommunicatorClient()
    {

        allListeners = new ArrayList<TCPClientListener>();
    }
    public static TCPCommunicatorClient getInstance()
    {
        if(uniqInstance==null)
        {
            uniqInstance = new TCPCommunicatorClient();
        }
        return uniqInstance;
    }
    public  TCPWriterErrors init(Context pContext, String pIP, int port)
    {
        setServerHost(pIP);
        setServerPort(port);
        appContext = pContext;

        //InitTCPClientTask task = new InitTCPClientTask();
        //task.execute(new Void[0]);
        //return task.result;
        if (!Helpers.Ping(getServerHost()))
        {
            return TCPWriterErrors.HostUnreachable;
        }

        TCPWriterErrors result = null;
        try {

            s = new Socket(getServerHost(), getServerPort());

            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            for (TCPClientListener listener : allListeners)
                listener.onTCPConnectionStatusChanged(true);



            result = TCPWriterErrors.OK;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            result = TCPWriterErrors.UnknownHostException;

        } catch (IOException e) {
            e.printStackTrace();
            result = TCPWriterErrors.IOException;

        } catch (Exception e) {
            e.printStackTrace();
            result = TCPWriterErrors.otherProblem;

        }

        closeStreams();
        try{
            //s.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static  TCPWriterErrors writeToSocket(/*final JSONObject obj*/ String pContent,Handler handle,Context context)
    {
        UIHandler=handle;
        appContext=context;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try
                {
                    //Settings.getSELF().LoadToFile(context);
                    String IP = Settings.getSELF().ServerIP;
                    int port = Settings.getSELF().ServerPort;
                    if (!Helpers.Ping(Settings.getSELF().ServerIP)){
                        https://stackoverflow.com/questions/47536005/cant-toast-on-a-thread-that-has-not-called-looper-prepare
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("ERROR TCP", "SERVER: " + Settings.getSELF().ServerIP + " UNREACHABLE");
                                Helpers.redToast(context,"SERVER " + Settings.getSELF().ServerIP + " UNREACHABLE!");
                                MainActivity.getInstance().writeError("SERVER " + Settings.getSELF().ServerIP + " UNREACHABLE!");
                            }
                        });
                        return;
                    }
                    s = new Socket(IP, port);
                    out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    //String outMsg = obj.toString() + System.getProperty("line.separator");
                    out.write(pContent);
                    //out.write(pContent+ System.getProperty("line.separator"));
                    //out.write(pContent,0,pContent.length());
                    //out.write(pContent.toCharArray());
                    //out.newLine();
                    out.flush();

                    MainActivity.getInstance().setResponsed(false);

                    Log.i("INFO TCP", "sent: " + pContent /*outMsg*/);
                    MainActivity.getInstance().logStream.logData("INFO TCP: sent:" + pContent );
                    closeStreams();
                }
                catch(Exception e)
                {
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            Helpers.redToast(appContext ,"A REMOTE SERVICE on PORT TCP"+ Settings.getSELF().ServerPort +" is NOT ACCESSIBLE");
                            if(appContext instanceof MainActivity){
                                ((MainActivity)appContext).writeError("REMOTE PORT TCP"+  Settings.getSELF().ServerPort   +" NOT LISTENING");
                            }
                            Log.i("ERROR TCP","REMOTE PORT "+  Settings.getSELF().ServerPort +" NOT LISTENING");
                        }
                    });
                }
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();
        return TCPWriterErrors.OK;
    }

    public static void addListener(TCPClientListener listener)
    {
        allListeners.clear();
        allListeners.add(listener);
    }
    public static void removeAllListeners()
    {
        allListeners.clear();
    }
    public static void closeStreams()
    {
        try
        {
            //in.close();
            s.close();
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public static String getServerHost() {
        return serverHost;
    }
    public static void setServerHost(String serverHost) {
        TCPCommunicatorClient.serverHost = serverHost;
    }
    public static int getServerPort() {
        return serverPort;
    }
    public static void setServerPort(int serverPort) {
        TCPCommunicatorClient.serverPort = serverPort;
    }


    public class InitTCPClientTask extends AsyncTask<Void, Void, Void>
    {

        public InitTCPClientTask()
        { }
        TCPWriterErrors result;

        @Override
        protected Void doInBackground(Void... params) {

            // TODO Auto-generated method stub
                try {
                    s = new Socket(getServerHost(), getServerPort());
                    //in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    for (TCPClientListener listener : allListeners)
                        listener.onTCPConnectionStatusChanged(true);
        //                while(true)
        //                {
        //                    String inMsg = in.readLine();
        //                    if(inMsg!=null)
        //                    {
        //                        Log.i("TcpClient", "received: " + inMsg);
        //                        for(TCPClientListener listener:allListeners)
        //                            listener.onTCPMessageRecieved(inMsg);
        //                    }
        //                }
                    result = TCPWriterErrors.OK;

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    result = TCPWriterErrors.UnknownHostException;

                } catch (IOException e) {
                    e.printStackTrace();
                    result = TCPWriterErrors.IOException;

                }
                return null;
        }
    }
    public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK,HostUnreachable}
}


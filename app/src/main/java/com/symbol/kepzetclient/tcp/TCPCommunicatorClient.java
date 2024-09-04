package com.symbol.kepzetclient.tcp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.symbol.kepzetclient.Helpers;
import com.symbol.kepzetclient.MainActivity;
import com.symbol.kepzetclient.auxx.Settings;

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
    public  TCPWriterErrors init(String host,int port)
    {
        setServerHost(host);
        setServerPort(port);
        InitTCPClientTask task = new InitTCPClientTask();
        task.execute(new Void[0]);
        return task.result;
    }
    public static  TCPWriterErrors writeToSocket(/*final JSONObject obj*/ String pContent,Handler handle,Context context)
    {
        UIHandler=handle;
        appContext=context;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try
                {
                    Settings.getSELF().LoadToFile(context);
                    //Socket socket = new Socket("192.168.1.8",2222);
                    String IP = Settings.getSELF().ServerIP;
                    Socket socket = new Socket(Settings.getSELF().ServerIP, Settings.getSELF().ServerPort);
                    out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    //String outMsg = obj.toString() + System.getProperty("line.separator");
                    out.write(pContent /*outMsg*/);
                    out.flush();
                    Log.i("TcpClient", "sent: " + pContent /*outMsg*/);
                    out.close();
                }
                catch(Exception e)
                {
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Helpers.redToast(appContext ,"a problem has occured, the app might not be able to reach the server");

                            if(appContext instanceof MainActivity){
                                ((MainActivity)appContext).writeError("the app might not be able to reach the server");

                            }
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
            s.close();
            //in.close();
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
        {

        }
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

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    result = TCPWriterErrors.UnknownHostException;

                } catch (IOException e) {
                    e.printStackTrace();
                    result = TCPWriterErrors.IOException;

                }

                result = TCPWriterErrors.OK;

            return null;

        }

    }
    public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}
}


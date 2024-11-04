package com.symbol.kepzetclient.tcp;

import android.os.AsyncTask;
import android.os.Handler;

import com.symbol.kepzetclient.Helpers;
import com.symbol.kepzetclient.MainActivity;
import com.symbol.kepzetclient.auxx.Settings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPCommunicatorServer {

    public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}
    private static TCPCommunicatorServer uniqInstance;
    private static int serverPort;
    private static List<TCPServerListener> allListeners;
    private static ServerSocket ss;
    private static Socket s;
    private static BufferedReader in;
    private static BufferedWriter out;
    //private static OutputStream outputStream;
    private static Handler handler = new Handler();
    private static boolean serverRunning;


    private TCPCommunicatorServer()
    {
        allListeners = new ArrayList<TCPServerListener>();
    }
    public static TCPCommunicatorServer getInstance()
    {
        if(uniqInstance==null)
        {
            uniqInstance = new TCPCommunicatorServer();
        }
        return uniqInstance;
    }
    public  TCPWriterErrors init(int port)
    {
        setServerPort(port);
        InitTCPServerTask task = new InitTCPServerTask();
        task.execute(new Void[0]);
        return TCPWriterErrors.OK;
//		}
    }
    public static  TCPWriterErrors writeToSocket(JSONObject obj)
    {
        try
        {
            out.write(obj.toString() + System.getProperty("line.separator"));
            out.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return TCPWriterErrors.OK;

    }

    public static void addListener(TCPServerListener listener)
    {
        allListeners.add(listener);
    }



    public static int getServerPort() {
        return serverPort;
    }
    public static void setServerPort(int serverPort) {
        TCPCommunicatorServer.serverPort = serverPort;
    }


    public class InitTCPServerTask extends AsyncTask<Void, Void, Void>
    {

        public InitTCPServerTask()
        {
            serverRunning = true;
        }

        @Override
        protected Void doInBackground(Void... params) {

                try {
                    //Settings.getSELF().LoadToFile(MainActivity.getContext());
                    int localServerPort = Settings.getSELF().ClientPort;
                    ss = new ServerSocket(localServerPort);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Settings.getSELF().LoadToFile(MainActivity.getContext());
                            for (TCPServerListener listener : allListeners) {
                                final String  TEXT  = "LISTENER at " + Helpers.getLocalIP() + ":"+Settings.getSELF().ClientPort;
                                //menej miesta na vypis => kratsi TAG
                                listener.onInfoEventOccured("TCP", TEXT);
                                //viac miesta na vypis => dlhsi TAG
                                MainActivity.getInstance().logStream.logData("TCP LOCAL SERVER: " + TEXT);
                            }
                        }
                    });

                    while (serverRunning)
                    {
                    s = ss.accept();
                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    //outputStream = s.getOutputStream();
                    //out = new BufferedWriter(new OutputStreamWriter(outputStream));
                    //receive a message
                    String incomingMsg = null;
                    ArrayList<String> incomingMsgs = new ArrayList<String>();
                    incomingMsg = in.readLine();
                    while (incomingMsg != null) {
                        incomingMsgs.add(incomingMsg);
                        incomingMsg = in.readLine();
                    }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                for (TCPServerListener listener : allListeners)
                                    //kedze listenerom je (aj) MainActivity, da musi spracovat dorucene data
                                    listener.onTCPMessageServerRecieved(incomingMsgs);
                                //LOGUJEM TU, MOZEM SPECIFIKOVAT "TAG" TCP RECIEVED
                                //Log.e("TCP RECIEVED", String.join(", ", incomingMsgs));
                                //MainActivity.getInstance().logStream.logData("TCP RECIEVED: " + String.join(", ", incomingMsgs));
                            }
                        });

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            return null;
        }

    }



    public static void closeStreams() {
        // TODO Auto-generated method stub
        try
        {
            serverRunning = false;
            s.close();
            ss.close();
            out.close();
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

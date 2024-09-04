package com.symbol.kepzetclient.tcp;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

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
    private static TCPCommunicatorServer uniqInstance;
    private static int serverPort;
    private static List<TCPServerListener> allListeners;
    private static ServerSocket ss;
    private static Socket s;
    private static BufferedReader in;
    private static BufferedWriter out;
    //private static OutputStream outputStream;
    private static Handler handler = new Handler();


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
        private boolean serverRunning;
        public InitTCPServerTask()
        {
            serverRunning = true;
        }

        @Override
        protected Void doInBackground(Void... params) {


                try {
                    ss = new ServerSocket(TCPCommunicatorServer.getServerPort());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Settings.getSELF().LoadToFile(MainActivity.getContext());
                            for (TCPServerListener listener : allListeners) {
                                final String  TEXT  = "LISTENER at " + Helpers.getLocalIP() + ":"+Settings.getSELF().ClientPort;
                                listener.onInfoEventOccured(TEXT);
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
                    String incomingMsg;
                    ArrayList<String> incomingMsgs = new ArrayList<>();
                    while ((incomingMsg = in.readLine()) != null) {
                        final String finalMessage = incomingMsg;
                        incomingMsgs.add(incomingMsg);
                    }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                for (TCPServerListener listener : allListeners)
                                    listener.onTCPMessageServerRecieved(incomingMsgs);
                                Log.e("TCP", String.join(", ", incomingMsgs));
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

    public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}

    public static void closeStreams() {
        // TODO Auto-generated method stub
        try
        {
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

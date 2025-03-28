/*
 * Copyright (C) 2015-2019 Zebra Technologies Corporation and/or its affiliates
 * All rights reserved.
 */
package com.symbol.ptlclient2024;

import static com.symbol.ptlclient2024.Helpers.getCurrentDateTime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKManager.FEATURE_TYPE;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.ConnectionState;
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.Scanner.TriggerType;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.StatusData.ScannerStates;
import com.symbol.ptlclient2024.auxx.LogStream;
import com.symbol.ptlclient2024.auxx.Settings;
import com.symbol.ptlclient2024.custom_components.SetupActivity;
import com.symbol.ptlclient2024.tcp.TCPClientListener;
import com.symbol.ptlclient2024.tcp.TCPCommunicatorClient;
import com.symbol.ptlclient2024.tcp.TCPCommunicatorServer;
import com.symbol.ptlclient2024.tcp.TCPServerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MainActivity extends Activity
        implements EMDKListener, DataListener, StatusListener, ScannerConnectionListener, OnCheckedChangeListener,
        TCPClientListener, TCPServerListener {

    private static final int MY_BUTTON_INVALID_ID = -1;
    private static final int RESPONSE_INTERVAL = 2800;
    private Thread responseThread;
    private final int RESPONSE_START_APP_INTERVAL = 4999;
    private Thread responseStartAppThread;
    private static boolean SPUSTENIE = true;
    private final Semaphore dialogSemaphore = new Semaphore(0, true);
    private int pressedButtonID;

    private Button btnScan;

    private boolean _strart = true;

    ArrayList<String> dataRcvd = null;
    SoundPool soundPool = null;
    private int alarm1Id;
    private int alarm2Id;

    boolean stop = false;

    public Socket MyServerSocket;

    public com.symbol.ptlclient2024.auxx.LogStream logStream;

    private TCPCommunicatorClient tcpClient;
    private TCPCommunicatorServer tcpServer;

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    private TextView textViewData = null;
    private TextView textViewStatus = null;

    private ArrayList<View> Controls = null;

    //private CheckBox checkBoxEAN8 = null;
    //private CheckBox checkBoxEAN13 = null;
    //private CheckBox checkBoxCode39 = null;
    //private CheckBox checkBoxCode128 = null;

    //##################################################################################################
    //Spiner po novom TextView
    private TextView spinnerScannerDevices = null;
    //##################################################################################################

    private List<ScannerInfo> deviceList = null;

    private ScannerInfo My2DBarCodeImager = null;

    private int scannerIndex = 0; // Keep the selected scanner
    private final int defaultIndex = 0; // Keep the default scanner
    private int dataLength = 0;
    private String statusString = "";

    private TextView tvBarcode = null;
    private TextView tvPrevPallet = null;
    private TextView tvNOK = null;
    private TextView tvPartNumber = null;
    private TextView tvPosition = null;

    private TextView tvFirstPallet = null;

    private TextView tvInfo = null;

    private TextView tvTitle = null;

    private CheckBox cbClear = null;

    private boolean bSoftTriggerSelected = false;
    private boolean bDecoderSettingsChanged = true;
    private boolean bExtScannerDisconnected = false;
    private final Object lock = new Object();
    private String history = "";
    public com.symbol.emdk.barcode.ScannerInfo SI = null;

    private static MainActivity _mainActivity;
    private float _volume;
    private boolean _leaderMode;
    private String scannedBc;

    public static MainActivity getInstance(){

        return _mainActivity;
    }

    TabLayout tabLayout;
    private Connection con;

    private ProgressDialog dialog;
    private boolean isFirstLoad = true;
    private Handler UIHandler = new Handler();
    private Handler HandlerServer = new Handler();

    private List<multipackData> mpData = new ArrayList<multipackData>();;
    private CheckBox cbLeader;

    private Button btnAccept = null;
    private Button btnDbAccess = null;

    private TextView tvLastError;
    private int OkNokThreadsCount;
    private int OkNokTime;




    public static Context getContext(){

        return MainActivity._mainActivity;
    }

    //=======================================================================================================================
    //  onData
    //=======================================================================================================================
    @Override
    public void onData(ScanDataCollection scanDataCollection) {

        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList <ScanData> scanData = scanDataCollection.getScanData();

            //==========================================================================================
            //znefunkcnenia skenera a buttonv po dobu 2.5 sekundy, resp. kym nepride respone
            //onPause();
            //==========================================================================================

            for(ScanData data : scanData) {
                //updateData("<font color='silver'>" +  Helpers.getCurrentDateTime() + "</font>" +"  "+ "<font color='gray'>" +  data.getLabelType() + "</font> : <b>" + data.getData() +"</b>");
                String Type = data.getLabelType().toString();
                String Data = data.getData();
                updateData(Type, Data);
                //====================================
                barcodeRead(Data);
                //====================================

                //TCP START
                //String content  = scanData.get(scanData.size()-1).getData();
                //ConnectToServer();
                //TCPCommunicatorClient.writeToSocket(content,UIHandler,this);
                //TCP END

                logStream.logData("QR-CODE READ: " + Data);
            }
        }
    }



    //======================================================================================================================================================
    //CLIENT LISTENER
    //======================================================================================================================================================
    //https://www.facebook.com/share/r/cF3JcNMUMhKzrCLQ/
    //https://www.facebook.com/share/v/fsyALHALtUWCjHpQ/
    //vypise vsetky spravy zaslane serverom ako rekaciu na nejaku udalost
    @Override
    public void onTCPMessageServerRecieved(ArrayList<String> pIncMessages) {

        // TODO Auto-generated method stub
        dataRcvd = pIncMessages;
        //https://stackoverflow.com/questions/15136199/when-to-use-handler-post-when-to-new-thread
        HandlerServer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try
                {
                    //===================================================
                    //nastavim priznak existence reakcie zo serveru
                    setResponsed(true);
                    //na to co prislo zo serveru aplikujme parseData()
                    MainActivity._mainActivity.parseData();


                    //a vypisem do tvInfo
                    //LEN NA DIAGNOSTICKE UCELY; v produkcnej verzi sa na miesto celej kolekcie doslych dat vypisuje LEN previous pallet
                    //tvInfo.setText(String.join(", ", dataRcvd));
                    //===================================================
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    MainActivity._mainActivity.logStream.logData("TCP RECIEVED ERROR: " + String.join(", ", e.getMessage()));
                }
            }
        });

        //naco zatazovat hlavne vlanko zapisom do LOGOV =>
        //MainActivity._mainActivity.logStream.logData("TCP RECIEVED: " + String.join(", ", dataRcvd));
        Log.e("TCP RECIEVED", String.join(", ", pIncMessages));
        MainActivity.getInstance().logStream.logData("TCP RECIEVED: " + String.join(", ", pIncMessages));
    }


    //======================================================================================================================================================
    //ParseData START
    //======================================================================================================================================================
    private void parseData()
    {
        String pos = null;
        String pn = null;
        String firstPal = null;
        String result = null;
        String multipack = null;
        String info = null;
        String prevPal = null;

        try
        {
            //String[] arrayTempLines = dataRcvd.split("\\\n");
            //List<String> lines = Arrays.asList(arrayTempLines);
            pn = dataRcvd.get(0).trim();// 1013.0000.000.00
            pos = dataRcvd.get(1).trim();// x5 Y41 S2
            firstPal = dataRcvd.get(2).trim();// 1234567
            result = dataRcvd.get(3).trim();// "OK"/"NOK"/""
            multipack = dataRcvd.get(4).trim();//G1234|B2345|
            info = dataRcvd.get(5).trim();

            int indexPP = info.indexOf("previous pallet: ");
            int lengthPP = "previous pallet: ".length();
            if (indexPP >=0)
            {
                prevPal = info.substring(indexPP + lengthPP);
            }

        }
        catch (Exception ex)
        {
            Helpers.redToast(this, "received data (" + dataRcvd + ") parse error: " + ex.toString());
            logStream.logData("RECIEVED DATA (" + dataRcvd + ") PARSE ERROR: " + ex.toString());
        }

//        if ((info.compareTo("EMPTY STRING RECIEVED")==0 || info.compareTo("") == 0) && SPUSTENIE)
//        {
//            tvNOK.setText("OK");
//            tvNOK.setTextColor(getResources().getColor(R.color.text));
//            tvNOK.setBackgroundColor(Color.GREEN);
//            PanelOkNokOff();
//        }
        //else
        if ((result.compareTo("OK") == 0)||
                ((result.compareTo("NOK") == 0)&&((info.compareTo("EMPTY STRING RECIEVED")==0)||(info.compareTo("")==0))))
        {
            //ak mi prisiel PRVY PRAZDNY STRING PEZPROSTREDNE PO SPUSTENI APLIKACIE, ZRUSIM ODPOCITAVANIE DO ZOBRAZENIA HLASKY O NENADVIAZANI SPOJENIA SO SERVEROM
            if (SPUSTENIE){
                setStartAppResponsed(true);
                info = "SCANNER CONNECTED TO SERVER";
            }

            tvNOK.setText("OK");
            tvNOK.setTextColor(getResources().getColor(R.color.text));
            tvNOK.setBackgroundColor(Color.GREEN);
            PanelOkNokOff();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    playOK();
                }
            }).start();

            //if (tvPosition.getText().toString().compareTo("") == 0||(scannedBc.compareTo("TLeader1")!=0))
            //AK BOL NACITANY LEADER, POLIA SA NEUPRAVUJU, OSTAVAJU ZACHOVANE, MENIT SA BUDE LEN PRISLUSNY CHECKBOX
            if (scannedBc.compareTo("TLeader1")!=0) {
                tvBarcode.setText("");
                tvPartNumber.setText("");
                tvPosition.setText("");
                tvPrevPallet.setText("");
                tvInfo.setText("");
                tvFirstPallet.setText("");
            }
        }
        else if (result.compareTo("NOK") == 0)
        {
            tvNOK.setText("NOK");
            tvNOK.setTextColor(getResources().getColor(R.color.text,null));
            tvNOK.setBackgroundColor(Color.RED);
            PanelOkNokOff();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    playNOK();
                }
            }).start();
        }
        else if (result.compareTo("STOP") == 0) {
            tvNOK.setText("STOP");
            tvNOK.setBackgroundColor(getResources().getColor(R.color.light_orange));
            PanelOkNokOff();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    playNOK();
                }
            }).start();
            //return;
        }
        else
        {
            tvNOK.setText("");
            tvNOK.setBackgroundColor(Color.TRANSPARENT);
        }

        if (info.compareTo("teamLeader logged in")==0) cbLeader.setChecked(true);
        else if (info.compareTo("clear mode set")==0)  cbClear.setChecked(true);
        else if (info.compareTo("clear mode reset")==0) cbClear.setChecked(false);
        else if (info.endsWith("output sets reset") ||
                info.endsWith("input sets reset") ||
                info.endsWith("clear mode reset, teamLeader logged out") ||
                info.compareTo("teamLeader logged out")==0) {
            cbLeader.setChecked(false);
            cbClear.setChecked(false);
        }


        // ak som naskenoval leadra pocas otvorenej pozicie, nechcem, aby mi prislusne texboxy premazalo
        //if (tvPosition.getText().toString().compareTo("") == 0||(scannedBc.compareTo("TLeader1")!=0))
        //AK SOM NASKENOVAL LIDRA, UPRAVIM LEN MESAGE
        if (scannedBc.compareTo("TLeader1")!=0)
        {
            tvPartNumber.setText(pn);
            tvPosition.setText(pos);
            tvFirstPallet.setText(firstPal);
            if (prevPal != null)
                tvPrevPallet.setText(prevPal);
        }
        //INDO UPRAVIM AJ V PRIPADE PRIHLASENIA/ODSHLASENIA LIDRA
        tvInfo.setText(info);

        //NA ZAVER SAMOZREJME OBSLUHA ACCEPT BUTTONU
        this.btnAcceptHanlde();

        //if (SPUSTENIE) this.setStartAppResponsed(true);


        //BUTTON ACCEPT BUDE VIDITELNY LEN V LEADER MODE a ak je povoleny v NASTAVENIACH
//        //V OPACNOM PRIPADE JE NEVIDITELNY
////        if (cbLeader.isChecked() && Settings.getSELF().ManualAccepting){
////            btnAccept.setVisibility(View.VISIBLE);
////            if (pos.trim().compareTo("") != 0)
////                btnAccept.setEnabled(true);
////            else
////                btnAccept.setEnabled(false);
////        }
////        else
////            btnAccept.setVisibility(View.INVISIBLE);

    }
    //========================================================================================================================================================
    //========================================================================================================================================================
//    public boolean getResponsed() {
//
//        return isResponsed;
//    }
    //pri odoslani poziadavky na server => pResponse == false
    //po prijati poziadavky zo servera => pResponse == true
    public void setResponsed(boolean pResponsed) {

        //ak dosla odpoved na danu poziadavku zo servera VCAS (2800 ms)
        if (pResponsed == true){
            if(responseThread != null && (!responseThread.isInterrupted() || responseThread.isAlive()))
            {
                responseThread.interrupt();
                //responseThread.destroy();
            }

            //onResume2();
            UIHandler.post(()->{
                btnAcceptHanlde();
                //onResume2();
                //scanner.disable();
            });
        }
        else
        if (pResponsed == false){
            //UIHandler.post(()->onPause());
            //onPause2();
            UIHandler.post(()->{
                //onPause2();
                btnAcceptHanlde();});


            responseThread =
            Helpers.setTimeout(()->{
                //ak medzicasom neprisla reakcia zo serveru hlasim chybu
                    UIHandler.post(()->{
                            Helpers.redToast(_mainActivity ,"response from server not recieved");
                            _mainActivity.writeError("RESPONSE FROM SERVER NOT RECIEVED");
                            btnAcceptHanlde();
                    });
                    logStream.logData("ERROR TCP: response from server not recieved");
                    Log.i("ERROR TCP", "response from server not recievedr");
                    //UIHandler.post(()->onResume2());
                    //onResume2();
                //}

            },RESPONSE_INTERVAL);
            //Ak dojde odpoved do intervalu RESPONSE_INTERVAL, potom sa vlakno pochopitelne ukonci (hread.interrupt()) a chybova hlaska nezobrazi T
        }
    }

    void btnAcceptHanlde(){

        if (!Settings.getSTATIC().ManualAccepting){
            btnAccept.setEnabled(false);
            btnAccept.setVisibility(View.INVISIBLE);
            return;
        }

        if (cbLeader.isChecked()){
            btnAccept.setEnabled(false);
            btnDbAccess.setEnabled(true);

            if(tvPosition.getText().toString().compareTo("")!=0) {
                btnAccept.setEnabled(true);
                //btnDbAccess.setEnabled(false);
            }

            btnAccept.setVisibility(View.VISIBLE);
            btnDbAccess.setVisibility(View.VISIBLE);
        }
        else
        {
            btnAccept.setEnabled(false);
            btnAccept.setVisibility(View.INVISIBLE);

            btnDbAccess.setEnabled(false);
            btnDbAccess.setVisibility(View.INVISIBLE);
        }
    }


    public void setStartAppResponsed(boolean pResponsed) {

        //ak dosla odpoved vcas
        if (pResponsed == true){
            if(responseStartAppThread != null && responseStartAppThread.isAlive())
                responseStartAppThread.interrupt();
            tvInfo.setText("SCANNER CONNECTED TO SERVER");
            SPUSTENIE = false;
        }
        else
        if (pResponsed == false){
                responseStartAppThread =
                        Helpers.setTimeout(()->{
                            SPUSTENIE = false;
                            UIHandler.post(()->{
                                    Helpers.redToast(_mainActivity ,"connection to server FAILED");
                                    _mainActivity.writeError("connection to server FAILED");});
                                    logStream.logData("NETWOROK ERROR: connection to server FAILED");
                            Log.i("NETWORK ERROR", "connection to server FAILED");
                        },RESPONSE_START_APP_INTERVAL);
                //odstartujem ODPOCITAVANIE DO ZOBRAZENIA ERROR HLASKY
                //responseStartAppThread.start();
        }
    }


    //private boolean isResponsed  = false;

//    public void setVolume(float pVolume){
//        _volume = pVolume;
//    }

    class multipackData
    {
        public multipackData(String n, Color pColor)
        {
            this.nr = n;
            this.color = pColor;
        }
        public String nr;
        public Color color;

    }

    private void ConnectToServer() {

        //setupDialog();

        tcpClient = TCPCommunicatorClient.getInstance();
        //zabezpeci, ze pripadny pozitivny vysledok spojenia so serverom sa zobrazi na hlavnej aktivite
        TCPCommunicatorClient.addListener(this);
        //nacitanie suboru s aktualnymi nastaveniami
        //Settings.getSELF().LoadToFile(MainActivity.getContext());
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TCPCommunicatorClient.TCPWriterErrors result = tcpClient.init(
                        MainActivity.getContext(),
                        Settings.getSELF().ServerIP,
                        Settings.getSELF().ServerPort
                );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (result == TCPCommunicatorClient.TCPWriterErrors.IOException){
                            Helpers.redToast(getContext(), "Can't connect to the server due to 'IOException'");
                            tvLastError.setText("Can't connect to the server due to 'IOException'");
                        }
                        else
                        if (result == TCPCommunicatorClient.TCPWriterErrors.UnknownHostException){
                            Helpers.redToast(getContext(), "Can't connect to the server due to 'UnknownHostException'");
                            tvLastError.setText("Can't connect to the server due to 'UnknownHostException'");
                        }
                        else
                        if (result == TCPCommunicatorClient.TCPWriterErrors.otherProblem){
                            Helpers.redToast(getContext(), "Can't connect to the server due to 'otherProblem'");
                            tvLastError.setText("Can't connect to the server due to 'otherProblem'");
                        }
                        else
                        if (result == null){
                            Helpers.redToast(getContext(), "Can't connect to the server due to 'NULL'");
                            tvLastError.setText("Can't connect to the server due to 'NULL'");
                        }
                        else
                        if (result == TCPCommunicatorClient.TCPWriterErrors.HostUnreachable){
                            Helpers.redToast(getContext(), "SERVER " + Settings.getSELF().ServerIP + " UNREACHABLE!");
                            tvLastError.setText("SERVER " + Settings.getSELF().ServerIP + " UNREACHABLE!");
                        }

                        //if(responseStartAppThread != null && responseStartAppThread.isAlive())
                        //    responseStartAppThread.interrupt();


                    }
                });
            }
        });
        connectionThread.start();
    }

    public void TimeOutCallBack()
    {
        if (this.OkNokThreadsCount <= 1) {
            hideInstantly();
            }
            this.OkNokThreadsCount--;
    }

    public void hideInstantly(){
        tvNOK.setText("");
        tvNOK.setBackgroundColor(Color.TRANSPARENT);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _mainActivity = this;

        //vytvorenie instancie pre pristup k logom
        logStream = new LogStream(_mainActivity);

        //PO SPUSTENI APLKACIE NALOADUJEM INSTANCIU S NASTAVENIAMI
        Settings.getSELF().LoadFromFile(MainActivity.getContext());

        //BEGIN SOUND
            AudioAttributes audioAttributes = new AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool
                    .Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();


        // This load function takes three parameter context, file_name and priority.
        alarm1Id = soundPool.load(this, R.raw.alarm, 1);
        alarm2Id = soundPool.load(this, R.raw.alarm2, 1);
        //END SOUND

        //volume =  Settings.getSELF().Volume / 100;

        //https://stackoverflow.com/questions/17069955/play-sound-using-soundpool-example
//        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
//        alarmId = soundPool.load(this.getContext(), R.raw.alarm, 1);
//        alarm2Id = soundPool.load(this.getContext(), R.raw.alarm2, 1);
//        soundPool.play(alarmId, 100, 100, 0, 0, 1);

        //#########################################################################################
        //BEGIN RemoteServer
        this.ConnectToServer();
        //END RemoteServer
        //#########################################################################################


        //#########################################################################################
        //BEGIN LocalServer
        this.tcpServer = TCPCommunicatorServer.getInstance();
        TCPCommunicatorServer.addListener(this);
        //NIE JE NUTNE LOADOVAT SI KAZDE NASTAVENIE DO OSOBITNEHO ATRIBUTU; tam kde treba staci zavolat: Setting.getSTATIC().???
        int clientPort = Settings.getSELF().ClientPort;
        tcpServer.init(clientPort); //lokalny server na klientovi bude pocuvat na spravy HLAVNEHO SERVERU (192.168.1.8) na ulozeom porte (2004, 2005, 2006, 2007, 3333)
        //END LocalServer
        //#########################################################################################

        //SUND_TIME = Settings.getSELF().Time;

        deviceList = new ArrayList<ScannerInfo>();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setDefaultOrientation();

        textViewData = findViewById(R.id.textViewData);
        tvBarcode = findViewById(R.id.tvBarcode);
        textViewStatus = findViewById(R.id.textViewStatus);

        btnDbAccess = findViewById(R.id.btnDbAccesss);
        btnAccept = findViewById(R.id.btnAccept);

        tvNOK = findViewById(R.id.tvNOK);
        tvPartNumber = (TextView)findViewById(R.id.tvPartNunber);
        tvPosition = (TextView)findViewById(R.id.tvPosition);
        tvPrevPallet = (TextView)findViewById(R.id.tvPrevPallet);
        tvFirstPallet = (TextView)findViewById(R.id.tvFirstPallet);
        tvInfo = (TextView)findViewById(R.id.tvInfo);
        cbLeader = (CheckBox)findViewById(R.id.cbLeader);
        cbClear = (CheckBox)findViewById(R.id.cbxClear);
        tvLastError = (TextView)findViewById(R.id.tvLastError);

        btnScan = (Button) findViewById(R.id.btnScan);

        tvTitle = (TextView)findViewById(R.id.tvTitle);

        spinnerScannerDevices = findViewById(R.id.spinnerScannerDevices);

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            updateStatus("EMDKManager object request failed!");
            return;
        }

        //tvNOK.setVisibility(View.INVISIBLE);
        this.hideInstantly();

        //nutne volat ZA az po vytvoreni deviceList-u (enumerateScannerSevice)), inak sa scener neinicializuje
        //deviceList sa vytvara v enumerateScannerDevices();
        addSpinnerScannerDevicesListener();

        textViewData.setSelected(true);
        textViewData.setMovementMethod(new ScrollingMovementMethod());

        tvFirstPallet.setText("");
        tvBarcode.setText("");
        tvPartNumber.setText("");
        tvPosition.setText("");
        tvPrevPallet.setText("");

        //tvTitle.setText("Warehouse PBL® Client 2024 v."+ Helpers.getAppTimeStamp2(_mainActivity));
        tvTitle.setText(getResources().getString(R.string.appLabel) + Helpers.getAppTimeStampVer(_mainActivity));

        btnScan.setEnabled(false);
        btnScan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bSoftTriggerSelected = true;
                        cancelRead();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        break;
                }
                return false;
            }
        });

        if (SPUSTENIE) this.setStartAppResponsed(false);
    }

    void playNOK() {

        float volume = Settings.getSELF().Volume/100f;

        soundPool.play(alarm1Id, volume, volume , 1, 0, 1.0f);
    }
    void playOK() {
        float volume = Settings.getSELF().Volume/100f;

        soundPool.play(alarm2Id, volume, volume, 1, 0, 1.0f);
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        updateStatus("EMDK open success!");
        this.emdkManager = emdkManager;
        // Acquire the barcode manager resources
        initBarcodeManager();
        // Enumerate scanner devices
        enumerateScannerDevices();
        addSpinnerScannerDevicesListener();
        // Set default scanner
        spinnerScannerDevices.setText(My2DBarCodeImager.getFriendlyName());
        //spinnerScannerDevices.setSelection(defaultIndex);
        Toast.makeText(this,"onOpened()",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The application is in foreground
        if (emdkManager != null) {
            // Acquire the barcode manager resources
            initBarcodeManager();
            // Enumerate scanner devices
            enumerateScannerDevices();
            // Set selected scanner
            spinnerScannerDevices.setText(My2DBarCodeImager.getFriendlyName());
            //spinnerScannerDevices.setSelection(scannerIndex);
            // Initialize scanner
            initScanner();
        }

        //TCP START
//        if(!isFirstLoad)
//        {
//            TCPCommunicatorClient.closeStreams();
//            //vytovri hlavne instanciu TCPClient-sa
//            ConnectToServer();
//        }
//        else
//            isFirstLoad=false;
        //TCP END

        //Toast.makeText(this,"onResume()",Toast.LENGTH_SHORT);
    }
    protected void onResume2() {

            //scanner.enable();
            //spinnerScannerDevices.setText(My2DBarCodeImager.getFriendlyName());
//            //spinnerScannerDevices.setSelection(scannerIndex);
//            // Initialize scanner
             //initScanner();

        //if (emdkManager != null) {
            // Acquire the barcode manager resources
            //initBarcodeManager();
            // Enumerate scanner devices
            enumerateScannerDevices();
            // Set selected scanner
            spinnerScannerDevices.setText(My2DBarCodeImager.getFriendlyName());
            //spinnerScannerDevices.setSelection(scannerIndex);
            // Initialize scanner
            initScanner();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The application is in background
        // Release the barcode manager resources
        deInitScanner();
        deInitBarcodeManager();
        //Toast.makeText(this,"onPause()",Toast.LENGTH_SHORT);

    }
    protected void onPause2() {
        //deInitScanner();
        //deInitBarcodeManager();
        try{
            scanner.release();
        } catch (Exception e) {
            updateStatus(e.getMessage());
        }
        //scanner = null;
    }



    @Override
    public void onClosed() {
        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        updateStatus("EMDK closed unexpectedly! Please close and restart the application.");

        Toast.makeText(this,"onClose()",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        soundPool.release();

        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        //this.btnStopServer(null);
        Toast.makeText(this,"onDestroy()",Toast.LENGTH_SHORT);
    }

    //########################################################################################################
    //vola sa automaticky pri zmene statusu skenera
    //########################################################################################################
    @Override
    public void onStatus(StatusData statusData) {
        ScannerStates state = statusData.getState();
        switch(state) {
            case IDLE:
                statusString = statusData.getFriendlyName()+" is enabled and idle...";
                updateStatus(statusString);
                // set trigger type
                if(bSoftTriggerSelected) {
                    scanner.triggerType = TriggerType.SOFT_ONCE;
                    bSoftTriggerSelected = false;
                } else {
                    scanner.triggerType = TriggerType.HARD;
                }
                // set decoders
                //kontrola ci sa po zmene statusu zmenilo aj nastavenie scenera
                if(bDecoderSettingsChanged) {
                    //offDecoders();
                    setDecoders();
                    bDecoderSettingsChanged = false;
                }

                // submit read
                if(!scanner.isReadPending() && !bExtScannerDisconnected) {
                    try {
                        scanner.read();
                    } catch (ScannerException e) {
                        updateStatus(e.getMessage());
                    }
                }
                break;
            case WAITING:
                statusString = "Scanner is waiting for trigger press...";
                updateStatus(statusString);
                break;
            case SCANNING:
                statusString = "Scanning...";
                updateStatus(statusString);
                break;
            case DISABLED:
                statusString = statusData.getFriendlyName()+" is disabled.";
                updateStatus(statusString);
                break;
            case ERROR:
                statusString = "An error has occurred.";
                updateStatus(statusString);
                break;
            default:
                break;
        }
    }
    //########################################################################################################
    //########################################################################################################

    @Override
    public void onConnectionChange(ScannerInfo scannerInfo, ConnectionState connectionState) {
        String status;
        String scannerName = "";
        String statusExtScanner = connectionState.toString();
        String scannerNameExtScanner = scannerInfo.getFriendlyName();
        if (deviceList.size() != 0) {
            scannerName = deviceList.get(scannerIndex).getFriendlyName();
        }
        if (scannerName.equalsIgnoreCase(scannerNameExtScanner)) {
            switch(connectionState) {
                case CONNECTED:
                    bSoftTriggerSelected = false;
                    synchronized (lock) {
                        initScanner();
                        bExtScannerDisconnected = false;
                    }
                    break;
                case DISCONNECTED:
                    bExtScannerDisconnected = true;
                    synchronized (lock) {
                        deInitScanner();
                    }
                    break;
            }
            status = scannerNameExtScanner + ":" + statusExtScanner;
            updateStatus(status);
        }
        else {
            bExtScannerDisconnected = false;
            status =  statusString + " " + scannerNameExtScanner + ":" + statusExtScanner;
            updateStatus(status);
        }
    }
    //################################################################
    //KLUCVA METODA PROJEKTU => INICIALIZACIA SKENERA PRI SPUSTENI, AKO AJ PRI VRATENI SA NA AKTIVITU
    //################################################################
    private void initScanner() {
        if (scanner == null) {
            //#####################################################################################
            if ((deviceList != null) && (deviceList.size() != 0)) {
                if (barcodeManager != null){
                    //inicializacia klucovej premmennej aplikacie
                    //barcodeManager.getDevice - prijima scannerInfo object
                    //barcodeManager.getDevice - vracia Scanner objekt
                    //#############################################################################

                    scanner = barcodeManager.getDevice(deviceList.get(scannerIndex));
                    //this.offDecoders();
                }
            }
            //#####################################################################################
            else {
                updateStatus("Failed to get the specified scanner device! Please close and restart the application.");
                return;
            }
            if (scanner != null) {
                scanner.addDataListener(this);
                scanner.addStatusListener(this);
                try {
                    scanner.enable();
                } catch (ScannerException e) {
                    updateStatus(e.getMessage());
                    deInitScanner();
                }
            }else{
                updateStatus("Failed to initialize the scanner device.");
            }
        }
    }
    //#####################################################################################################################
    //#####################################################################################################################

    private void deInitScanner() {
        if (scanner != null) {
            try{
                scanner.disable();
            } catch (Exception e) {
                updateStatus(e.getMessage());
            }

            try {
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);
            } catch (Exception e) {
                updateStatus(e.getMessage());
            }

            try{
                scanner.release();
            } catch (Exception e) {
                updateStatus(e.getMessage());
            }
            scanner = null;
        }
    }

    private void initBarcodeManager(){
        //nacitanie vsetky podporovanych TYPOV citaciek
        //################################################################################
        barcodeManager = (BarcodeManager) emdkManager.getInstance(FEATURE_TYPE.BARCODE);
        //################################################################################
        // Add connection listener
        if (barcodeManager != null) {
            barcodeManager.addConnectionListener(this);
        }
    }

    private void deInitBarcodeManager(){
        if (emdkManager != null) {
            emdkManager.release(FEATURE_TYPE.BARCODE);
        }
    }

    //listener na zmenu typu scenera zo spineru
    private void addSpinnerScannerDevicesListener() {
        if (scanner==null) {
            //########################################################################################
            //########################################################################################
            scannerIndex = 1;
            //########################################################################################
            //########################################################################################
            bSoftTriggerSelected = false;
            bExtScannerDisconnected = false;
            deInitScanner();
            initScanner();
        }
        /*
        spinnerScannerDevices.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {
                if ((scannerIndex != position) || (scanner==null)) {
                    //########################################################################################
                    //########################################################################################
                    scannerIndex = position+1;
                    //########################################################################################
                    //########################################################################################
                    bSoftTriggerSelected = false;
                    bExtScannerDisconnected = false;
                    deInitScanner();
                    initScanner();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });*/
    }

    private void enumerateScannerDevices() {
        if (barcodeManager != null) {
            //friendlyNameList - zoznam ktory sa zobrazi v Spineri
            List<String> friendlyNameList = new ArrayList<String>();
            int spinnerIndex = 0;
            //######################################################################################
            //naplnenie zoznamu podporovanych typov scannerov
            //mne staci "2D Barcode Imager" pod indexom 1
            deviceList = barcodeManager.getSupportedDevicesInfo();
            My2DBarCodeImager =  deviceList.get(1);

            //######################################################################################
            if ((deviceList != null) && (deviceList.size() != 0)) {
                //Iterator<ScannerInfo> it = deviceList.iterator();
                //while(it.hasNext()) {
                //ScannerInfo scnInfo = it.next();
                //friendlyNameList.add(scnInfo.getFriendlyName());
                friendlyNameList.add(My2DBarCodeImager.getFriendlyName());
                //if(scnInfo.isDefaultScanner()) {
                //    defaultIndex = spinnerIndex;
                // }
                // ++spinnerIndex;
                //}
            }
            else {
                updateStatus("Failed to get the list of supported scanner devices! Please close and restart the application.");
            }
            //adapter na vypis zoznamu od spinera
            //ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, friendlyNameList);
            //spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //naplnenie spiera zoznamom moznych typov scanerov
            //spinnerScannerDevices.setAdapter(spinnerAdapter);
        }
    }

    private void setDecoders() {
        if (scanner != null) {
            try {
                ScannerConfig config = scanner.getConfig();
                // Set EAN8
                //config.decoderParams.ean8.enabled = checkBoxEAN8.isChecked();
                config.decoderParams.ean8.enabled = false;
                // Set EAN13
                //config.decoderParams.ean13.enabled = checkBoxEAN13.isChecked();
                config.decoderParams.ean13.enabled = false;
                // Set Code39
                //config.decoderParams.code39.enabled= checkBoxCode39.isChecked();
                config.decoderParams.code39.enabled= false;
                //Set Code128
                //config.decoderParams.code128.enabled = checkBoxCode128.isChecked();
                config.decoderParams.code128.enabled = false;
                scanner.setConfig(config);
            } catch (ScannerException e) {
                updateStatus(e.getMessage());
            }
        }
    }

    private void offDecoders() {
        if (scanner != null) {
            try {
                ScannerConfig config = scanner.getConfig();
                // Set EAN8
                config.decoderParams.ean8.enabled = false;
                // Set EAN13
                config.decoderParams.ean13.enabled = false;
                // Set Code39
                config.decoderParams.code39.enabled= false;
                //Set Code128
                config.decoderParams.code128.enabled = false;
                scanner.setConfig(config);
            } catch (ScannerException e) {
                updateStatus(e.getMessage());
            }
        }
    }


    public void softScan(View view) {
        bSoftTriggerSelected = false;
        cancelRead();
    }

    private void cancelRead(){
        if (scanner != null) {
            if (scanner.isReadPending()) {
                try {
                    scanner.cancelRead();
                } catch (ScannerException e) {
                    updateStatus(e.getMessage());
                }
            }
        }
    }

    private void updateStatus(final String status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewStatus.setText(status);
            }
        });
    }

    private void updateData(String pType, String pText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pText != null) {
                    String CurrentDateTime = getCurrentDateTime();
                    //String CurrentCode = Helpers.parsujKod(Html.fromHtml(result, 0).toString());
                    //tvBarcode.setText(pText);

                    //textViewData.setText( Html.fromHtml(CurrentDateTime + " <b>" + CurrentCode+"</b>\n" + history));

                    if(dataLength ++ > 100) { //Clear the cache after 100 scans
                        textViewData.setText("");
                        dataLength = 0;
                        history = "";
                        //textViewData.append(Html.fromHtml(result,0));
                    } //else {
                       // textViewData.append(Html.fromHtml(result,0));
                    //}
                    //textViewData.append("\n");

                    String result = "<font color='silver'>" +  CurrentDateTime + "</font>" +"  "+ "<font color='gray'>" +  pType + "</font> : <b>" + pText +"</b>";
                    textViewData.setText(Html.fromHtml(result + "<br>" + history,0));
                    history  = result + "<br>" + history;
                    /*
                    ((View) findViewById(R.id.scrollViewData)).post(new Runnable()
                    {
                        public void run()
                        {
                            ((ScrollView) findViewById(R.id.scrollViewData)).fullScroll(View.FOCUS_DOWN);
                        }
                    });*/
                }
            }
        });
    }
    private void updateList(final String pText, ResultType pResutType){
        Spanned as;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pText != null) {

                    if (pResutType == ResultType.ERROR)
                        textViewData.setText(Html.fromHtml(Helpers.getCurrentDateTime() + ": <span style='color:red'>" + pText + "</span><br>" + history,0));
                    else
                    if (pResutType == ResultType.WARNING)
                        textViewData.setText(Html.fromHtml(Helpers.getCurrentDateTime() + ": <span style='color:yellow'>" + pText + "</span> <br>" + history,0));
                    else
                    if (pResutType == ResultType.SUCCESS)
                        textViewData.setText(Html.fromHtml(Helpers.getCurrentDateTime() + ": <span style='color:green'>" + pText + "</span> <br>" + history,0));
                    else
                        textViewData.setText((Spanned)Html.fromHtml(Helpers.getCurrentDateTime() + ":" + pText + "<br>" + history,0));

                    history  =  Helpers.getCurrentDateTime() + ": " + pText + "<br>" + history;
                }
            }
        });
    }


    private void setDefaultOrientation(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //if(width > height){
        //    setContentView(R.layout.activity_main_landscape);
        //} else {
            setContentView(R.layout.activity_main);
        //}
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        bDecoderSettingsChanged = true;
        cancelRead();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void btnDbAccess_Click(View view) {
        Intent dbActivity = new Intent(this, DbActivity.class);
        startActivity(dbActivity);
    }

    public void clkSetup(View view) {
        Intent SetupActivity = new Intent(this, SetupActivity.class);
        startActivity(SetupActivity);
    }

    public void btnConToServer_onClick(View view) {

        //Settings.getSELF().LoadToFile(MainActivity.getContext());
        String IP = Settings.getSELF().ServerIP;
        Integer port = Settings.getSELF().ServerPort;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Socket socket = new Socket(IP,port); //!!!!! uz vytvorenie objektu pre socket INICIALIZUJE SPOJENIE !!!!
                    //###############################################
                    //tu len precitam co mi prislo, resp. este PRIDE
                    BufferedReader br_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String txtFromServer = br_input.readLine();
                    //###############################################
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvInfo.setText(txtFromServer);
                        }
                    });

                } catch (IOException e) {
                    Helpers.redToast(_mainActivity,"Nepodarilo sa spojit so serverom");
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    @Override
    public void onTCPMessageRecieved(String message) {
        //klient neprijma ziadne spravy zo vzdialeneho servera
        //vsetky sravy su prijmane lokalnym serverom
    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {
        // TODO Auto-generated method stub
        if(isConnectedNow)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    //if (dialog != null) dialog.hide();
                    //Toast.makeText(getApplicationContext(), "Connected to server", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //ButtonAccept START
    public void btnAcceptClick(View view)
    {
        //tvarim sa ze som naskenoval TAccept
        scannedBc = "TAccept";
        String content  = "TAccept";
        TCPCommunicatorClient.writeToSocket(content,UIHandler,this);
        this.updateData("BUTTON CLICK", content);
        logStream.logData("btnAccept_Click: BUTTON ACCEPT CLICK");
    }
    //ButtonAccept END

    //ButtonDbAccess START
    public void btnDbAccessClick(View view) {
        Intent dbACCESS = new Intent(MainActivity.getContext(), DbActivity.class);
        Bundle pBundle = new Bundle();
        pBundle.putInt("OpenTabIndex",1);
        dbACCESS.putExtras(pBundle);
        //Passing a Bundle on startActivity()?
        //https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
        startActivity(dbACCESS);
    }
    //ButtonDbAccess END


    //vypise info o spusteni listenera do HISTORIE
    @Override
    public void onInfoEventOccured(String pType, String pMessage) {
        updateData(pType, pMessage);
    }
    //END ServerListener


    public void PanelOkNokOff() {
        OkNokThreadsCount++;
        Helpers.setTimeout(() -> {

                runOnUiThread(()-> {
                            ((MainActivity) MainActivity.getContext()).TimeOutCallBack();
                    });
                }
                //, Helpers.INTERVAL);
                , Settings.getSELF().Time);
    }


    //######################################################################################################################################################################
    //######################################################################################################################################################################
    public int ShowMyModalDialog(Runnable mMyDialog)  //should be called from non-UI thread
    {
        pressedButtonID = MY_BUTTON_INVALID_ID;
        runOnUiThread(mMyDialog);
        try
        {
            dialogSemaphore.acquire();
        }
        catch (InterruptedException e)
        {

        }
        return pressedButtonID;
    }

    private boolean _isLeader(String pString)
    {
        return pString.compareTo("TLeader1")==0?true:false;
    }private boolean _isAccept(String pString)
    {
        return pString.compareTo("TAccept")==0?true:false;
    }
    /** CALLBACK po NACITANI QR-kodu
     * @param pQrCode - nacitany QR kod v string formate
     */
    @SuppressLint("SuspiciousIndentation")
    private void barcodeRead(String pQrCode)
    {
        //region #MODAL DIALOG
        final Runnable MyYesNoDialog = new Runnable()
        {
            //https://stackoverflow.com/questions/6120567/android-how-to-get-a-modal-dialog-or-similar-modal-behavior
            public void run()
            {
                AlertDialog errorDialog = new AlertDialog.Builder(MainActivity.getContext()).setMessage("Are you sure???")
                        .setPositiveButton("YES", (dialog, which) -> {
                    pressedButtonID = 1;
                    dialogSemaphore.release();
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pressedButtonID = 0;
                        dialogSemaphore.release();
                    }
                }).create();
                errorDialog.setCancelable(false);
                errorDialog.show();
            }
        };
        //endregion

        StringBuilder sbQrCode = new StringBuilder(pQrCode);
        //odstranenie Ska zo zaciatku precitaneho stringu
        //String scannedBc = (sbQrCode.substring(0) == "S")? sbQrCode.delete(0, 1).toString():sbQrCode.toString();
        scannedBc = sbQrCode.toString().trim();

        //https://stackoverflow.com/questions/6120567/android-how-to-get-a-modal-dialog-or-similar-modal-behavior
        //nutne spustat MIMO GUI VLAKNA, inak sa dialog NEZOBRAZI
        //nutne spustat MIMO GUI VLAKNA, inak sa dialog NEZOBRAZI
        //nutne spustat MIMO GUI VLAKNA, inak sa dialog NEZOBRAZI
        //AK JE V POLE POSITION NEPRAZDNE a sucasne MI NIE JE DORUCENY LEADER => ZOBRAZIM DIALOG
        if (tvPosition.getText().toString().compareTo("")!=0 && (!_isLeader(scannedBc) && !_isAccept(scannedBc)))
        {
            int RESULT = ShowMyModalDialog(MyYesNoDialog);
            if (RESULT == 0){
                return;
            }
            //else
                //AK JE LEADER MOD AKTIVOVANY LEN DEAKTIVUJEM BUTTON ACCEPT
            //    btnAcceptHanlde();
//                if (cbLeader.isChecked()) btnAccept.setEnabled(false);
//                 else
//                    //V OPACNOM PRIPADE HO ZNEVIDITELNIM
//                     btnAccept.setVisibility(View.INVISIBLE);
        }

        runOnUiThread(()-> {
            //if (tvPosition.getText().toString().compareTo("") == 0 || scannedBc.compareTo("TLeader1")!=0 )
            //AK JE NACITANY LEADER, NEROBI SA SO ZOBRAZOVANYMI POLAMI NIC
            if (scannedBc.compareTo("TLeader1")!=0) {
                tvBarcode.setText(scannedBc);//bre.strDataBuffer;
                tvPartNumber.setText("");
                tvPosition.setText("");

                tvFirstPallet.setText("");
                tvPrevPallet.setText("");
                tvInfo.setText("");
                PanelOkNokOff();
            }
        });


        //SetText("QR_CODE: " + pQrCode);

                try
                {
                    //ConnectToServer(); <=> NETREBA, PRI ZASIELANI DATA SA ROBI TEST SPOJENIA; NAVYSE 3-nasobny PING TEST PREBEHOL PRED TYM
                    TCPCommunicatorClient.writeToSocket(pQrCode,UIHandler,MainActivity.getContext());
                    runOnUiThread(()-> {
                    if (scannedBc.compareTo("TAccept") == 0 || scannedBc.compareTo("TLeader1") == 0 || scannedBc.compareTo("TResetI") == 0 || scannedBc.compareTo("TResetO") == 0 || scannedBc.compareTo("TClear") == 0 )
                        SetText("QR_SYSTEM: " +scannedBc);
                    else
                        SetText("QR_CODE: " + scannedBc);});
                }

                catch (Exception ex)
                {
                    runOnUiThread(()-> {
                        Helpers.redToast(MainActivity.getContext(),ex.getMessage());
                        SetText("ERROR TCP (.writeToSocket): "+ ex.getMessage());
                    });

                    Log.e("KEPZET ERROR", ex.getMessage());
                }
    }


    private void show_children(View v) {
        ViewGroup viewgroup=(ViewGroup)v;
        for (int i=0;i<viewgroup.getChildCount();i++) {
            View v1=viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup) show_children(v1);
            Log.d("KEPZET-ANDROID-CLIENT", v1.toString());
        }
    }

    private void SetText(String pText) {
        logStream.logData(Helpers.getNow() + ": " + pText);
    }

    public enum  ResultType{
        SUCCESS, ERROR, WARNING
    }


    //region @NOT USED
    // NON USED pripadova studia ONLY
    class TCWriteThread extends Thread implements Runnable{

        //String IP_SERVER = "192.168.1.8";
        String IP_SERVER = "192.168.1.8";
        Integer port_server = 2222;
        private boolean serverIsRunning;
        private String _CONTENT;
        private String _resultText;
        private ResultType _resultType;

        public TCWriteThread(String pContent){

            this._CONTENT = pContent;
        }

        @Override
        public void run() {

            try {
                Socket socket = new Socket(IP_SERVER, port_server);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setTextColor(getResources().getColor(R.color.design_default_color_on_primary,null));
                        tvInfo.setText("Ready for reading!");
                    }
                });

                PrintWriter otWritter = new PrintWriter(socket.getOutputStream());
                otWritter.write(_CONTENT);
                otWritter.flush();
                socket.close();
                _resultText = "Accept SENT succesfully";
            }
            catch (IOException e) {
                //throw new RuntimeException(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //https://stackoverflow.com/questions/6926644/android-color-to-int-conversion
                        //tvReceivedData.setTextColor(android.graphics.Color.RED);
                        //tvReceivedData.setText("Connection to server FAILED!!");
                        _resultText = "Connection to server FAILED!!";
                    }
                });
            }
        }//run()

        public void SendContent(){
            serverIsRunning = true;
            start();
        }//startServer()

    }//WriteThread


    // NON USED pripadova studia ONLY
    class ServerThread extends  Thread implements Runnable{
        private boolean serverIsRunning;
        private ServerSocket serverSocket;
        private int connectCount;

        public void connectServer(){

            serverIsRunning  = true;
            start();
        }

        @Override
        public void run() {
            try {

                //https://stackoverflow.com/questions/60396719/socket-and-serversocket-communication-unclear
                serverSocket = new ServerSocket(MainActivity.this.serverPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setText("Waiting for Clients");
                    }
                });

                while(serverIsRunning){

//                    Socket socket = serverSocket.accept();
                    MyServerSocket = serverSocket.accept();
                    //connectCount++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvInfo.setText("TC21 conected to SERVER: " + MyServerSocket.getInetAddress() + ":" + MyServerSocket.getLocalPort());
                        }
                    });

                    PrintWriter output_Server = new PrintWriter(MyServerSocket.getOutputStream());

                    output_Server.write("TC21 is listenig on"+ Helpers.getLocalIP() +":3333");
                    output_Server.flush();
                    MyServerSocket.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }//run()

        public  void stopSerever(){
            serverIsRunning = false;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if(serverSocket != null) {
                        try {
                            serverSocket.close();
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    textViewStatus.setText("Server Stopped");
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }//stopServer()


        public  void startSerever(){
            serverIsRunning = true;
            start();
        }//startServer()

    }

    // NON USED pripadova studia ONLY
    private int serverPort = 2222;
    // NON USED pripadova studia ONLY
    private ServerThread serverThread;

    // NON USED pripadova studia ONLY
    public  void btnStartServer(View view){

        serverThread = new ServerThread();
        serverThread.startSerever();

    } //btnStartServer


    // NON USED pripadova studia ONLY
    public  void btnStopServer(View view){

        serverThread.stopSerever();
    }
    //endregion

    public void writeError(String pErrorText){

        tvLastError.setText(pErrorText);
    }


}

/*
 * Copyright (C) 2015-2019 Zebra Technologies Corporation and/or its affiliates
 * All rights reserved.
 */
package com.symbol.kepzetclient;

import static com.symbol.kepzetclient.Helpers.getCurrentDateTime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.symbol.kepzetclient.auxx.LogFile;
import com.symbol.kepzetclient.custom_components.SetupActivity;
import com.symbol.kepzetclient.tcp.TCPClientListener;
import com.symbol.kepzetclient.tcp.TCPCommunicatorClient;
import com.symbol.kepzetclient.tcp.TCPCommunicatorServer;
import com.symbol.kepzetclient.tcp.TCPServerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
        implements EMDKListener, DataListener, StatusListener, ScannerConnectionListener, OnCheckedChangeListener,
        TCPClientListener, TCPServerListener {


    public Socket MyServerSocket;
    private static com.symbol.kepzetclient.auxx.FS fs;

    private TCPCommunicatorClient tcpClient;
    private TCPCommunicatorServer tcpServer;

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    private TextView textViewData = null;
    private TextView textViewStatus = null;

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
    private TextView tvReceivedData = null;

    private boolean bSoftTriggerSelected = false;
    private boolean bDecoderSettingsChanged = true;
    private boolean bExtScannerDisconnected = false;
    private final Object lock = new Object();
    private String history = "";
    public com.symbol.emdk.barcode.ScannerInfo SI = null;

    public static LogFile lf;
    private static MainActivity _mainActivity;

    TabLayout tabLayout;
    private Connection con;

    private ProgressDialog dialog;
    private boolean isFirstLoad = true;
    private Handler UIHandler = new Handler();
    private Handler HandlerServer = new Handler();

    private void ConnectToServer() {
        //setupDialog();
        tcpClient = TCPCommunicatorClient.getInstance();
        TCPCommunicatorClient.addListener(this);
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        tcpClient.init(/*settings.getString(EnumsAndStatics.SERVER_IP_PREF,*/ "192.168.1.8"//),
                /*Integer.parseInt(settings.getString(EnumsAndStatics.SERVER_PORT_PREF, "1500"))*/,2222);
    }


    public void OtvorSettingsScreen() {
        startActivity(new Intent(MainActivity.this,Preferences.class));
    }

    public static LogFile getLogFile(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (lf == null || !lf.getAssignedDate().isEqual(LocalDate.now()))
                lf = new LogFile(_mainActivity, LocalDate.now());
        }
        return lf;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //client
        this.ConnectToServer();
        //this.btnStartServer(null);
        //END client


        //#########################################################################################
        //server
        this.tcpServer = TCPCommunicatorServer.getInstance();
        TCPCommunicatorServer.addListener(this);
        tcpServer.init(3333); //lokalny server na klientovi bude pocuvat na pripadne spravy HLAVNEHO SERVERU (192.168.1.8) na 3333ke
        //END server
        //#########################################################################################

        _mainActivity = this;

        deviceList = new ArrayList<ScannerInfo>();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setDefaultOrientation();

        textViewData = findViewById(R.id.textViewData);
        tvReceivedData = findViewById(R.id.tvReceivedData);
        tvBarcode = findViewById(R.id.tvBarcode);
        textViewStatus = findViewById(R.id.textViewStatus);
        //checkBoxEAN8 = (CheckBox)findViewById(R.id.checkBoxEAN8);
        //checkBoxEAN13 = (CheckBox)findViewById(R.id.checkBoxEAN13);
        //checkBoxCode39 = (CheckBox)findViewById(R.id.checkBoxCode39);
        //checkBoxCode128 = (CheckBox)findViewById(R.id.checkBoxCode128);
        spinnerScannerDevices = findViewById(R.id.spinnerScannerDevices);

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            updateStatus("EMDKManager object request failed!");
            return;
        }

        //checkBoxEAN8.setOnCheckedChangeListener(this);
        //checkBoxEAN13.setOnCheckedChangeListener(this);
        //checkBoxCode39.setOnCheckedChangeListener(this);
        //checkBoxCode128.setOnCheckedChangeListener(this);

        //nutne volat ZA az po vytvoreni deviceList-u (enumerateScannerSevice)), inak sa scener neinicializuje
        //deviceList sa vytvara v enumerateScannerDevices();
        addSpinnerScannerDevicesListener();

        textViewData.setSelected(true);
        textViewData.setMovementMethod(new ScrollingMovementMethod());

        //DB CONNECTION
        //ConnectionClass connectionClass = new ConnectionClass();
        //con = connectionClass.CONN();

        textViewStatus.setText("AHOJJJJJ");

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
        //setContentView(R.layout.activity_main);
        if(!isFirstLoad)
        {
            TCPCommunicatorClient.closeStreams();
            ConnectToServer();
        }
        else
            isFirstLoad=false;
        //TCP END
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The application is in background
        // Release the barcode manager resources
        deInitScanner();
        deInitBarcodeManager();
    }

    @Override
    public void onClosed() {
        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        updateStatus("EMDK closed unexpectedly! Please close and restart the application.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
        this.btnStopServer(null);

    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList <ScanData> scanData = scanDataCollection.getScanData();
            for(ScanData data : scanData) {
                updateData("<font color='silver'>" +  Helpers.getCurrentDateTime() + "</font>" +"  "+ "<font color='gray'>" +  data.getLabelType() + "</font> : <b>" + data.getData() +"</b>");
            }
        }
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
        bSoftTriggerSelected = true;
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

    private void updateData(final String result){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result != null) {

                    String CurrentDateTime = getCurrentDateTime();
                    String CurrentCode = Helpers.parsujKod(Html.fromHtml(result, 0).toString());
                    tvBarcode.setText(CurrentCode);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            this.OtvorSettingsScreen();
            return true;
//            case R.id.action_about:
//                AboutBox.Show(MainActivity2.this);return true;
//
//            case (R.id.searchDeviceMenu):
//                Intent intent = new Intent(MainActivity2.this, SearchActivity.class);
//                startActivity(intent);
//                return true;
//            //AUXX.redToast(MainActivity2.this,"Vyhľadávanie bude dorobené!!!"); return true;
//            case R.id.addDevice:
//                showNoticeDialog();
//                //InsertDialog.Show(MainActivity2.this);return true;
        }
        return super.onOptionsItemSelected(item);

//        if (id == R.id.action_settings) {
//            return true;
//        }


//        return super.onOptionsItemSelected(item);
    }

    public void clkAccept(View view) {
        Intent dbActivity = new Intent(this, DbActivity.class);
        startActivity(dbActivity);
    }

    public void clkSetup(View view) {
        //Intent preferencesIntent = new Intent(this, Preferences.class);
        //startActivity(preferencesIntent);

        //this.OtvorSettingsScreen();

        Intent SetupActivity = new Intent(this, SetupActivity.class);
        startActivity(SetupActivity);


    }

    public void onClick_btnAccept(View view) {
        //tcpSecnd.send ("TAccept");
        TCWriteThread tcw = new TCWriteThread("TAccept");
        tcw.SendContent();
        try {
            tcw.join();
            updateList(tcw._resultText, ResultType.SUCCESS);
        }
        catch (InterruptedException e) {
            updateList("TCP thread ERROR!", ResultType.ERROR);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //SetText("S@" + LocalDateTime.now().toString() + ": accept");
        }
    }

//    private void SetText(String text)
//    {
//          runOnUiThread(()->{
//              lf.appendLog(text);
//          });
////        // InvokeRequired required compares the thread ID of the
//        // calling thread to the thread ID of the creating thread.
//        // If these threads are different, it returns true.
//        if (this.InvokeRequired)
//        {
//            SetTextCallback d = new SetTextCallback(SetText);
//            this.Invoke(d, new object[] { text });
//        }
//        else
//        {
            //this.listBox1.Items.Add(text);
            //listBox1.SelectedIndex = listBox1.Items.Count - 1;
           // FS.logData(text);

//        }
//    }

    public void btnConToServer_onClick(View view) {
        String IP = "192.168.1.24";
        Integer port = 2222;
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
                            tvReceivedData.setText(txtFromServer);
                        }
                    });

                } catch (IOException e) {
                    Helpers.redToast(_mainActivity,"Nepodarilo sa spojit so serverom");
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    //CLIENT onTCPMessageRecieved
    @Override
    public void onTCPMessageRecieved(String pMessage) {
        // TODO Auto-generated method stub
        final String theMessage=pMessage;
        //try {
            //JSONObject obj = new JSONObject(message);
            //String messageTypeString=obj.getString(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON);
            //MessageTypes messageType = EnumsAndStatics.getMessageTypeByString(messageTypeString);
            //switch(messageType)
            //{
            //case MessageFromServer:
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            TextView editTextFromServer =(TextView)findViewById(R.id.tvReceivedData);
                            editTextFromServer.setText(theMessage);
                        }
                    });

               //     break;
               // }
                            }
        //} catch (JSONException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        //}

    }
    //CLIENT END

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {

        // TODO Auto-generated method stub
        if(isConnectedNow)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (dialog != null)
                        dialog.hide();
                    Toast.makeText(getApplicationContext(), "Connected to server", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }


    public void btnSendClick(View view)
    {
        //tcpClient.init("192.168.1.8",2222);
        ConnectToServer();
        String content  = "TAccept";
        TCPCommunicatorClient.writeToSocket(content,UIHandler,this);
    }
    //btnSendClick


    //ServerListener
    //https://www.facebook.com/share/r/cF3JcNMUMhKzrCLQ/
    //https://www.facebook.com/share/v/fsyALHALtUWCjHpQ/
    @Override
    public void onTCPMessageServerRecieved(String pMessage) {

        // TODO Auto-generated method stub
        String theMessage = pMessage;
        HandlerServer.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try
                {
                    TextView editTxt = (TextView) findViewById(R.id.tvTHISServer);
                    editTxt.setText(theMessage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    //END ServerListener

    //######################################################################################################################################################################
    //######################################################################################################################################################################
    public enum  ResultType{
        SUCCESS, ERROR, WARNING
    }
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
                        tvReceivedData.setTextColor(getResources().getColor(R.color.design_default_color_on_primary,null));
                        tvReceivedData.setText("Ready for reading!");
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
                        tvReceivedData.setText("Waiting for Clients");
                    }
                });

                while(serverIsRunning){

//                    Socket socket = serverSocket.accept();
                    MyServerSocket = serverSocket.accept();
                    //connectCount++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvReceivedData.setText("TC21 conected to SERVER: " + MyServerSocket.getInetAddress() + ":" + MyServerSocket.getLocalPort());
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

    private int serverPort = 2222;

    private ServerThread serverThread;

    public  void btnStartServer(View view){

        serverThread = new ServerThread();
        serverThread.startSerever();

    } //btnStartServer

    public  void btnStopServer(View view){

        serverThread.stopSerever();
    }


}

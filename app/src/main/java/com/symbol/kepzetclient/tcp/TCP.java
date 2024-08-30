package com.symbol.kepzetclient.tcp;

import android.app.ProgressDialog;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class TCP implements TCPClientListener {

    private TCPCommunicatorClient tcpClient;
    private ProgressDialog dialog;
    public static String currentUserName;
    private Handler UIHandler = new Handler();
    private boolean isFirstLoad=true;


    public TCP () {
        ConnectToServer();
    }

    private void ConnectToServer() {
        setupDialog();
        tcpClient = TCPCommunicatorClient.getInstance();
        TCPCommunicatorClient.addListener(this);
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        tcpClient.init( "192.168.1.100", Integer.parseInt("1500"));
    }



    private void setupDialog() {
//        dialog = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
//        dialog.setTitle("Loading");
//        dialog.setMessage("Please wait...");
//        dialog.setIndeterminate(true);
//        dialog.show();
    }


    public void btnSendClick(View view)
    {
//        JSONObject obj = new JSONObject();
//        EditText txtName= (EditText)findViewById(R.id.txtUserName);
//        if(txtName.getText().toString().length()==0)
//        {
//            Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try
//        {
//            obj.put(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON,MessageTypes.MessageFromClient);
//            obj.put(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON, txtName.getText().toString());
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//        TCPCommunicatorClient.writeToSocket(obj,UIHandler,this);
        //dialog.show();

    }

    @Override
    public void onTCPMessageRecieved(String message) {
        // TODO Auto-generated method stub
        final String theMessage=message;
//        try {
//            JSONObject obj = new JSONObject(message);
//            String messageTypeString=obj.getString(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON);
//            MessageTypes messageType = EnumsAndStatics.getMessageTypeByString(messageTypeString);
//
//
//                    new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            EditText editTextFromServer =(EditText)findViewById(R.id.editTextFromServer);
//                            editTextFromServer.setText(theMessage);
//                        }
//                    };
//
//
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {
        // TODO Auto-generated method stub
        if(isConnectedNow)
        {
            new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    dialog.hide();
                    Toast.makeText(dialog.getContext(), "Connected to server", Toast.LENGTH_SHORT).show();
                }
            };

        }
    }
}

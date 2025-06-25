package com.symbol.ptlclient2024.custom_components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.symbol.ptlclient2024.About;
import com.symbol.ptlclient2024.DbActivity;
import com.symbol.ptlclient2024.Helpers;
import com.symbol.ptlclient2024.HorizontalNumberPicker;
import com.symbol.ptlclient2024.R;
import com.symbol.ptlclient2024.auxx.MyAlertDialog;
import com.symbol.ptlclient2024.auxx.Settings;
import com.symbol.ptlclient2024.tcp.TCPCommunicatorClient;

public class SetupActivity extends AppCompatActivity {

    private Button btnSave;
    private Button btnCancel;
    private Button btnQuit;
    private Button btnDbAccess;
    private Button btnSend;

    private TextView  tvSettingsChangeText;

    private EditText  etServerIP;
    private EditText etDbName;
    private EditText etDbPort;
    private EditText etDbUser;
    private EditText etClientIP;
    private EditText etServerPort;

    private int _oldClientPort;

    private EditText etClientPort;
    private EditText etTime;
    private HorizontalNumberPicker hnpVolume;
    private EditText etPassword1;
    private EditText etPassword2;
    private CheckBox cbManualAccept;

    private TextView tvPssPhraseText;

    private Context  _context;

    private int OpenTabIndex;
    private EditText etIgnoreNo;

    private Dialog pswDialog;

    private SetupActivity _thisActivity;

    private Button btnPswdCancel;
    private Button btnPswdOK;

    private EditText etxPassword;

    private TextView tvPswdError;
    private HorizontalNumberPicker hnpMaxY;
    private HorizontalNumberPicker hnpMaxX;
    private Button btnAboutDlg;



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("OpenTabIndex",1);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        _thisActivity  = this;
        _context = this;

        //Settings.getSELF().LoadToFile(SetupActivity.this);


//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .add(R.id.fragment_container_view, PasswordFragment2.class, null)
//                    .commit();
//        }
        View view = getLayoutInflater().inflate(R.layout.fragment_password,null);
        pswDialog = new Dialog(this, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        pswDialog.setCancelable(false);
        //pswDialog.setCancelable(true);
        pswDialog.setContentView(view);
        btnPswdCancel = view.findViewById(R.id.btnPswdCancel);
        btnPswdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _thisActivity.finish();
            }
        });

        btnPswdOK = view.findViewById(R.id.btnPswdOk);
        tvPswdError = view.findViewById(R.id.tvPswdError);
        etxPassword = view.findViewById(R.id.etxPswPassword);



        btnPswdOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pswd = etxPassword.getText().toString();
                String heslo = Settings.getSELF().Password;
                if (pswd.compareTo(heslo)== 0)
                    pswDialog.dismiss();
                else
                    tvPswdError.setText("Invalid PASSWORD");

            }
        });


        tvSettingsChangeText = (TextView)findViewById(R.id.tvSettingsChangeText);

        btnSave = (Button)findViewById(R.id.btnSaveSearch);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnQuit = (Button)findViewById(R.id.btnQuit);
        btnDbAccess = (Button)findViewById(R.id.btnDbAccess);
        btnSend = (Button)findViewById(R.id.btnSend);


        etServerIP = (EditText)findViewById(R.id.etServerIP);
        etClientIP = (EditText)findViewById(R.id.etClientIp);
        etServerPort = (EditText)findViewById(R.id.etServerPort);

        etClientPort = (EditText)findViewById(R.id.etClientPort);

        etTime = (EditText)findViewById(R.id.etTime);
        etPassword1 = (EditText)findViewById(R.id.etPswd1);
        etPassword2 = (EditText)findViewById(R.id.etPswd2);
        etIgnoreNo = (EditText)findViewById(R.id.etIgnoreNo);


        etDbName = (EditText)findViewById(R.id.etDBName);
        etDbPort = (EditText)findViewById(R.id.etDBServerPort);
        etDbUser = (EditText)findViewById(R.id.etDBUser);

        cbManualAccept = (CheckBox)findViewById(R.id.cbManualAccept);
        hnpVolume = (HorizontalNumberPicker) findViewById(R.id.hnpVolume);
        hnpVolume.setMin(0);
        hnpVolume.setMax(12);

        tvPssPhraseText = (TextView)findViewById(R.id.tvPssPhraseText);
        tvPssPhraseText.setVisibility(View.INVISIBLE);

        hnpMaxX =  (HorizontalNumberPicker)findViewById(R.id.hnpMaxX);
        hnpMaxY =  (HorizontalNumberPicker)findViewById(R.id.hnpMaxY);

        hnpMaxX.setMin(1); hnpMaxY.setMin(1);
        hnpMaxX.setMax(100); hnpMaxY.setMax(100);
        hnpMaxX.setRotate(true); hnpMaxY.setRotate(true);

        hnpVolume.setMin(0);
        hnpVolume.setMax(100);
        hnpVolume.setRotate(true);


        //tvSettingsChangeText = (TextView)view.findViewById(R.id.tvSettingsChangeText);
        //tvSettingsChangeText.setVisibility(View.INVISIBLE);




        btnAboutDlg = (Button)findViewById(R.id.btnAboutDlg);

        btnAboutDlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                About.Show(SetupActivity.this);
            }
        });

        etPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etPassword1.getText().toString().compareTo(etPassword2.getText().toString())!=0)
                    tvPssPhraseText.setVisibility(View.VISIBLE);
                else
                    tvPssPhraseText.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etPassword1.getText().toString().compareTo(etPassword2.getText().toString())!=0)
                    tvPssPhraseText.setVisibility(View.VISIBLE);
                else
                    tvPssPhraseText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        this.FillTheValues();
        this._oldClientPort = Integer.valueOf(etClientPort.getText().toString());


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPassword1.getText().toString().compareTo(etPassword2.getText().toString())!=0){
                    Helpers.redToast(_context, "Passwords do not match!");
                    return;
                }
                if (etPassword1.getText().toString().compareTo("")==0){
                    Helpers.redToast(_context, "Password can not be EMPTY!");
                    return;
                }


                SetupActivity.this.SaveTheValues();

                if (!Settings.getSELF().SaveToFile(SetupActivity.this)){
                    Helpers.redToast(_context, "Saving of settings FAILED!");
                    return;
                }

                //po kliknuti na SAVE a uspesnom ulozeni sucasne aktualizujem instanciu s nastaveniami
                if (!Settings.getSELF().LoadFromFile(SetupActivity.this)){
                    Helpers.redToast(_context, "Loading of settings FAILED!");
                    return;
                }


                if (clientPortChanged()){

                    tvSettingsChangeText.setVisibility(View.VISIBLE);
                    //aktualizujem odchytenu hodnotu portu servera
                    //_oldServerPort = Integer.valueOf(etServerPort.getText().toString());
                }
                else
                    //ak je hlaska zobrazena z predosleho ulozenia zrusim ju ak som vratil hodnotu portu servera na hodnotu nastavenu pri otvoreni Setup aktivity
                    tvSettingsChangeText.setVisibility(View.INVISIBLE);

                //Log.i("TestSerializacieObjektu", Settings.SaveToFile(SetupActivity.this));
                //Settings.SaveToFile(SetupActivity.this);
                //Log.i("settings:",Settings.SELF.ServerIP);
            }

        });

//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//        builder.setTitle("Confirm");
//        builder.setMessage("Are you sure?");


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = false;

                MyAlertDialog myAlertDialog = new MyAlertDialog();

                String ignoreNumber = etIgnoreNo.getText().toString();
                if (ignoreNumber.length() != 8) {
                    myAlertDialog.createDialog(
                            _context,
                            new Runnable() {
                                    @Override
                                    public void run() {

                                        TCPCommunicatorClient.writeToSocket("I"+ignoreNumber, new Handler(), _context);
                                        Log.e("Runnable", "Runnable TEST ");
                                    }
                                },
                            "Confirmation",
                            "Wrong format, are you sure?");

                    myAlertDialog.showDialog();
                }
                else
                    TCPCommunicatorClient.writeToSocket("I"+ignoreNumber, new Handler(), _context);



//
//
//                if (.Text.Length != 8)
//                {
//                    if (MessageBox.Show("wrong format, are you sure?", "?", MessageBoxButtons.YesNo, MessageBoxIcon.Question, MessageBoxDefaultButton.Button1) == DialogResult.No)
//                    {
//                        return;
//                    }
//                }
//
//                tcpSend.send("I" + tbIgnore.Text);
            }
        });



        btnDbAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dbACCESS = new Intent(SetupActivity.this, DbActivity.class);
                Bundle pBundle = new Bundle();
                pBundle.putInt("OpenTabIndex",1);
                dbACCESS.putExtras(pBundle);
                //Passing a Bundle on startActivity()?
                //https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
                startActivity(dbACCESS);
            }
        });

        //########################################################################################################################
        pswDialog.show();
        //########################################################################################################################
    }

    private boolean clientPortChanged(){
        if (_oldClientPort ==  Integer.valueOf(etClientPort.getText().toString()))
        {
            return false;

        }
        return true;
    }

    public void FillTheValues(){

        etServerIP.setText(Settings.getSELF().ServerIP);
        etClientIP.setText(Helpers.getLocalIP());
        etServerPort.setText(String.valueOf(Settings.getSELF().ServerPort));
        etClientPort.setText(String.valueOf(Settings.getSELF().ClientPort));

        etTime.setText(String.valueOf(Settings.getSELF().Time));
        etPassword1.setText(Settings.getSELF().Password);
        etPassword2.setText(Settings.getSELF().Password);

        cbManualAccept.setChecked(Settings.getSELF().ManualAccepting);
        hnpVolume.setValue(Settings.getSELF().Volume);

        etDbUser.setText(Settings.getSELF().DbUser);
        etDbName.setText(Settings.getSELF().DbName);
        etDbPort.setText(Settings.getSELF().DbPort.toString());

        hnpMaxX.setValue(Settings.getSELF().MaxX);
        hnpMaxY.setValue(Settings.getSELF().MaxY);

    }

    public void SaveTheValues(){

        Settings.getSELF().ServerIP = String.valueOf(etServerIP.getText());
        Settings.getSELF().ClientIP = String.valueOf(etClientIP.getText());
        Settings.getSELF().ServerPort = Integer.valueOf(etServerPort.getText().toString());
        Settings.getSELF().ClientPort = Integer.valueOf(etClientPort.getText().toString());

        Settings.getSELF().Time = Integer.valueOf(etTime.getText().toString());
        Settings.getSELF().Password = etPassword1.getText().toString();
        Settings.getSELF().Password = etPassword2.getText().toString();

        Settings.getSELF().ManualAccepting = cbManualAccept.isChecked();
        Settings.getSELF().Volume = hnpVolume.getValue();

        Settings.getSELF().DbPort = Integer.valueOf(etDbPort.getText().toString());
        Settings.getSELF().DbName = etDbName.getText().toString();
        Settings.getSELF().DbUser = etDbUser.getText().toString();

        Settings.getSELF().MaxX = hnpMaxX.getValue();
        Settings.getSELF().MaxY = hnpMaxY.getValue();

    }

    public void quit(View view) {
        //https://stackoverflow.com/questions/6330200/how-to-quit-android-application-programmatically
        //finishAndRemoveTask();
        //getActivity().finish();
        finishAffinity();
        System.exit(0);
    }

    public void btnCancelOnClickHandler(View pView){

        this.FillTheValues();
    }

    public void btnCloseOnClickHandler(View pView){
        this.finish();
    }


}
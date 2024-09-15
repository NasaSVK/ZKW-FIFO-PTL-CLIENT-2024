package com.symbol.kepzetclient.custom_components;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.symbol.kepzetclient.DbActivity;
import com.symbol.kepzetclient.Helpers;
import com.symbol.kepzetclient.HorizontalNumberPicker;
import com.symbol.kepzetclient.R;
import com.symbol.kepzetclient.auxx.Settings;

public class SetupActivity extends AppCompatActivity {

    private Button btnSave;
    private Button btnCancel;
    private Button btnQuit;
    private Button btnDbAccess;

    private EditText  etServerIP;
    private EditText etClientIP;
    private EditText etServerPort;
    private EditText etClientPort;
    private EditText etTime;
    private HorizontalNumberPicker hnpVolume;
    private EditText etPassword1;
    private EditText etPassword2;
    private CheckBox cbManualAccept;

    private int OpenTabIndex;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putInt("OpenTabIndex",1);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Settings.getSELF().LoadToFile(SetupActivity.this);

        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnQuit = (Button)findViewById(R.id.btnQuit);
        btnDbAccess = (Button)findViewById(R.id.btnDbAccess);

        etServerIP = (EditText)findViewById(R.id.etServerIP);
        etClientIP = (EditText)findViewById(R.id.etClientIp);
        etServerPort = (EditText)findViewById(R.id.etServerPort);
        etClientPort = (EditText)findViewById(R.id.etClientPort);

        etTime = (EditText)findViewById(R.id.etTime);
        etPassword1 = (EditText)findViewById(R.id.etPswd1);
        etPassword2 = (EditText)findViewById(R.id.etPswd2);

        cbManualAccept = (CheckBox)findViewById(R.id.cbManualAccept);
        hnpVolume = (HorizontalNumberPicker) findViewById(R.id.hnpVolume);

        this.FillTheValues();



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetupActivity.this.SaveTheValues();
                Settings.getSELF().SaveToFile(SetupActivity.this);

                //Log.i("TestSerializacieObjektu", Settings.SaveToFile(SetupActivity.this));
                //Settings.SaveToFile(SetupActivity.this);
                //Log.i("settings:",Settings.SELF.ServerIP);
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
        Settings.getSELF().Volume = Integer.valueOf(hnpVolume.getValue());

    }

    public void quit(View view) {
        this.finish();
    }
}
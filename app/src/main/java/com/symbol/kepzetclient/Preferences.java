package com.symbol.kepzetclient;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Preferences extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    //https://developer.android.com/guide/topics/ui/settings.html
    //http://www.cs.dartmouth.edu/~campbell/cs65/lecture12/lecture12.html
    //NEW https://developer.android.com/develop/ui/views/components/settings#java

    public static final String KEY_PREF_SYNC_CONN = "pref_syncConnectionType";
    private static final String READ_ERROR = "Nepodarilo sa prečítať hodnotu!";
    public static PrefsFragment mPrefsFragment;
    public static SharedPreferences sharedPref;
    private AlertDialog.Builder etpDialog = null;

    //PrefsFragment fragment definovany v ramci aktivity, ktora ho hostuje
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etpDialog = new AlertDialog.Builder(this);


        setContentView(R.layout.z_activity_pref);

        //setTheme(android.R.style.ThemeOverlay); //cervene detaily
        //setTheme(android.R.style.Theme_Light);
        //setTheme(android.R.style.Theme_DeviceDefault_Light);//zelene detaily

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //####################################################
        //getSupportActionBar().setIcon(R.mipmap.slon_round);
        //####################################################

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //sharedPref = getPreferences(MODE_PRIVATE);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getFragmentManager(); //FragmentManager potrebujem aby som mohol zacat Fragment transaction
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction(); //mFragmentTransaction potrebujem, aby som sa mohol na zacatu transakciu odvolavat
        mPrefsFragment = new PrefsFragment(); //vytvorim framgment s nastaveniami podla xml.
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);//nahradim standardny obsah okna novym fragmentom obsahujucim navrh preferences.xml
        mFragmentTransaction.commit(); //spustim transakciu
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(this); //namiesto kodu v on Resume a on Pause
        //sharedPref.registerOnSharedPreferenceChangeListener(this); ////namiesto kodu v on Resume a on Pause
        //String syncConnPref = sharedPref.getString( "db_server",""); ////namiesto kodu v on Resume a on Pause

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Preference connectionPref = mPrefsFragment.findPreference("db_server");
        //String hodnota = sharedPref.getString("db_server", "Nepodarilo sa prečítať hodnotu!");
        //connectionPref.setSummary(hodnota);
    }

    //dialogove okno pre zadanie hesla
    //pText - predvolene heslo
    void inicializujEtpDialog(String pText) {
        etpDialog.setTitle("Vaše heslo");
        etpDialog.setMessage("\n do podnikového systému NASA/STOPLUP");

        // Set up the input pre zadanie hesla
        final EditText input = new EditText(this);
        input.setText(pText);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //https://stackoverflow.com/questions/6217378/place-cursor-at-the-end-of-text-in-edittext?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        input.setSelection(input.getText().length());

        //vlozenie vlastneho inputu do dialogu
        etpDialog.setView(input);

        // Set up the buttons
        etpDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Heslo = input.getText().toString();
                savePref("user_pass", Heslo);
                String Login = sharedPref.getString("user_login", READ_ERROR);
                //new Prihlasenie().execute(Login, Heslo);
            }
        });

        etpDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPrefsFragment.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        //https://stackoverflow.com/questions/3827356/setting-ui-preference-summary-field-to-the-value-of-the-preference
        //PREDOVSETKYM NASTEVENIE SUMMARY
        //Preference connectionPref = mPrefsFragment.findPreference("db_server");
        //connectionPref.setSummary(sharedPref.getString("db_server", READ_ERROR));

        //connectionPref = mPrefsFragment.findPreference("max_time_before_syn");
        //String string = sharedPref.getString("max_time_before_syn", READ_ERROR);
        //if (!string.contentEquals(READ_ERROR)) string += " sekúnd";
        //connectionPref.setSummary(string);

        //connectionPref = mPrefsFragment.findPreference("syn_after_start");
        //Boolean bool = sharedPref.getBoolean("syn_after_start", false);
        //if (bool) string = "ÁNO"; else string = "NIE";
        //connectionPref.setSummary(string);

        //connectionPref = mPrefsFragment.findPreference("syn_after_multi");
        //bool = sharedPref.getBoolean("syn_after_multi", false);
        //if (bool) string = "ÁNO"; else string = "NIE";
        //connectionPref.setSummary(string);

        //connectionPref = mPrefsFragment.findPreference("user_login");
        //string = sharedPref.getString("user_login", READ_ERROR);
        //if (string.contentEquals("")) string = "NEZADANÉ";
        //connectionPref.setSummary(string);

        Preference connectionPref = mPrefsFragment.findPreference("password");
        String string = sharedPref.getString("password", "");
        if (string.contentEquals("")) string = "NEZADANÉ"; //else string = dajHviezdicky(string);
        connectionPref.setSummary(string);

        //connectionPref = mPrefsFragment.findPreference("user_name");
        //string = sharedPref.getString("user_name", "");
        //if (string.contentEquals("")) string = "NEZNÁMY";
        //connectionPref.setSummary(string);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPrefsFragment.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String string = null;
        Preference connectionPref = mPrefsFragment.findPreference(key);
        switch (key) {
            case "db_server":
                string = sharedPreferences.getString(key, READ_ERROR);
                //GetData.AktualizujConnection();
                break;
            case "max_time_before_syn":
                string = sharedPref.getString("max_time_before_syn", READ_ERROR);
                if (!string.contentEquals(READ_ERROR)) string += " sekúnd";
                break;
            case "syn_after_start":
                Boolean bool = sharedPref.getBoolean("syn_after_start", false);
                if (bool) string = "ÁNO";
                else string = "NIE";
                break;
            case "syn_after_multi":
                bool = sharedPref.getBoolean("syn_after_multi", false);
                if (bool) string = "ÁNO";
                else string = "NIE";
                break;
            case "user_login":
                string = sharedPref.getString("user_login", READ_ERROR);
                if (string.contentEquals("")) string = "NEZADANÉ";
                //PreferenceScreen screen = findPreferenceScreenForPreference("user_pass",null);
                PreferenceScreen screen = mPrefsFragment.getPreferenceScreen();
                // the position of your item inside the preference screen above
                //int pos = mPrefsFragment.findPreference("user_pass").getOrder();
                //int pos = screen.findPreference("user_login").getOrder();

                //otvorenie textview-u s posswordom pri zmene prihlasovacieho mena
                screen.onItemClick(null, null, 7, 0);
                break;
            case "user_pass":
                string = sharedPref.getString("user_pass", "");
                //ak da uzivatel pred heslo alebo za heslo medzeru alebo enter treba ich odstranit
                if (!string.equals(Helpers.OrezString(string))) {
                    savePref("user_pass", string.trim());
                    break;
                }

                if (string.contentEquals("")) string = "NEZADANÉ";
                else string = dajHviezdicky(string);
                //new Prihlasenie().execute(sharedPref.getString("user_login", READ_ERROR),sharedPref.getString("user_pass", READ_ERROR));
                //((MainActivity2)MainActivity2.getContextOfApplication()).synchronizujsDB();
                //((MainActivity2)getApplicationContext()).synchronizujsDB();
                break;
        }
        connectionPref.setSummary(string);
    }

    private PreferenceScreen findPreferenceScreenForPreference(String key, PreferenceScreen screen) {
        if (screen == null) {
            screen = mPrefsFragment.getPreferenceScreen();
        }

        PreferenceScreen result = null;

        android.widget.Adapter ada = screen.getRootAdapter();
        for (int i = 0; i < ada.getCount(); i++) {
            String prefKey = ((Preference) ada.getItem(i)).getKey();
            if (prefKey != null && prefKey.equals(key)) {
                return screen;
            }
            if (ada.getItem(i).getClass().equals(android.preference.PreferenceScreen.class)) {
                result = findPreferenceScreenForPreference(key, (PreferenceScreen) ada.getItem(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }


    private String dajHviezdicky(String pString) {

        StringBuffer SB = new StringBuffer(pString.length());
        for (int i = 0; i < pString.length(); i++) {
            SB.append('*');
        }
        return SB.toString();
    }

    private void setSumary(Preference pPref, String pString) {
        pPref.setSummary(pString);

        //Ma sharedPref.getAll()

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /*public class Prihlasenie extends AsyncTask<String,String,String>
    {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params)
        {

            Map<String,String> USER = null;
            if (GetData.DB_AUTO.Connect())
            {
                try {
                    //#####################
                    //USER =  GetData.dajUzivatela("duracer","CT2Wq/fNATdOTufhy43cSQ==",GetData.DB_AUTO.connection);
                    USER =  GetData.dajUzivatela(params[0],params[1],GetData.DB_AUTO.connection);
                    if (USER != null) return ('#'+USER.get("MENO")+" "+USER.get("PRIEZVISKO"));
                    else
                    if (GetData.DB_BC.Connect()) {
                        USER =  GetData.dajUzivatela(params[0],params[1],GetData.DB_BC.connection);
                        if (USER != null) return ('#'+USER.get("MENO") + " " + USER.get("PRIEZVISKO"));
                    }
                    else
                        return("Nepodarilo sa pripojiť k podnikovej databáze spoločnosti STOPLUP"*//*+ System.lineSeparator()*//*);

                }
                catch (SQLException e) {
                    e.printStackTrace();
                    return (e.getMessage()+"Chyba (SQLException) pri overovaní užívateľa DB ");
                }
                return("ZLÉ MENO alebo HESLO");
            }
            else
            {
                return("Nepodarilo sa pripojiť k podnikovej databáze spoločnosti NASA Slovakia!"*//*+ System.lineSeparator()*//*);
            }
        }

        //vykona sa pred zacatim procesu
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(PrefActivity.this, "Overovanie užívateľa", "Čakajte prosím...");
        }

        //vykona sa po skonceni procesu
        @Override
        protected void onPostExecute(String result)
        {
            progressDialog.dismiss();
            //0xffafed44
            if (result.charAt(0)=='#') {
                //ak sa prihlásil ten istý užívateľ nerobím nič
                Toast.makeText(PrefActivity.this,"Prihlásený: " + result.substring(1), Toast.LENGTH_SHORT).show();
                if (MainActivity2.GetUserName().equals(result.substring(1))) return;

                Preference Pref = mPrefsFragment.findPreference("user_name");
                savePref("user_name",result.substring(1));
                Pref.setSummary(result.substring(1));
                MainActivity2.AKTUALIZUJ = true;
            }
            else {
                AUXX.redToast(PrefActivity.this,result);

                //if (result.equals("SIGNUP_FAILED"))
                {
                    savePref("user_name","NEZNÁMY");
                    Preference Pref = mPrefsFragment.findPreference("user_name");
                    Pref.setSummary("NEZNÁMY");}
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }


    }
*/

    public static void savePref(String valueKey, String value) {
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(valueKey, value);
        edit.commit();
    }
}

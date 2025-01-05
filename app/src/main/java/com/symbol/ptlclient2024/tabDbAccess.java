package com.symbol.ptlclient2024;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.ptlclient2024.SQL.ConnectionToDB;
import com.symbol.ptlclient2024.SQL.FIFO;
import com.symbol.ptlclient2024.SQL.GetData;
import com.symbol.ptlclient2024.SQL.WarehouseDB;
import com.symbol.ptlclient2024.auxx.Settings;
import com.symbol.ptlclient2024.custom_components.PalletAdapter2;
import com.symbol.ptlclient2024.custom_components.RecycleViewInterface;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class tabDbAccess extends Fragment implements RecycleViewInterface {

    public final Activity _ParentActivity;
    private static androidx.recyclerview.widget.RecyclerView RecycleView1;
    //private DbActivity dbActivity;
    Connection con;
    String str;
    ArrayList<WarehouseDB> pallets;

    PalletAdapter2 adapter;

    ArrayList<Integer> removedPositions;

    private ArrayList<Integer> updatedPositions;

    HorizontalNumberPicker hnpR = null;
    HorizontalNumberPicker hnpS = null;

    Button btnSave;
    Button btnClear;

    private  void buttonsEnable(boolean pParam){
            this.btnClear.setEnabled(pParam);
            this.btnSave.setEnabled(pParam);
    }

    public void removeSelectedViews(){

        //RecycleView1.

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         //Connect(this._ParentActivity);
        this.removedPositions = new ArrayList<Integer>();

        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_tab_db_access, container, false);

         //HorizontalNumberPicker
         hnpR = view.findViewById(R.id.hnpR);
         hnpR.setMin(1);
         hnpR.setValue(1);

         hnpS = view.findViewById(R.id.hnpSPos);
         hnpS.setMin(1);
         hnpS.setValue(1);

         hnpR.setTextColor(getResources().getColor(R.color.green,null));
         hnpS.setTextColor(getResources().getColor(R.color.green,null));

         hnpR.setMax(Settings.getSELF().MaxY);
         hnpS.setMax(Settings.getSELF().MaxX);

         hnpR.setRotate(true);
         hnpS.setRotate(true);

         //btnSave.setVisibility(View.VISIBLE);

        TextWatcher twR = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hnpR.setTextColor(getResources().getColor(R.color.design_default_color_on_primary,null));
            }
            @Override
            public void afterTextChanged(Editable editable){}
        };
        TextWatcher twS = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hnpS.setTextColor(getResources().getColor(R.color.design_default_color_on_primary,null));
            }
            @Override
            public void afterTextChanged(Editable editable){}
        };

        hnpR.getEtxValue().addTextChangedListener(twR);
        hnpS.getEtxValue().addTextChangedListener(twS);


         btnClear = view.findViewById(R.id.btnClearDB);
         Button btnShow = view.findViewById(R.id.btnShowPos);
         btnSave = view.findViewById(R.id.btnSaveDB);


         btnClear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 //RecycleView1.removeAllViewsInLayout();
                 onClearClicked(null);
             }
         });

         btnSave.setOnClickListener(new View.OnClickListener() {

//             for (int i = 0; i < tbDBRowpn.Count; i++)
//             {
//
//                 if (tbDBRowcb[i].Checked)
//                 {
//                     sqldb.updateRecordByPalletNr(gridData[i].palletNr, tbDBRowpn[i].Text, tbDBRowdt[i].Text);
//                     FS.logData("updateRecord(" + gridData[i].palletNr + "," + tbDBRowpn[i].Text + "," + tbDBRowdt[i].Text + ")");
//                     tbDBRowcb[i].Checked = false;
//
//                 }
//             }
//
//             btnShow_Click(null, null);




             @Override
             public void onClick(View view) {

                 //sqldb.updateRecordByPalletNr(gridData[i].palletNr, tbDBRowpn[i].Text, tbDBRowdt[i].Text);
                 //FS.logData("updateRecord(" + gridData[i].palletNr + "," + tbDBRowpn[i].Text + "," + tbDBRowdt[i].Text + ")");
                 updatePallets(view);
                 ShowPallets(view,false);
             }
         });


         btnShow = view.findViewById(R.id.btnShowPos);
         btnShow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 ShowPallets(view, true);
             }
         });


         //RecycleView
         this.RecycleView1 = (RecyclerView) view.findViewById(R.id.recvDbAccess);
         this.RecycleView1.setItemViewCacheSize(100);
         RecyclerView.LayoutManager LM = new LinearLayoutManager(getContext());
         this.RecycleView1.setLayoutManager(LM);

          pallets = new ArrayList<WarehouseDB>();

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            //LocalDateTime dt1 = LocalDateTime.now();
//            //Timestamp  dt1 =     LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth(),LocalDateTime.now().getDayOfMonth(),LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),LocalDateTime.now().getSecond(),0);
//            Timestamp dt1 = new Timestamp(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth().getValue(),LocalDateTime.now().getDayOfMonth(),LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),LocalDateTime.now().getSecond(),0);
//            //LocalDateTime dt2 = LocalDateTime.of(2024,06,06,12,12,12,0);
//            Timestamp dt2 = new Timestamp(2024,06,06,12,12,12,0);
//            //LocalDateTime dt3 = LocalDateTime.of(2024,07,07,07,07,07,0);
//            Timestamp dt3 = new Timestamp(2024,07,07,07,07,07,0);
//         }

        //ZISKAVANIE DAT ZO VZDIALENJ DB NA UVODNY VYOIS VSETKYCH DAT
//        if (GetData.DB.CONN()) {
//                    pallets = GetData.getAllPallets(_ParentActivity);
//                    this.adapter = new PalletAdapter2(pallets,tabDbAccess.this);
//                    this.RecycleView1.setAdapter(adapter);
//        }

        //TABLEVIEW komponent
//        TableView tbvPallets = view.findViewById(R.id.tbvPallets);
//        String[] headers = {"ID","Pallet nb","DateTime","Remove"};
//        String[][]  data = {{"1","1234567890","2024-06-28","true"},{"2","2345678901","2024-06-28","false"}, {"3","3456789012","2024-06-28", "false"}};
//        tbvPallets.setHeaderAdapter(new SimpleTableHeaderAdapter(view.getContext(),headers));
//        tbvPallets.setHeaderAdapter(new SimpleTableHeaderAdapter(view.getContext(), Arrays.toString(data)));
        return view;
    }


    public void Connect(Activity pActivity){

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            //try {
            //DB CONNECTION
            ConnectionToDB connectionToDB = new ConnectionToDB();
                boolean isDBConnected = connectionToDB.CONN();
                str = connectionToDB.ConnectionResult;

                    //Statement stmt = con.createStatement();
                    //String query  = "SELECT * FROM svetla";
                    //stmt.execute(query);
                    //connectionClass.stmt.execute(query);
                    //str = "SELECT executed successfully";
//            }
//            catch (SQLException e) {
//                Helpers.redToast(pActivity, "SQL Exception: " + e.getMessage());
//                return;
//            }
                //oneskoreny vypis do GUI
                //GUI pocas danej doby ZAMRZNUTE
                    pActivity.runOnUiThread(()->{
                    try
                    {
                      Thread.sleep(1000);
                    }
                    catch (InterruptedException e){
                        //throw new RuntimeException(e);
                        Helpers.redToast(pActivity, "Interupted Exception: "+ e.getMessage());
                        return;
                    }
                    if (isDBConnected)
                        //Toast.makeText(this.getContext(), str, Toast.LENGTH_SHORT).show();
                        Toast.makeText(pActivity,str,Toast.LENGTH_LONG).show();
                            else
                                Helpers.redToast(pActivity, str);
            });
        });
    }

    //#############################################################################################################################################
    //#############################################################################################################################################
    //#############################################################################################################################################
    private void ShowPallets(View view, boolean pEnabled){
        //gridData.Clear();
        if (GetData.DB.CONN()) {
            RecycleView1.removeAllViewsInLayout();
            //int x = (int)nudX.Value, y = (int)nudY.Value, p = 0;
            Integer x = hnpR.getValue(), y = hnpS.getValue(), p = 0;
            pallets = GetData.getPosWarehouse(_ParentActivity,x, y);
            //if (id == null)
            if (pallets == null) return;
            if (pallets.isEmpty()) {
                Helpers.redToast(getActivity(), "no pallet on position");
                this.buttonsEnable(false);
                return;
            }
            //if (GetData.DB.CONN())
            {
                FIFO fifo = GetData.getPosFIFO(_ParentActivity,x, y);
                if (fifo == null || fifo.getID() == 0) {
                    Helpers.redToast(getContext(), "no data for pos");
                    this.buttonsEnable(false);
                    return;
                }

                try {
                    //p = Convert.ToInt32(pos[2].ToString());
                    //pos[2] - zeby hodonta v tretom stlpci co je "pack"
                    p = fifo.getPack();
                } catch (Exception e) {
                    p = 1;
                }
                if (p == 0) p = 1;
            }

            adapter = new PalletAdapter2(pallets,tabDbAccess.this);
            RecycleView1.setAdapter(adapter);
            this.buttonsEnable(true);
            if (!pEnabled)
                //vypis po uspesnom UDATE (alebo DELETE)
                this.buttonsEnable(false);
        }
        else {

            Helpers.redToast(getActivity(), "Connection to DB FAILED!");
            this.buttonsEnable(false);
        }
    }
    //#############################################################################################################################################
    //#############################################################################################################################################
    //#############################################################################################################################################


    public tabDbAccess(Activity pParentActivity) {
        // Required empty public constructor
        this._ParentActivity = pParentActivity;
    }

    //basicaly anything you want to have happen when the user clicks or holds on one of the item
    @Override
    public void onItemCheckBoxChecked(int pPosition) {

        removedPositions.add(pPosition);
    }

    @Override
    public void onItemCheckBoxUnchecked(int position) {

        int remIndex = removedPositions.lastIndexOf(position);
        removedPositions.remove(remIndex);

    }

    //basicaly anything you want to have happen when the user clicks or holds on one of the item
    @Override
    public void onItemLongClick(int position) {

        //ZISKAVANIE DAT ZO VZDIALENJ DB
        if (GetData.DB.CONN()) {
//            pallets = GetData.getAllPallets(_ParentActivity);
//            this.adapter = new PalletAdapter2(pallets,tabDbAccess.this);
//            this.RecycleView1.setAdapter(adapter);
            WarehouseDB DeletedPallet = pallets.get(position);
            int deletedCount = GetData.deletePallet(_ParentActivity,DeletedPallet.getPalleteNr());
            if (deletedCount > 0 )
                pallets.remove(position);


            adapter.notifyItemRemoved(position);
        }
    }

    public void onClearClicked(View view){

//        for (int i = 0; i < tbDBRowpn.Count; i++)
//        {
//
//            if (tbDBRowcb[i].Checked)
//            {
//                sqldb.clearRecord(gridData[i].palletNr);
//                FS.logData("clearRecord(" + gridData[i].palletNr + ")");
//                tbDBRowcb[i].Checked = false;
//
//            }
//        }
//        btnShow_Click(null, null);

        int deletedRecords = 0;
        this.removedPositions.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return integer - t1;
            }
        });

        //////////////////////////////////////////////////////////////////////////
        ArrayList<WarehouseDB> deletedPallets = new ArrayList<>();
        removedPositions.forEach((index)-> deletedPallets.add(pallets.get(index)));
        if(deletedPallets.size() == 0)
        {
            Helpers.redToast(this._ParentActivity, "NO pallets for deleting!");
            this.buttonsEnable(true);
            this.removedPositions.clear();
            this.buttonsEnable(true);
            return;
        }

        try {
            if (GetData.DB.CONN()) {

                int recordsCount = GetData.deletePallets(_ParentActivity, deletedPallets);
                if (recordsCount != 0) {
                    Toast.makeText(this._ParentActivity, "Successfully deleted records: " + recordsCount, Toast.LENGTH_LONG).show();
                    this.buttonsEnable(false);
                }
            }
            else {
                Helpers.redToast(this._ParentActivity, "Can not connect to DB");
                this.buttonsEnable(true);
                return;
            }
        }
        catch (Exception ex){
            Helpers.redToast(this._ParentActivity, "Deleting FAILED: " + ex.getMessage());
            this.buttonsEnable(false);
            return;
        }

        //////////////////////////////////////////////////////////////////////////
        //pallets.removeAll(this.removedPositions);
        int i = 0;
        for (int position:removedPositions) {
            //adapter.removeItem(position-i);
            pallets.remove(position-i);
            adapter.notifyItemRemoved(position-i);
            i++;
        }
        this.removedPositions.clear();

    }

    public void updatePallets(View view){

        //pallets - sktruktura ktorej obsah sa zobrazuje v RecycleView
        updatedPositions = removedPositions;
        removedPositions =  reverseINT(updatedPositions, pallets.size()-1);

        this.removedPositions.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return integer - t1;
            }
        });

        //////////////////////////////////////////////////////////////////////////
        ArrayList<WarehouseDB> deletedPallets = new ArrayList<>();
        ArrayList<WarehouseDB> updatedPallets = new ArrayList<>();
        removedPositions.forEach((index)-> deletedPallets.add(pallets.get(index)));
        updatedPositions.forEach((index)-> updatedPallets.add(pallets.get(index)));
        if(updatedPallets.size() == 0)
        {
            Helpers.redToast(this._ParentActivity, "NO pallets for UPDATE!");
            this.pallets.clear();
            this.removedPositions.clear();
            this.buttonsEnable(true);
            return;
        }

        try {
            if (GetData.DB.CONN()) {

                //int recordsCount = GetData.deletePallets(_ParentActivity, deletedPallets);
                int recordsCount = GetData.updatePallets(_ParentActivity, updatedPallets, hnpR.getValue(),hnpS.getValue());
                if (recordsCount != 0) {
                    Toast.makeText(this._ParentActivity, "Successfully UPDATED records: " + recordsCount, Toast.LENGTH_LONG).show();
                    this.buttonsEnable(false);
                }
            }
            else {
                Helpers.redToast(this._ParentActivity, "Can not connect to DB");
                this.buttonsEnable(true);
                //return;
            }
        }
        catch (Exception ex){
            Helpers.redToast(this._ParentActivity, "Updating FAILED: " + ex.getMessage());
            this.buttonsEnable(false);
            //return;
        }

        //////////////////////////////////////////////////////////////////////////
        //pallets - sktruktura ktorej obsah sa zobrazuje v RecycleView
//        pallets.removeAll(this.removedPositions);
//        int i = 0;
//        for (int position:removedPositions) {
//            adapter.notifyItemRemoved(position-i);
//            i++;
//        }
        this.pallets.clear();
        this.removedPositions.clear();
    }

    private ArrayList<Integer> reverseINT(ArrayList<Integer> pArrayList, int pMax) {

        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i =  0; i<=pMax; i++){
            if (pArrayList.contains(i))  continue;
            else
                result.add(i);
        }

        return result;
    }

}
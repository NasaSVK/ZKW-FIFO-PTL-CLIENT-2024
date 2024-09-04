package com.symbol.kepzetclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.kepzetclient.SQL.ConnectionToDB;
import com.symbol.kepzetclient.SQL.FIFO;
import com.symbol.kepzetclient.SQL.GetData;
import com.symbol.kepzetclient.SQL.WarehouseDB;
import com.symbol.kepzetclient.custom_components.PalletAdapter2;
import com.symbol.kepzetclient.custom_components.RecycleViewInterface;

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
         HorizontalNumberPicker hnpR = view.findViewById(R.id.hnpR);
         hnpR.setMin(0); hnpR.setMax(99);
         hnpR.setValue(1);
         HorizontalNumberPicker hnpS = view.findViewById(R.id.hnpSPos);
         hnpS.setMin(0); hnpS.setMax(99);
         hnpS.setValue(48);

         Button btnClear = view.findViewById(R.id.btnClearDB);
         Button btnShow = view.findViewById(R.id.btnShowPos);
         int nbpX = hnpR.getValue();
         int nbpY = hnpS.getValue();

         btnClear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 //RecycleView1.removeAllViewsInLayout();
                 onClearClicked(null);
             }
         });


         btnShow = view.findViewById(R.id.btnShowPos);
         btnShow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //gridData.Clear();
                 if (GetData.DB.CONN()) {
                     RecycleView1.removeAllViewsInLayout();
                     //int x = (int)nudX.Value, y = (int)nudY.Value, p = 0;
                     Integer x = hnpR.getValue(), y = hnpS.getValue(), p = 0;
                     //object[] id = sqldb.getPos("id", x.ToString(), y.ToString());
                     /*ArrayList<WarehouseDB>*/ pallets = GetData.getPosWarehouse(_ParentActivity,x, y);
                     //if (id == null)
                     if (pallets == null) return;
                     if (pallets.isEmpty()) {
                         Helpers.redToast(getActivity(), "no pallet on position");
                         return;
                     }

                     //object[] palettNr = sqldb.getPos("paletteNr", x.ToString(), y.ToString());
                     ///object[] dt = sqldb.getPos("FIFODatetime", x.ToString(), y.ToString());
                     //if (GetData.DB.CONN())
                     {
                         FIFO fifo = GetData.getPosFIFO(_ParentActivity,x, y);
                             if (fifo == null || fifo.getID() == 0) {
                             Helpers.redToast(getContext(), "no data for pos");
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
//                     else {
//                         Helpers.redToast(getActivity(), "Connection to DB FAILED!");
//                         return;
//                     }

                     adapter = new PalletAdapter2(pallets,tabDbAccess.this);
                     RecycleView1.setAdapter(adapter);

                 }
                 else {
                     Helpers.redToast(getActivity(), "Connection to DB FAILED!");
                     return;
                 }

             }

         });


         //RecycleView
         this.RecycleView1 = (RecyclerView) view.findViewById(R.id.recvDbAccess);
         this.RecycleView1.setItemViewCacheSize(100);
         RecyclerView.LayoutManager LM = new LinearLayoutManager(getContext());
         this.RecycleView1.setLayoutManager(LM);

          pallets = new ArrayList<WarehouseDB>();
         //ArrayList<Pallet> pallets = new ArrayList<Pallet>();
//            Pallet novaPaleta1 = new Pallet();
//            Pallet novaPaleta2 = new Pallet();
//            Pallet novaPaleta3 = new Pallet();
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            //LocalDateTime dt1 = LocalDateTime.now();
//            //Timestamp  dt1 =     LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth(),LocalDateTime.now().getDayOfMonth(),LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),LocalDateTime.now().getSecond(),0);
//            Timestamp dt1 = new Timestamp(LocalDateTime.now().getYear(),LocalDateTime.now().getMonth().getValue(),LocalDateTime.now().getDayOfMonth(),LocalDateTime.now().getHour(),LocalDateTime.now().getMinute(),LocalDateTime.now().getSecond(),0);
//            //LocalDateTime dt2 = LocalDateTime.of(2024,06,06,12,12,12,0);
//            Timestamp dt2 = new Timestamp(2024,06,06,12,12,12,0);
//            //LocalDateTime dt3 = LocalDateTime.of(2024,07,07,07,07,07,0);
//            Timestamp dt3 = new Timestamp(2024,07,07,07,07,07,0);
//
//           novaPaleta1.setPalletNumber("11111111");
//           novaPaleta1.setDateTime(dt1);
//           novaPaleta1.setActive(true);
//
//            novaPaleta2.setPalletNumber("22222222");
//            novaPaleta2.setDateTime(dt2);
//            novaPaleta2.setActive(false);
//
//            novaPaleta3.setPalletNumber("33333333");
//            novaPaleta3.setDateTime(dt3);
//            novaPaleta3.setActive(true);
//         }
//         pallets.add(novaPaleta1);
//         pallets.add(novaPaleta2);
//         pallets.add(novaPaleta3);
//
//
//        this.RecycleView1.setAdapter(new PalletAdapter(pallets));


        //ZISKAVANIE DAT ZO VZDIALENJ DB
        if (GetData.DB.CONN()) {
                    pallets = GetData.getAllPallets(_ParentActivity);
                    this.adapter = new PalletAdapter2(pallets,tabDbAccess.this);
                    this.RecycleView1.setAdapter(adapter);
        }



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

       pallets.remove(position);
       adapter.notifyItemRemoved(position);

    }

    public void onClearClicked(View view){
        int deletedRecords = 0;
        this.removedPositions.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return integer - t1;
            }
        });
        pallets.removeAll(this.removedPositions);
        int i = 0;
        for (int position:removedPositions) {
            adapter.notifyItemRemoved(position-i);
            i++;
        }
        this.removedPositions.clear();

        if (GetData.DB.CONN()) {
            //deletedRecords = GetData.deletePallets(this._ParentActivity,removedPositions);
        }

        Helpers.redToast(this._ParentActivity,"Successfully deleted records:"+ deletedRecords);

    }
}
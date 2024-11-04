package com.symbol.kepzetclient;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.symbol.kepzetclient.SQL.GetData;

import com.symbol.kepzetclient.auxx.PalletPlus;
import com.symbol.kepzetclient.databinding.FragmentTabSearchBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tabSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tabSearch extends Fragment implements  View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static Activity _ParentActivity;
    //View Binding in Android : Activity, Fragment
    //https://medium.com/@shobhith/view-binding-in-android-activity-fragment-3d8ed995e276
    private  FragmentTabSearchBinding _tabSearchBinding = null;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _tabSearchBinding = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //  View Binding in Android : Activity, Fragment
        //  https://medium.com/@shobhith/view-binding-in-android-activity-fragment-3d8ed995e276
        _tabSearchBinding = FragmentTabSearchBinding.inflate(inflater, container, false);
        ConstraintLayout view = _tabSearchBinding.getRoot();
        _tabSearchBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SearchPallet(view);
            }
        });

        return view;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_tab_search, container, false);
    }

    public tabSearch(Activity pParentActivity) {
        // Required empty public constructor
        this._ParentActivity = pParentActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tabSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static tabSearch newInstance(String param1, String param2) {
        tabSearch fragment = new tabSearch(_ParentActivity);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    public void btnClose_onClickHandler(View view) {

    }

    @Override
    public void onClick(View view) {
        String palletNumber  = _tabSearchBinding.etxPalletNumberSrch.getText().toString();

        //tbX.Text = "";
        _tabSearchBinding.etxRSrch.setText("");
        //tbY.Text = "";
        _tabSearchBinding.etxSSrch.setText("");
        //tbPN.Text = "";
        _tabSearchBinding.etxPartNumberSrch.setText("");
        //tbChannel.Text = "";
        _tabSearchBinding.etxChannelSrch.setText("");
        //tbPack.Text = "";
        _tabSearchBinding.etxPackSrch.setText("");
        //tbZ.Text = "";
        _tabSearchBinding.etxZSrch.setText("");

//        if (tbPalNr.Text.Length == 7)
//            tbPalNr.Text = "0" + tbPalNr.Text;
        if (palletNumber.length() == 7){
            _tabSearchBinding.etxPalletNumberSrch.setText("0" + palletNumber);
            palletNumber = ("0" + palletNumber);
        }

        try
        {
            //Int64 i = Convert.ToInt64(tbPalNr.Text);
            Long i = Long.parseLong(palletNumber);

//            if (tbPalNr.Text.Length != 10 && tbPalNr.Text.Length != 8) {
//                MessageBox.Show("the format of pallet number not valid");
//                FS.logData("btnSrch error:" + tbPalNr.Text + " the format of pallet number not valid");
//                return;
//            }
            if (palletNumber.length() != 10 && palletNumber.length() != 8) {
                Helpers.redToast(getContext(),"the format of pallet number not valid");
                ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH: btnSrch error:" + palletNumber + " the format of pallet number not valid");
                return;
            }
        }
        catch(Exception ex)
        {
//            MessageBox.Show("not a number");
//            FS.logData("btnSrch error:" + tbPalNr.Text + " is not a number");
//            return;
            Helpers.redToast(getContext(),"not a number");
            ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH: btnSrch error:" + palletNumber + " is not a number");
            return;
        }

        try
        {

            PalletPlus palletPlus = GetData.getPalletPlus(_ParentActivity, palletNumber);

//            if (o[0] == null)
//            {
//                MessageBox.Show("record not found");
//                FS.logData("data not found:" + tbPalNr.Text);
//                return;
//            }

            if (palletPlus == null)
            {
                //MessageBox.Show("record not found");
                Helpers.redToast(getContext(), "record not found");
                ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH: data not found for:" + palletPlus);
                return;
            }

//            int row = Convert.ToInt32(o[2]), pack = Convert.ToInt32(o[5]), pos = 0;
//            tbX.Text = o[0].ToString();
//            tbY.Text = o[1].ToString();
//            tbPN.Text = o[3].ToString();
//            tbChannel.Text = o[4].ToString();
//            tbPack.Text = pack.ToString();
//            if (pack > 1)
//            {
//                pos = ((row - 1) / pack) + 1;
//            }
//            else
//                pos = row;
//            tbZ.Text = pos.ToString();

            int row = palletPlus.Z;
            int pack = palletPlus.pack;
            int pos = 0;

            _tabSearchBinding.etxRSrch.setText(palletPlus.X.toString());
            _tabSearchBinding.etxSSrch.setText(palletPlus.Y.toString());
            _tabSearchBinding.etxPartNumberSrch.setText(palletPlus.partNr.toString());
            _tabSearchBinding.etxChannelSrch.setText(palletPlus.chnl.toString());

            _tabSearchBinding.etxPackSrch.setText(String.valueOf(pack));
            if (pack > 1)
            {
                pos = ((row - 1) / pack) + 1;
            }
            else
                pos = row;
            _tabSearchBinding.etxZSrch.setText(String.valueOf(pos));

        }
        catch (Exception ex)
        {
            ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH:  searchRecord(" + palletNumber + ")" + ex.getMessage());
        }

    }

    public void SearchPallet(View view) {

        _tabSearchBinding.etxRSrch.setText(""); //X
        _tabSearchBinding.etxSSrch.setText(""); //Y
        _tabSearchBinding.etxZSrch.setText(""); //Z
        _tabSearchBinding.etxPartNumberSrch.setText(""); //PartNumber
        _tabSearchBinding.etxChannelSrch.setText(""); //Channel
        _tabSearchBinding.etxPackSrch.setText(""); //Pack

        if (_tabSearchBinding.etxPalletNumberSrch.getText().length() == 7)
            _tabSearchBinding.etxPalletNumberSrch.setText("0" +  _tabSearchBinding.etxPalletNumberSrch.getText().toString());
        try
        {
            Long i = Long.valueOf(_tabSearchBinding.etxPalletNumberSrch.getText().toString());
            if (_tabSearchBinding.etxPalletNumberSrch.getText().length() != 10 && _tabSearchBinding.etxPalletNumberSrch.getText().length() != 8) {
                Helpers.redToast(_ParentActivity,"the format of pallet number not valid");
                ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH:" + _tabSearchBinding.etxPalletNumberSrch.getText() + " the format of pallet number not valid");
                return;
            }
        }
        catch(Exception ex)
        {
            Helpers.redToast(_ParentActivity,"not a number");
            ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH:" + _tabSearchBinding.etxPalletNumberSrch.getText().length() + " is not a number");
            return;
        }

        try
        {
            PalletPlus o =  GetData.getPalletPlus(_ParentActivity, _tabSearchBinding.etxPalletNumberSrch.getText().toString());
            if (o.X == null)
            {
                Helpers.redToast(_ParentActivity,"record not found");
                ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH: data not found:" + _tabSearchBinding.etxPalletNumberSrch.getText().toString());
                return;
            }
            int row = o.Z;
            int pack = o.pack;
            int pos = 0;
            _tabSearchBinding.etxRSrch.setText(o.X.toString());
            _tabSearchBinding.etxSSrch.setText(o.Y.toString());
            _tabSearchBinding.etxPartNumberSrch.setText(o.partNr.toString());
            _tabSearchBinding.etxChannelSrch.setText(o.chnl.toString());
            _tabSearchBinding.etxPackSrch.setText(o.pack.toString());
            if (pack > 1)
            {
                pos = ((row - 1) / pack) + 1;
            }
            else
                pos = row;
            _tabSearchBinding.etxZSrch.setText(String.valueOf(pos));
        }
        catch (Exception ex)
        {
            ((MainActivity)MainActivity.getContext()).logStream.logData("DB SEARCH: searchRecord("+_tabSearchBinding.etxPalletNumberSrch.getText().toString()+")" + ex.getMessage());
        }
    }

}
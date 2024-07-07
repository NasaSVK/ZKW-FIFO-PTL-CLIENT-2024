package com.symbol.kepzetclient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class tabDbAccess extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_tab_db_access, container, false);

         HorizontalNumberPicker hnpR = view.findViewById(R.id.hnpR);
         hnpR.setMin(0); hnpR.setMax(99);

         HorizontalNumberPicker hnpS = view.findViewById(R.id.hnpS);
         hnpS.setMin(0); hnpS.setMax(99);

//        TableView tbvPallets = view.findViewById(R.id.tbvPallets);
//        String[] headers = {"ID","Pallet nb","DateTime","Remove"};
//        String[][]  data = {{"1","1234567890","2024-06-28","true"},{"2","2345678901","2024-06-28","false"}, {"3","3456789012","2024-06-28", "false"}};
//        tbvPallets.setHeaderAdapter(new SimpleTableHeaderAdapter(view.getContext(),headers));
//        tbvPallets.setHeaderAdapter(new SimpleTableHeaderAdapter(view.getContext(), Arrays.toString(data)));
        return view;
    }
    public tabDbAccess() {
        // Required empty public constructor
    }


}
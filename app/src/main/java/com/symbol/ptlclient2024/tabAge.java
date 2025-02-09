package com.symbol.ptlclient2024;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.symbol.ptlclient2024.SQL.GetData;
import com.symbol.ptlclient2024.auxx.Settings;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tabAge#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tabAge extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Activity _ParentActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public tabAge(Activity pParentActivity) {
        // Required empty public constructor
        this._ParentActivity = pParentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_age, container, false);
        Button button = (Button) view.findViewById(R.id.btnCloseSearch);
        Button btnUpdateCounts = (Button) view.findViewById(R.id.btnUpdateAge);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnUpdateCounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int MaxY = Settings.getSELF().MaxY;
                int MaxX = Settings.getSELF().MaxX;

                if (GetData.DB.CONN()) {

                    int updatedCounts = GetData.updateCount(_ParentActivity,MaxY, MaxX);
                    Toast.makeText(_ParentActivity,"Updated counts:" + updatedCounts,Toast.LENGTH_LONG).show();

                }
                else {
                    Helpers.redToast(getActivity(), "Connection to DB FAILED!");
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static tabAge newInstance(String param1, String param2) {
        tabAge fragment = new tabAge(_ParentActivity);
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

//    public void close(View view) {
//        getActivity().onBackPressed();
//    }


}
package com.symbol.kepzetclient;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.symbol.kepzetclient.SQL.FIFO;
import com.symbol.kepzetclient.SQL.GetData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tabPosition#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tabPosition extends Fragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static Activity _ParentActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button btnShow = null;
    Button btnSave = null;
    EditText etxPartNr = null;
    HorizontalNumberPicker hnpRPos,hnpSPos= null;
    EditText etxChnl = null, etxPack = null, etxCount = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
       View  view = inflater.inflate(R.layout.fragment_tab_position, container, false);

        btnShow = (Button)view.findViewById(R.id.btnShowPos);
        btnSave = (Button)view.findViewById(R.id.btnSavePos);

        hnpRPos = (HorizontalNumberPicker)view.findViewById(R.id.hnpRPos);
        hnpSPos = (HorizontalNumberPicker)view.findViewById(R.id.hnpSPos);

        hnpRPos.setValue(3); hnpRPos.setMin(1); hnpRPos.setMax(100);
        hnpSPos.setValue(48); hnpSPos.setMin(1); hnpSPos.setMax(100);

        TextWatcher twR = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hnpRPos.setTextColor(getResources().getColor(R.color.design_default_color_on_primary,null));
            }
            @Override
            public void afterTextChanged(Editable editable){}
        };
        TextWatcher twS = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hnpSPos.setTextColor(getResources().getColor(R.color.design_default_color_on_primary,null));
            }
            @Override
            public void afterTextChanged(Editable editable){}
        };

        hnpSPos.getEtxValue().addTextChangedListener(twS);
        hnpRPos.getEtxValue().addTextChangedListener(twR);


        etxChnl = (EditText) view.findViewById(R.id.etxChannelPos);
        etxPack = (EditText) view.findViewById(R.id.etxPackPos);
        etxCount = (EditText) view.findViewById(R.id.etxCountPos);

        etxPartNr = (EditText) view.findViewById(R.id.etxPartNumberPos);


        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int x = (int)nudXP.Value, y = (int)nudYP.Value, p = 0, count = 0;
                Integer x = hnpRPos.getValue(), y = hnpSPos.getValue(), p = 0, count = 0;
//               object[] pos = sqldb.getPos(x.ToString(), y.ToString());
                FIFO fifo = GetData.getPosFIFO(_ParentActivity, x, y);

//                if (pos == null || pos[0] == null)
//                {
//                    MessageBox.Show("no data for pos");
//                    return;
//                }
                if (fifo == null || fifo.getID() <= 0)
                {
                    //MessageBox.Show("no data for pos");
                    Helpers.redToast(_ParentActivity,"no data for pos");
                    return;
                }
//                tbDBPn.Text = pos[0].ToString().Trim();
//                tbDBChannel.Text = pos[1].ToString();
//                tbDBPack.Text = pos[2].ToString();
                etxPartNr.setText(fifo.getPartNr());
                etxChnl.setText(String.valueOf(fifo.getChannel()));
                etxPack.setText(String.valueOf(fifo.getPack()));

//              https://www.youtube.com/watch?v=1IIKac0RPKY

                //cbAgeCheck.Checked = Convert.ToBoolean(pos[4]);
                //tbAgeLimit.Text = pos[3].ToString();
                //tbAgeBypassTime.Text = pos[5].ToString();


                try
                {
                    p = Integer.parseInt(etxPack.getText().toString());
                    count = Integer.parseInt(etxCount.getText().toString());
                }

                catch(NumberFormatException  ex)
                {
                    p = 1;
                    count = 6;
                }
                if (p == 0)
                    p = 1;

//                tbDBCount.Text = (count / p).ToString();
//                nudXP.ForeColor = System.Drawing.SystemColors.WindowText;
//                nudYP.ForeColor = System.Drawing.SystemColors.WindowText;
//                btnSaveP.Visible = true;
                etxCount.setText(String.valueOf(count / p));
                //hnpRPos.setForeground (new ColorDrawable(getResources().getColor(R.color.green,null)));
                //hnpSPos.setForeground (new ColorDrawable(getResources().getColor(R.color.primary,null)));
                hnpSPos.setTextColor(getResources().getColor(R.color.green,null));
                hnpRPos.setTextColor(getResources().getColor(R.color.green,null));
                //hnpRPos.setTextColor(getResources().getColor(R.color.primary,null));
                btnSave.setVisibility(View.VISIBLE);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FIFO newFIFO = new FIFO();
                Boolean result = false;
                //if (nudXP.ForeColor == SystemColors.WindowText && nudYP.ForeColor == SystemColors.WindowText)
                if (hnpRPos.getTexColor() == getResources().getColor(R.color.green,null)
                        && hnpSPos.getTexColor() == getResources().getColor(R.color.green,null))
                {
                    try
                    {
                        int p = Integer.parseInt(etxPack.getText().toString());
                        int count = Integer.parseInt(etxCount.getText().toString());
                        newFIFO.setPosX(hnpRPos.getValue());
                        newFIFO.setPosY(hnpSPos.getValue());
                        newFIFO.setPartNr(etxPartNr.getText().toString());
                        newFIFO.setChannel(Integer.parseInt(etxChnl.getText().toString()));
                        newFIFO.setPack(Integer.parseInt(etxPack.getText().toString()));
                        newFIFO.setMaxCount(count*p);

                        result = GetData.updatePosByPos(getContext(),newFIFO);
                    }
                    catch (Exception ex)
                    {
                        //MessageBox.Show("save Failed!\nukladanie zlyhalo\n" + ex.ToString());
                         Helpers.redToast(getContext(), "SAVE FAILED!\n\nreason: " + ex.getMessage());
                    }
                }
                else
                {
                    Helpers.redToast(getContext(), "Position changed, can not save!\nPozícia zmenená, zmeny sa neuložia\n");
                }
                Toast.makeText(_ParentActivity,"Selelected FIFO updated successfuly!", Toast.LENGTH_SHORT);
            }

        });

        return  view;
    }

    public tabPosition(Activity pParentActivity) {
        // Required empty public constructor
        this._ParentActivity = pParentActivity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tabPosition.
     */
    // TODO: Rename and change types and number of parameters
    public static tabPosition newInstance(String param1, String param2) {
        tabPosition fragment = new tabPosition(_ParentActivity);
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
}
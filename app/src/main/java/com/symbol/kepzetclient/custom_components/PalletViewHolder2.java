package com.symbol.kepzetclient.custom_components;

import static com.symbol.kepzetclient.Helpers.timestampAsString;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.kepzetclient.R;
import com.symbol.kepzetclient.SQL.WarehouseDB;

public class PalletViewHolder2 extends RecyclerView.ViewHolder {
    private TextView tvPalletNumber;
    private TextView tvDateTime;
    private CheckBox cbActive;

    public PalletViewHolder2(@NonNull View itemView) {

        super(itemView);
        tvPalletNumber = (TextView) itemView.findViewById(R.id.tvPalletNumber);
        tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
        cbActive = (CheckBox)  itemView.findViewById(R.id.cbActive);

    }

    public void setPalletData(WarehouseDB pallet) {
        tvPalletNumber.setText(pallet.getPalleteNr());
        tvDateTime.setText(timestampAsString(pallet.getFIFODateTime()));
        cbActive.setChecked(true);
    }
}


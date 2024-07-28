package com.symbol.kepzetclient.custom_components;

import static com.symbol.kepzetclient.Helpers.timestampAsString;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.kepzetclient.R;
import com.symbol.kepzetclient.SQL.Pallet;

public class PalletViewHolder extends RecyclerView.ViewHolder {
    private TextView tvPalletNumber;
    private TextView tvDateTime;
    private CheckBox cbActive;

    public PalletViewHolder(@NonNull View itemView) {

        super(itemView);
        tvPalletNumber = (TextView) itemView.findViewById(R.id.tvPalletNumber);
        tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
        cbActive = (CheckBox)  itemView.findViewById(R.id.cbActive);

    }

    public void setPalletData(Pallet pallet) {
        tvPalletNumber.setText(pallet.getPalletNumber());
        tvDateTime.setText(timestampAsString(pallet.getDateTime()));
        cbActive.setChecked(pallet.getActive());
    }
}

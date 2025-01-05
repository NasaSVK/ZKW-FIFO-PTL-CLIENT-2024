package com.symbol.ptlclient2024.custom_components;

import static com.symbol.ptlclient2024.Helpers.timestampAsString;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.ptlclient2024.R;
import com.symbol.ptlclient2024.SQL.Pallet;

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

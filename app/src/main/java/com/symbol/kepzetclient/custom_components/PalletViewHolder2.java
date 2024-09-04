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

    private final RecycleViewInterface recyclerViewInterface;

    public PalletViewHolder2(@NonNull View itemView, RecycleViewInterface pRecyclerViewInterface) {

        super(itemView);
        tvPalletNumber = (TextView) itemView.findViewById(R.id.tvPalletNumber);
        tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
        cbActive = (CheckBox)  itemView.findViewById(R.id.cbActive);
        recyclerViewInterface = pRecyclerViewInterface;

        cbActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()){
                    if (recyclerViewInterface != null){
                        int pos  = getAdapterPosition(); //:X :X :X :X :X
                        if (pos != RecyclerView.NO_POSITION) //:X :X :X :X :X
                            recyclerViewInterface.onItemCheckBoxChecked(pos);
                    }
                }
                else
                if (!((CheckBox)view).isChecked()){
                    if (recyclerViewInterface != null){
                        int pos  = getAdapterPosition(); //:X :X :X :X :X
                        if (pos != RecyclerView.NO_POSITION) //:X :X :X :X :X
                            recyclerViewInterface.onItemCheckBoxUnchecked(pos);
                    }
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (recyclerViewInterface != null){
                    int pos  = getAdapterPosition(); //:X :X :X :X :X
                    if (pos != RecyclerView.NO_POSITION) //:X :X :X :X :X
                        recyclerViewInterface.onItemLongClick(pos);
                }
                    return true;
            }
        });
    }

    public void setPalletData(WarehouseDB pallet) {
        tvPalletNumber.setText(pallet.getPalleteNr());
        tvDateTime.setText(timestampAsString(pallet.getFIFODateTime()));
        cbActive.setChecked(false);
    }



}


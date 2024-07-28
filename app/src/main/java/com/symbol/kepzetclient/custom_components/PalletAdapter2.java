package com.symbol.kepzetclient.custom_components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.kepzetclient.R;
import com.symbol.kepzetclient.SQL.WarehouseDB;

import java.util.ArrayList;

public class PalletAdapter2 extends RecyclerView.Adapter<PalletViewHolder2> {
    private ArrayList<WarehouseDB> PalletList;
    public  PalletAdapter2(ArrayList<WarehouseDB> pPalletList){
        PalletList = pPalletList;
    }


    @NonNull
    @Override
    public PalletViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.pallet_recycle_row,parent,false);
        //vytvorenie definovaneho ViewHoldera a predanie ziskaneho layoutu z inflat prikazu
        return new PalletViewHolder2(view);
    }

    //zaslanie dat pridruzenemu ViewHolderu pre kazdy zaznam
    @Override
    public void onBindViewHolder(@NonNull PalletViewHolder2 holder, int position) {
        WarehouseDB pallet = PalletList.get(position);
        holder.setPalletData(pallet);

    }

    @Override
    public int getItemCount() {
        return  PalletList.size();
    }
}


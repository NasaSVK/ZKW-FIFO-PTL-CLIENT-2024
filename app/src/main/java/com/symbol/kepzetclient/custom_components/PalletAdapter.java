package com.symbol.kepzetclient.custom_components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.kepzetclient.R;
import com.symbol.kepzetclient.SQL.Pallet;

import java.util.ArrayList;

public class PalletAdapter extends RecyclerView.Adapter<PalletViewHolder> {
    private ArrayList<Pallet> PalletList;
    public  PalletAdapter(ArrayList<Pallet> pPalletList){
        PalletList = pPalletList;
    }


    @NonNull
    @Override
    public PalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.pallet_recycle_row,parent,false);
        //vytvorenie definovaneho ViewHoldera a predanie ziskaneho layoutu z inflat prikazu
        return new PalletViewHolder(view);
    }

    //zaslanie dat pridruzenemu ViewHolderu pre kazdy zaznam
    @Override
    public void onBindViewHolder(@NonNull PalletViewHolder holder, int position) {
        Pallet pallet = PalletList.get(position);
        holder.setPalletData(pallet);

    }

    @Override
    public int getItemCount() {
        return  PalletList.size();
    }
}

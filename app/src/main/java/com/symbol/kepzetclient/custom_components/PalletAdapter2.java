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
    private RecycleViewInterface recycleViewInterface;
    public  PalletAdapter2(ArrayList<WarehouseDB> pPalletList, RecycleViewInterface pRecycleViewInterface)
    {
        PalletList = pPalletList;
        recycleViewInterface = pRecycleViewInterface;
    }

    @NonNull
    @Override
    public PalletViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.pallet_recycle_row,parent,false);
        //vytvorenie definovaneho ViewHoldera a predanie ziskaneho layoutu z inflat prikazu
        return new PalletViewHolder2(view,this.recycleViewInterface);
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


    //########################################## NEW ####################################
    public void removeItem(int position) {
        PalletList.remove(position);
        notifyItemRemoved(position); //poslanie spravy ViewHolderu o zmanzani palety, ktory zabezpeci jej vizualne zmazanie
    }

    }


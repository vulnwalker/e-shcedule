package com.eschedule.eclient.lu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.eschedule.eclient.lu.HolderItem;
import com.eschedule.eclient.R;
import com.eschedule.eclient.lu.itemObject;

import java.util.List;


public class AdapterItem2 extends RecyclerView.Adapter<HolderItem> {

    Context context;
    List<itemObject> itemObjects;

    public AdapterItem2 (Context context, List<itemObject> itemObjects) {
        this.context = context;
        this.itemObjects = itemObjects;
    }

    @Override
    public HolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null);
        HolderItem holderItem =new HolderItem(view);
        return holderItem;
    }

    @Override
    public void onBindViewHolder(HolderItem holder, int position) {
        holder.nama.setText(itemObjects.get(position).nama);
        holder.uid.setText(itemObjects.get(position).uid);
        Glide.with(context)
                .load("http://e-schedule.info/images/users/" + itemObjects.get(position).gambar)
                .asBitmap()
                .placeholder(R.drawable.thumb)
                .into(holder.gambar);

    }

    @Override
    public int getItemCount() {
        return itemObjects.size();
    }
}

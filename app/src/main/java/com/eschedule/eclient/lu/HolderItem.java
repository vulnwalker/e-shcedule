package com.eschedule.eclient.lu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eschedule.eclient.R;


public class HolderItem extends RecyclerView.ViewHolder{
    public TextView nama, uid;
    public ImageView gambar;


    public HolderItem(View itemView) {
        super(itemView);

        nama = (TextView) itemView.findViewById(R.id.nama);
        uid = (TextView) itemView.findViewById(R.id.uid);
        gambar = (ImageView) itemView.findViewById(R.id.gambar);
    }
}
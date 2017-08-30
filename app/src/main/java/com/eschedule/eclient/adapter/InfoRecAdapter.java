package com.eschedule.eclient.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eschedule.eclient.App;
import com.eschedule.eclient.R;
import com.eschedule.eclient.core.Util;
import com.eschedule.eclient.informasi.InformasiDetailActivity;
import com.eschedule.eclient.informasi.RalatActivity;
import com.eschedule.eclient.model.Informasi;

import java.util.List;

/**
 * Created by Zund#i on 09/06/2016.


/**
 * Created by Teguh on 14/02/2016.
 */
public class InfoRecAdapter extends RecyclerView.Adapter<InfoRecAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Informasi> mDataset;
    private boolean ralatMode;

    public InfoRecAdapter(Activity activity, List<Informasi> mDataset, boolean ralatMode) {
        this.mActivity = activity;
        this.mDataset = mDataset;
        this.ralatMode = ralatMode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Informasi item = mDataset.get(position);

        // Untuk Gambar Profile Gunakan Internet
        if(Util.checkConnection(mActivity)) {
            Glide.with(mActivity)
                    .load(App.BASE_URL+"images/users/"+item.userPicture)
                    .error(R.drawable.thumb)
                    .placeholder(R.drawable.thumb)
                    .into(holder.itemPicture);
        }

        holder.itemAuthor.setText(item.userName);
        holder.itemKepada.setText(item.kepada);
        holder.itemTanggal.setText(item.tglFusion);
        holder.itemMateri.setText(item.materi);
        holder.itemPenting.setVisibility(item.stPenting.equals("1") ? View.VISIBLE : View.GONE);
        holder.itemRalat.setVisibility(item.stRalat.equals("1") ? View.VISIBLE : View.GONE);
        holder.itemHistory.setVisibility(ralatMode ? View.VISIBLE : View.GONE);
        holder.itemHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.informasiParentID.equals("0")) {
                    Util.toast(mActivity, "Tidak Memiliki Riwayat Ralat");
                } else {
                    Intent inRalat = new Intent(mActivity, RalatActivity.class);
                    inRalat.putExtra("id", item.informasiParentID);
                    mActivity.startActivity(inRalat);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        ImageView itemPicture, itemHistory;
        TextView itemAuthor, itemKepada, itemTanggal, itemMateri, itemPenting, itemRalat;

        public ViewHolder(View v) {
            super(v);
            v.setClickable(true);
            v.setOnClickListener(this);

            itemPicture = (ImageView) v.findViewById(R.id.item_info_picture);
            itemHistory = (ImageView) v.findViewById(R.id.item_info_history);

            itemAuthor = (TextView) v.findViewById(R.id.item_info_author);
            itemKepada = (TextView) v.findViewById(R.id.item_info_kepada);
            itemTanggal = (TextView) v.findViewById(R.id.item_info_tanggal);
            itemMateri = (TextView) v.findViewById(R.id.item_info_materi);
            itemPenting = (TextView) v.findViewById(R.id.item_info_penting);
            itemRalat = (TextView) v.findViewById(R.id.item_info_ralat);
        }

        @Override
        public void onClick(View view) {
            Intent detail = new Intent(mActivity, InformasiDetailActivity.class);
            detail.putExtra("id", mDataset.get(getAdapterPosition()).informasiID);
            mActivity.startActivity(detail);
        }
    }
}


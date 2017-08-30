package com.eschedule.eclient.adapter;

import android.app.Activity;
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
import com.eschedule.eclient.model.Jadwal;

import java.util.List;

/**
 * Created by Zund#i on 09/06/2016.


 /**
 * Created by Teguh on 14/02/2016.
 */
public class JadwalRecAdapter extends RecyclerView.Adapter<JadwalRecAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Jadwal> mDataset;

    public JadwalRecAdapter(Activity activity, List<Jadwal> mDataset) {
        this.mActivity = activity;
        this.mDataset = mDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Jadwal item = mDataset.get(position);

        // Untuk Gambar Profile Gunakan Internet
        if(Util.checkConnection(mActivity)) {
            Glide.with(mActivity)
                    .load(App.BASE_URL+"images/users/"+item.userPicture)
                    .error(R.drawable.thumb)
                    .placeholder(R.drawable.thumb)
                    .into(holder.itemPicture);
        }
        holder.itemidjd.setText(item.jadwalID);
        holder.itemAuthor.setText(item.userName);
        holder.itemUntuk.setText(item.untuk);
        holder.itemWaktu.setText(item.jamFusion);
        holder.itemTanggal.setText(item.tglFusion);
        holder.itemAcara.setText(item.acara);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView itemPicture;
        TextView itemAuthor, itemUntuk, itemWaktu, itemTanggal, itemAcara, itemidjd;

        public ViewHolder(View v) {
            super(v);

            itemPicture = (ImageView) v.findViewById(R.id.item_jadwal_picture);
            itemAuthor = (TextView) v.findViewById(R.id.item_jadwal_author);
            itemUntuk = (TextView) v.findViewById(R.id.item_jadwal_untuk);
            itemWaktu = (TextView) v.findViewById(R.id.item_jadwal_waktu);
            itemTanggal = (TextView) v.findViewById(R.id.item_jadwal_tanggal);
            itemAcara = (TextView) v.findViewById(R.id.item_jadwal_acara);
            itemidjd = (TextView) v.findViewById(R.id.item_jadwal_idjd);
        }
    }
}


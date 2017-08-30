package com.eschedule.eclient.informasi;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.eschedule.eclient.MainActivity;
import com.eschedule.eclient.R;
import com.eschedule.eclient.adapter.InfoRecAdapter;
import com.eschedule.eclient.model.Informasi;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformasiFragment extends Fragment {

    private List<Informasi> mDataset;
    private RecyclerView mInfoRecycle;
    private InfoRecAdapter mDataAdapter;
    private MainActivity mParent;
    private RelativeLayout mInfoNoData;
    public InformasiFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mParent = (MainActivity) getActivity();
        mDataset = mParent.myApp.getDB().getInformasi();
        mInfoNoData.setVisibility( mDataset.size() > 0 ? View.GONE : View.VISIBLE);

        mDataAdapter = new InfoRecAdapter(mParent, mDataset, true);

        mInfoRecycle.setLayoutManager(new LinearLayoutManager(mParent));
        mInfoRecycle.setHasFixedSize(true);
        mInfoRecycle.setAdapter(mDataAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_informasi, container, false);
        mInfoRecycle = (RecyclerView) v.findViewById(R.id.info_recycle);
        getActivity().findViewById(R.id.add_info).setVisibility(View.VISIBLE);
        mInfoNoData = (RelativeLayout) v.findViewById(R.id.info_nodata);
        return v;
    }

    public void swap() {
        mDataset = mParent.myApp.getDB().getInformasi();
        mInfoRecycle.setAdapter(new InfoRecAdapter(mParent, mDataset, true));
        mInfoNoData.setVisibility( mDataset.size() > 0 ? View.GONE : View.VISIBLE);
    }

}

package com.eschedule.eclient.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eschedule.eclient.R;
import com.eschedule.eclient.model.IndexValue;

import java.util.ArrayList;


/**
 * Created by Teguh on 7/31/2015.
 */
public class SpinnerAdapter extends BaseAdapter {

    public ArrayList<IndexValue> mItems = new ArrayList<>();

    private Activity mActivity;

    public SpinnerAdapter(Activity activity, ArrayList<IndexValue> items) {
        this.mActivity = activity;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public IndexValue getItemModel(int position) {
        return mItems.get(position);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = mActivity.getLayoutInflater().inflate(R.layout.spinner_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(Html.fromHtml(getTitle(position)));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mActivity.getLayoutInflater().inflate(R.layout.spinner_item, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(Html.fromHtml(getTitle(position)));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).getValue() : "";
    }

    public void setError(View v, CharSequence s) {
        TextView name = (TextView) v.findViewById(android.R.id.text1);
        name.setError(s);
    }
}

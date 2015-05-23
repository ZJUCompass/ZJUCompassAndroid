package com.zjucompass.zjucampus3;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MatterListAdapter extends ArrayAdapter<Matter> {

    public MatterListAdapter(Context context, int resource, int textViewResourceId,
                              ArrayList<Matter> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        Matter t = getItem(position);
        TextView matterNameView = (TextView) view.findViewById(R.id.matter_name);
        matterNameView.setText(t.getName());

        return view;
    }
}
package net.alexandroid.network.cctvportscanner.main.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.alexandroid.network.cctvportscanner.R;

import java.util.List;

public class SuggestionsAdapter extends ArrayAdapter<String> {


    private View.OnClickListener mOnClickListener;

    public SuggestionsAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestion, parent, false);
        }
        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvHost);
        // Populate the data into the template view using the data object
        tvName.setText(getItem(position));
        // Return the completed view to render on screen

        AppCompatImageView btnClear = convertView.findViewById(R.id.btnClear);

        if (mOnClickListener != null) {
            tvName.setTag(getItem(position));
            btnClear.setTag(getItem(position));
            tvName.setOnClickListener(mOnClickListener);
            btnClear.setOnClickListener(mOnClickListener);
        }
        return convertView;
    }

    public void setOnClickListener(View.OnClickListener pOnClickListener) {
        mOnClickListener = pOnClickListener;
    }

}

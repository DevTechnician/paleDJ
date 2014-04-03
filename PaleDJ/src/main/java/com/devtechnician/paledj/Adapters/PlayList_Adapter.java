package com.devtechnician.paledj.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devtechnician.paledj.Bus_Objects.Play_Media;
import com.devtechnician.paledj.R;

import java.util.ArrayList;

/**
 * Created by Jason on 9/11/13.
 */
public class PlayList_Adapter extends ArrayAdapter<Play_Media>{

    public PlayList_Adapter(Context context) {
        super(context,0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.playlist_row, null);
        }

        TextView name = (TextView)convertView.findViewById(R.id.name_textView);
        name.setText(getItem(position).FILENAME);
        return convertView;
    }


}

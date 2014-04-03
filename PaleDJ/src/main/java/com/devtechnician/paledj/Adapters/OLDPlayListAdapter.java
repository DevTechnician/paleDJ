package com.devtechnician.paledj.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devtechnician.paledj.R;

/**
 * Created by Jason on 9/10/13.
 */
public class OLDPlayListAdapter extends CursorAdapter {

    private LayoutInflater pLayoutInflater;
    private Context pContext;
    private boolean pAutoRequery;
    private Cursor pCursor;


    public OLDPlayListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.pAutoRequery = autoRequery;
        this.pContext = context;
        this.pLayoutInflater = LayoutInflater.from(pContext);
        this.pCursor = c;


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup view) {

        return pLayoutInflater.inflate(R.layout.playlist_row,view,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder)view.getTag();
        if (holder == null){

            holder = new ViewHolder();
            holder.playlist_ID = (TextView)view.findViewById(R.id.id_textView);
            holder.playlist_NAME = (TextView)view.findViewById(R.id.name_textView);

            holder.ID_Column = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists._ID);
            holder.NAME_Column = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);
        }

        holder.playlist_ID.setText(holder.ID_Column);
        holder.playlist_NAME.setText(holder.NAME_Column);

    }
    private static class ViewHolder{

        TextView playlist_ID;
        TextView playlist_NAME;

        int ID_Column;
        int NAME_Column;

    }
}

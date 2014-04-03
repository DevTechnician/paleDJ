package com.devtechnician.paledj.Adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devtechnician.paledj.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 9/4/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlbumAdapter extends CursorAdapter {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Filter mFilter;

    public AlbumAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mLayoutInflater.inflate(R.layout.media_row,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder)view.getTag();
        if (holder == null){

            holder = new ViewHolder();
            holder.title = (TextView)view.findViewById(R.id.filenameView);
            holder.icon = (ImageView)view.findViewById(R.id.actionIconView);
            holder.progressText = (TextView)view.findViewById(R.id.progressTextView);

            holder.titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            holder.iconColumn = R.drawable.abs__progress_medium_holo;


        }

        holder.title.setText(cursor.getString(holder.titleColumn));

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri imageUri = ContentUris.withAppendedId(sArtworkUri, cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
        UrlImageViewHelper.setUrlDrawable(holder.icon, imageUri.toString(), R.drawable.abs__progress_medium_holo);

        Log.e("Album", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        Log.e("Album",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
        Log.e("Album",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
    }
    private static class ViewHolder{
        ImageView icon;
        TextView title;
        TextView progressText;

        int titleColumn;
        int iconColumn;

    }


}

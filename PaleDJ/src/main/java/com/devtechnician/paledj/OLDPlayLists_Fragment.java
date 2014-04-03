package com.devtechnician.paledj;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.devtechnician.paledj.Adapters.OLDPlayListAdapter;

/**
 * Created by Jason on 9/10/13.
 */
public class OLDPlayLists_Fragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String[] projection;
    private Uri uri;
    private String selection;
    private OLDPlayListAdapter playListAdapter;
    private int PLOADER_ID = 1;
    private LoaderManager.LoaderCallbacks<Cursor> pCallBacks;
    private Cursor pCursor;
    private CursorLoader pCursorLoader;
    private LoaderManager pLoaderManager;
    private ListView playList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        projection = new String[]{
                    MediaStore.Audio.Playlists._ID,
                    MediaStore.Audio.Playlists.NAME,
                    MediaStore.Audio.Playlists.DATA

            };

        playListAdapter = new OLDPlayListAdapter(getActivity().getApplicationContext(),null,true);
        pCallBacks = this;
        pLoaderManager = getLoaderManager();
        Bundle bundle = new Bundle();
        bundle.putString("Selection",selection);
        pLoaderManager.initLoader(PLOADER_ID,bundle,pCallBacks);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.playlist_list,null);
        playList = (ListView)v.findViewById(R.id.playlist);
        return v;


    }

    @Override
    public void onStart() {
        super.onStart();
        playList.setAdapter(playListAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        this.pCursorLoader = new CursorLoader(
                getActivity().getApplicationContext(),
                uri,
                projection,
                null,
                null,
                null
        );
        return this.pCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
         this.playListAdapter.swapCursor(cursor);
        this.pCursor = cursor;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        this.playListAdapter.swapCursor(null);
    }
}

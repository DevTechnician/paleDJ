package com.devtechnician.paledj;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.*;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.devtechnician.paledj.Adapters.AlbumAdapter;
import com.devtechnician.paledj.Bus_Objects.Play_Media;
import com.devtechnician.paledj.Bus_Objects.Song;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.io.File;

/**
 * Created by Jason on 6/20/13.
 */
public class DeviceMedia_Fragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, SearchView.OnCloseListener, ListView.OnItemClickListener {

    public static final String FRAGMENT_NAME = "DEVICE_MEDIA_LIST";

     private int LOADER_ID = 1;
     private LoaderManager.LoaderCallbacks<Cursor> mCallBacks;
     private AlbumAdapter albumAdapter;
     private Cursor mediaCursor;
     private Uri uri;
     private String selection;
     private String[] projection;
     private ListView lv;
     private TextView selectedRowTextView;
     private LoaderManager loaderManager;
     private DeviceSearchView mSearchView;
     private boolean dataLoaded;
     private CursorLoader mediaCursorLoader;
     private String searchQuery;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRetainInstance(true);
        setHasOptionsMenu(true);
        dataLoaded = false;


        projection = new String[] {
                //MediaStore.Audio.Media.,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID


        };
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        albumAdapter = new AlbumAdapter(getActivity().getApplicationContext(),null);
        mCallBacks = this;
        loaderManager = getLoaderManager();
        selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Bundle b = new Bundle();
        b.putString("select",selection);
        loaderManager.initLoader(LOADER_ID,b,mCallBacks);


}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_list, null);
        lv = (ListView)view.findViewById(R.id.devicelist);
        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        lv.setAdapter(albumAdapter);
        lv.setOnItemClickListener(this);


 }

    @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.device_popup_menu, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedRowTextView = (TextView)info.targetView.findViewById(R.id.progressTextView);
        mediaCursor.moveToPosition(info.position);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_upload:
                uploadFile();
                return true;
            case R.id.menu_device_play:
                playFile();
                return true;
            case R.id.menu_device_delete:
                deleteFile();
                return true;
        }

        return false;
    }

    private void playFile(){
        int data = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        int fileName = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
        int id = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        BusProvider.getInstance().post(new Play_Media(
                mediaCursor.getString(fileName),
                mediaCursor.getString(data),
                mediaCursor.getInt(id)

        ));
    }

    private void deleteFile(){
        try {
            // delete the original file
            int data = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int id = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);


           int rows = getActivity().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                   MediaStore.Audio.Media._ID + "=" + mediaCursor.getInt(id),null);

            File file = new File(mediaCursor.getString(data));
           boolean isfileDeleted = file.delete();

            Log.e("file delete", "mediastore " + String.valueOf(rows) + " " + String.valueOf(isfileDeleted));

        }

        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }



    }

    private void uploadFile(){
        int data = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        int fileName = mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
        BusProvider.getInstance().post(new Song(
                "UPLOAD",
                mediaCursor.getString(data),
                mediaCursor.getString(fileName)
        ));

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

           this.mediaCursorLoader = new CursorLoader(
                    getActivity().getApplicationContext(),
                    uri,
                    projection,
                    bundle.getString("select"),
                    null,
                    null );
        return this.mediaCursorLoader;
        }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
      this.albumAdapter.swapCursor(cursor);
       this.mediaCursor = cursor;
        dataLoaded = true;
        Log.e("", "load finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
      this.albumAdapter.swapCursor(null);
        Log.e("", "load reset");
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        unregisterForContextMenu(lv);
        super.onPause();
    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
        registerForContextMenu(lv);

        super.onResume();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        com.actionbarsherlock.view.MenuItem menuItem = menu.add("Search");
        menuItem.setIcon(android.R.drawable.ic_menu_search);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        mSearchView = new DeviceSearchView(getActivity().getApplicationContext());
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        menuItem.setActionView(mSearchView);

        //com.actionbarsherlock.view.MenuItem testItem = menu.add(0,0,0,"test");

        com.actionbarsherlock.view.MenuItem playItem = menu.add(0,10,0,"Play");
        playItem.setShowAsAction(playItem.SHOW_AS_ACTION_NEVER |
                playItem.SHOW_AS_ACTION_WITH_TEXT);

        com.actionbarsherlock.view.MenuItem uploadItem = menu.add(0,20,0,"Upload");
        uploadItem.setShowAsAction(uploadItem.SHOW_AS_ACTION_NEVER |
                uploadItem.SHOW_AS_ACTION_WITH_TEXT);

        com.actionbarsherlock.view.MenuItem deleteItem = menu.add(0,30,0,"Delete");
        deleteItem.setShowAsAction(deleteItem.SHOW_AS_ACTION_NEVER |
                deleteItem.SHOW_AS_ACTION_WITH_TEXT);
 }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mediaCursor.moveToPosition(position);
        getActivity().openOptionsMenu();
       // switch (){}

    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()){
            case 10://play
                playFile();
                break;
            case 20://upload
                uploadFile();
                break;
            case 30://delete
                deleteFile();
                break;

        }
        return true;
    }

    private void changeQuery(String s){

        String select =  MediaStore.Audio.Media.TITLE + " LIKE '%"+s.toString()+"%' AND "+ MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Bundle b = new Bundle();
        b.putString("select", select);
        loaderManager.restartLoader(LOADER_ID, b, mCallBacks);
    }
    @Override
    public boolean onQueryTextSubmit(String s) {

        changeQuery(s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        changeQuery(s);
        return true;
    }

    @Override
    public boolean onClose() {
        mSearchView.setQuery("", true);
        return true;
    }



    public static class DeviceSearchView extends SearchView {
        public DeviceSearchView(Context context) {
            super(context);
        }

        // The normal SearchView doesn't clear its search text when
        // collapsed, so we will do this for it.
        @Override
        public void onActionViewCollapsed() {
            setQuery("", false);

        }
    }


    private class AlbumsAdapter extends CursorAdapter {


        private LayoutInflater mLayoutInflater;
        private Context mContext;


        public AlbumsAdapter(Context context, Cursor c) {
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

            Log.e("Album",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            Log.e("Album",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
            Log.e("Album",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
        }
        private  class ViewHolder{
            ImageView icon;
            TextView title;
            TextView progressText;

            int titleColumn;
            int iconColumn;

        }
    }
}

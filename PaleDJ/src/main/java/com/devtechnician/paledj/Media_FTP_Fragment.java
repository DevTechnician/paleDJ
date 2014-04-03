package com.devtechnician.paledj;

import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.*;
import com.actionbarsherlock.view.Menu;
import com.devtechnician.paledj.Bus_Objects.Song;
import com.squareup.otto.Subscribe;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

/**
 * Created by Jason on 6/16/13.
 * ftp://ftp.devtechnician.net:9021
 : user paledj
 : password 5689hhhu79lv45
 */


public class Media_FTP_Fragment extends SherlockListFragment implements ListView.OnItemClickListener {
    public static final String FRAGMENT_NAME = "FTP_SERVER";

    private MediaListAdapter mediaListAdapter;
    private int listItemPosition;
    private ListView ftpList;
    private FtpConnection mTask;

    public static ResultReceiver mFTPReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        setHasOptionsMenu(true);
        mFTPReceiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                if (resultCode == 100)//update view from ftp server
                {
                    mTask = new FtpConnection();
                    mTask.execute();
                }
            }
        };

        mediaListAdapter = new MediaListAdapter(getActivity().getApplicationContext());

        setListAdapter(mediaListAdapter);
        mTask = new FtpConnection();
        mTask.execute();



    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        ftpList = getListView();
        ftpList.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        registerForContextMenu(getListView());

    }


    @Subscribe
    public void startFTP(Song event){
        Intent ftpIntent = new Intent(getActivity().getApplicationContext(),FTPIntentService.class);
        ftpIntent.putExtra(FTPIntentService.PARAM_ACTION,event.ACTION);
        ftpIntent.putExtra(FTPIntentService.PARAM_FILE_NAME,event.FILENAME);
        ftpIntent.putExtra(FTPIntentService.PARAM_FILE_PATH,event.FILEPATH);
        ftpIntent.putExtra(FTPIntentService.PARAM_FILE_SIZE,event.FILESIZE);
        getActivity().getApplicationContext().startService(ftpIntent);


    }



    private class MediaListAdapter extends ArrayAdapter<FTPFile>{

        public MediaListAdapter(Context context){
            super(context,0);
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.media_row, parent, false);
            }
            ImageView icon = (ImageView) convertView
                    .findViewById(R.id.actionIconView);
            icon.setImageResource(R.drawable.abs__progress_medium_holo);
            TextView title = (TextView) convertView
                    .findViewById(R.id.filenameView);
            title.setText(getItem(position).getName());


            return convertView;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterForContextMenu(getListView());
        BusProvider.getInstance().unregister(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        com.actionbarsherlock.view.MenuItem downloadItem = menu.add(0,10,0,"DownLoad");
        downloadItem.setShowAsAction(downloadItem.SHOW_AS_ACTION_NEVER |
                downloadItem.SHOW_AS_ACTION_WITH_TEXT);

        com.actionbarsherlock.view.MenuItem deleteItem = menu.add(0,30,0,"Delete");
        deleteItem.setShowAsAction(deleteItem.SHOW_AS_ACTION_NEVER |
                deleteItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listItemPosition = position;
        getActivity().openOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()){
            case 10:
                downloadFile();
                break;
            case 30:
                deleteFile();
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.ftp_popup_menu, menu);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        listItemPosition = info.position;


    }



    private void downloadFile(){
        long fileSize = mediaListAdapter.getItem(listItemPosition).getSize();
        BusProvider.getInstance().post(new Song(
                "DOWNLOAD",
                Environment.getExternalStoragePublicDirectory("music").toString(),
                mediaListAdapter.getItem(listItemPosition).getName(),
                (int)fileSize));
    }

    private void deleteFile(){
        BusProvider.getInstance().post(new Song(
                "DELETE",
                "",
                mediaListAdapter.getItem(listItemPosition).getName()));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_ftp_download:
             downloadFile();
                return true;
            case R.id.menu_ftp_delete:
              deleteFile();
                return true;
        }


        return super.onContextItemSelected(item);
    }


    private class FtpConnection extends AsyncTask<Void,Void,FTPFile[]> {
        FTPFile[] mFiles;
        private FTPClient paleDjClient;

        public FtpConnection(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            paleDjClient = new FTPClient();
            paleDjClient.setBufferSize(1024 * 100);

        }

        @Override
        protected FTPFile[] doInBackground(Void... params) {
            try {
                paleDjClient.connect("ftp.devtechnician.net",9021);
                boolean status = paleDjClient.login("paledj","5689hhhu79lv45");
                paleDjClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                paleDjClient.enterLocalPassiveMode();
                mFiles = paleDjClient.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mFiles;

        };

        @Override
        protected void onPostExecute(FTPFile[] ftpFiles) {
            mediaListAdapter.clear();
            if ( ftpFiles != null){
                for (FTPFile file : ftpFiles){
                    mediaListAdapter.add(file);
                }
                mediaListAdapter.notifyDataSetChanged();
            }

            try {

                paleDjClient.disconnect();

                } catch (IOException e) {
                e.printStackTrace();
            }
            paleDjClient = null;
            mTask = null;
        }
    }
}
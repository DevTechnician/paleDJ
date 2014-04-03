package com.devtechnician.paledj;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * Created by Jason on 6/27/13.
 * check internet connection
 * check for sd card
 * check if folder exists/create folder in sd card or internal storage
 * set file path for file download
 */
public class Media_Utilities_Fagment extends Fragment {

    boolean isExternalMediaAvailable;
    boolean isInternetAvailable;
    File dir;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        isExternalMediaAvailable = isSDCardAvailable();
        isInternetAvailable = isConnected();
        dir = Environment.getExternalStorageDirectory();
        File pubdir = Environment.getExternalStoragePublicDirectory("music");
        pubdir.mkdir();

        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //check internet connection
    private boolean isConnected(){

        ConnectivityManager connectivity = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    private boolean isSDCardAvailable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    private boolean isFolderPresent(){
        if (isExternalMediaAvailable){

            return true;
        }

        return false;
    }

    private String setDownloadPath(){

        return null;
    }

}

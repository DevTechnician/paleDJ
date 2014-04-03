package com.devtechnician.paledj;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.google.android.gcm.GCMRegistrar;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Jason on 6/6/13.
 */
public class GCMTask_Fragment extends Fragment {

    private String reg_ID;
    private String proj_ID;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       proj_ID = getString(R.string.project_id);
        //PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("MYLABEL", "myStringToSave").commit();
       reg_ID = getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("regId", "");
       GCMRegistrar.checkManifest(getActivity());
        if (reg_ID == ""){
            RegisterTask register = new RegisterTask();
            register.execute(proj_ID);
        }




    }

    private class RegisterTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... params) {

            GCMRegistrar.register(getActivity().getApplicationContext(), proj_ID);

            return null;
        }
    }
}

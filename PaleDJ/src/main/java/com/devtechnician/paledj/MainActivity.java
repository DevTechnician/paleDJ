package com.devtechnician.paledj;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragment;


public class MainActivity extends BaseActivity {

    private SherlockFragment mainFrag;
    private EditText searchText;


	public MainActivity() {
		super(R.string.app_name);

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.content_frame);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        FragmentManager fm = getSupportFragmentManager();

        mainFrag = (DeviceMedia_Fragment)fm.findFragmentByTag("mainFragment");
        if (mainFrag == null) {
            mainFrag = new DeviceMedia_Fragment();
            fm.beginTransaction().add(R.id.content_frame,mainFrag,"mainFragment").commit();
        }

        Fragment utilFrag = (Media_Utilities_Fagment)fm.findFragmentByTag("utilFrag");
        if(utilFrag == null)
        {
            utilFrag = new Media_Utilities_Fagment();
            fm.beginTransaction().add(utilFrag,"utilFrag").commit();
        }

        Fragment gcmFrag = (GCMTask_Fragment)fm.findFragmentByTag("gcmFrag");
        if (gcmFrag == null)
        {
            gcmFrag = new GCMTask_Fragment();
            fm.beginTransaction().add(gcmFrag,"gcmFrag").commit();
        }

       /* Fragment player = (PaleDjPlayer_Fragment)fm.findFragmentByTag("player");
        if (player == null)
        {
            player = new PaleDjPlayer_Fragment();
            fm.beginTransaction().add(player,"player").commit();
        }*/


    }



}

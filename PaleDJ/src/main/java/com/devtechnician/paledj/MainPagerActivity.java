package com.devtechnician.paledj;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.devtechnician.paledj.Adapters.PaleDJPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 8/21/13
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainPagerActivity extends SherlockFragmentActivity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Fragment ftpFrag = null;
    private Fragment deviceFrag = null;
    private Fragment playListFrag = null;
    private TitlePageIndicator mPageIndicator;
    private FragmentManager fm;
    private TextView titleText;
    private ImageView albumArt;
    private ImageButton actionButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_pager);

        titleText = (TextView)findViewById(R.id.nowplaying_textview);
        albumArt = (ImageView)findViewById(R.id.albumart_IconView);
        actionButton = (ImageButton)findViewById(R.id.actionImage_button);

        fm = getSupportFragmentManager();

        if(savedInstanceState !=null){
            String deviceFragTag = savedInstanceState.getString("deviceFragTag");
            if(deviceFragTag != null){
                deviceFrag = fm.findFragmentByTag(deviceFragTag);
            }
        }  else{
            deviceFrag = new DeviceMedia_Fragment();
        }

        if(savedInstanceState !=null){
            String ftpFragTag = savedInstanceState.getString("ftpFragTag");
            if(ftpFragTag != null){
                ftpFrag = fm.findFragmentByTag(ftpFragTag);
            }

        }   else{
            ftpFrag = new Media_FTP_Fragment();
        }

        if(savedInstanceState !=null){
            String playListFragTag = savedInstanceState.getString("playListFragTag");
            if(playListFragTag != null){
                playListFrag = fm.findFragmentByTag(playListFragTag);
            }

        }   else{
            playListFrag = new PlayList_Fragment();
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


        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(ftpFrag);
        fragmentList.add(deviceFrag);
        fragmentList.add(playListFrag);
        mPager = (ViewPager)findViewById(R.id.pager);

        mPagerAdapter = new PaleDJPagerAdapter(fm,fragmentList);

        mPager.setAdapter(mPagerAdapter);

        mPageIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mPageIndicator.setViewPager(mPager);
        mPageIndicator.setCurrentItem(mPagerAdapter.getCount()-1);

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(this.deviceFrag != null){
            outState.putString("deviceFragTag",this.deviceFrag.getTag());

        }
        if(this.ftpFrag != null){
            outState.putString("ftpFragTag",this.ftpFrag.getTag());
        }
        if (this.playListFrag != null)
            outState.putString("playListFragTag",this.playListFrag.getTag());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getSupportMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return  true;
    }



}

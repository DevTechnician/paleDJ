package com.devtechnician.paledj;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.devtechnician.paledj.Adapters.PlayList_Adapter;
import com.devtechnician.paledj.Bus_Objects.Play_Media;
import com.squareup.otto.Subscribe;

/**
 * Created by Jason on 9/11/13.
 */
public class PlayList_Fragment extends SherlockFragment implements MediaController.MediaPlayerControl{

    static ListView playList;
    static Play_Media mediaHolder;
    private MediaController mediaController;
    private View v;
    static ResultReceiver nowPlayingReciever;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        nowPlayingReciever = new ResultReceiver(new Handler()){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.playlist_list,null);
        playList = (ListView)v.findViewById(R.id.playlist);
        return v;
    }

    @Override
    public void onStart() {
        if (PaleDJPlayer_Service.player_ResultReceiver!=null){
            PaleDJPlayer_Service.player_ResultReceiver.send(100,null);
        }
        super.onStart();
        mediaController = new MediaController(getActivity());
        mediaController.setMediaPlayer(this);
        //mediaController.setAnchorView(v.findViewById(R.id.controllerAnchorView));

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        BusProvider.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);

        super.onPause();
    }

    @Subscribe
    public void onPlayMediaSelected(Play_Media media){

        if (PaleDJPlayer_Service.player_ResultReceiver!=null){
            mediaHolder = media;
            PaleDJPlayer_Service.player_ResultReceiver.send(101,null);
        }else
        {
            mediaHolder = media;
            startMediaPlayer();

        }

    }

    private void startMediaPlayer(){
        if (PaleDJPlayer_Service.player_ResultReceiver == null){
            Intent playIntent = new Intent(getActivity().getApplicationContext(),PaleDJPlayer_Service.class);
            getActivity().getApplicationContext().startService(playIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.player_control_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (PaleDJPlayer_Service.player_ResultReceiver!=null){
        switch (item.getItemId()){

            case R.id.audio_playpause:
                PaleDJPlayer_Service.player_ResultReceiver.send(102,null);
                break;

            case R.id.audio_next:
                PaleDJPlayer_Service.player_ResultReceiver.send(103,null);
                break;

            case R.id.audio_quit:
                PaleDJPlayer_Service.player_ResultReceiver.send(104,null);
                break;
         }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }



}

package com.devtechnician.paledj;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.MediaController;

import com.devtechnician.paledj.Adapters.PlayList_Adapter;
import com.devtechnician.paledj.Bus_Objects.Play_Media;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jason on 9/12/13.
 */
public class PaleDJPlayer_Service extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {

    public static ResultReceiver player_ResultReceiver;
    private MediaPlayer paleDJMediaPlayer;
    private PlayList_Adapter list_adapter;
    private int notificationID = 420;
    private MediaController paleDJMediaControl;
    View anchorView;

    @Override
    public void onCreate() {
        super.onCreate();
        list_adapter = new PlayList_Adapter(getApplicationContext());

        player_ResultReceiver = new ResultReceiver(new Handler()){

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                //start,stop,pause etc, change song in list
                switch (resultCode){
                    case 100://set list Adapter
                        PlayList_Fragment.playList.setAdapter(list_adapter);
                        break;

                    case 101://add media
                        list_adapter.add(PlayList_Fragment.mediaHolder);

                         break;
                }

                    if (paleDJMediaPlayer!=null){

                 switch (resultCode){

                    case 102://pause/play
                         if (paleDJMediaPlayer.isPlaying()){
                            paleDJMediaPlayer.pause();
                             sendNotification("Paused");
                        }else{
                            paleDJMediaPlayer.start();
                             sendNotification(list_adapter.getItem(0).FILENAME);
                        }
                        break;

                     case 103:
                         if (!paleDJMediaPlayer.isPlaying())
                         {
                             paleDJMediaPlayer.seekTo(paleDJMediaPlayer.getDuration());
                             paleDJMediaPlayer.start();
                         }
                         paleDJMediaPlayer.seekTo(paleDJMediaPlayer.getDuration());
                        break;

                     case 104:
                         stopSelf();
                         break;
                    }

                }
            }
        };
        PlayList_Fragment.playList.setAdapter(list_adapter);
        list_adapter.add(PlayList_Fragment.mediaHolder);

        playTheMediaList();
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void playTheMediaList(){
        if (paleDJMediaPlayer == null){
            paleDJMediaPlayer = new MediaPlayer();
            paleDJMediaPlayer.setOnCompletionListener(this);
            paleDJMediaPlayer.setOnPreparedListener(this);
        }
        Uri myUri = Uri.parse(list_adapter.getItem(0).FILEPATH);

        paleDJMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            paleDJMediaPlayer.setDataSource(getApplicationContext(), myUri);
            paleDJMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        paleDJMediaPlayer.start();
        if (PlayList_Fragment.nowPlayingReciever!=null){
            PlayList_Fragment.nowPlayingReciever.send(500,null);
        }
        sendNotification(list_adapter.getItem(0).FILENAME);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        paleDJMediaPlayer.reset();

        if (list_adapter.getCount()>0){
            list_adapter.remove(list_adapter.getItem(0));
            list_adapter.notifyDataSetChanged();

            if (list_adapter.getCount()>0){
            playTheMediaList();
            }else
            {


                stopSelf();
            }
        }

    }

    @Override
    public void onDestroy() {
        if (paleDJMediaPlayer != null){
        paleDJMediaPlayer.release();
        paleDJMediaPlayer = null;
        }
        if (PlayList_Fragment.playList != null){
            PlayList_Fragment.playList.setAdapter(null);
        }

        list_adapter = null;
        player_ResultReceiver = null;
        sendNotification("Finished");


        super.onDestroy();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {



    }

    private void sendNotification(String message){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.abs__ic_go)
                        .setContentTitle("Now Playing")
                        .setContentText(message);

// Creates an explicit intent for an Activity in your app
        //<---------------------change activity------------------------------------------------>
        Intent resultIntent = new Intent(this, MainPagerActivity.class);
        /*Intent resultIntent = new Intent(this, MainActivity.class);*/

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        //<---------------------change activity------------------------------------------------>
        //stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addParentStack(MainPagerActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());

    }

}

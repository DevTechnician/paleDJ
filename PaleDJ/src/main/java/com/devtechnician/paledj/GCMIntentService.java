package com.devtechnician.paledj;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;


/**
 * Created by Jason on 6/4/13.
 */
public class GCMIntentService extends GCMBaseIntentService {
  public GCMIntentService(){

        super(String.valueOf(R.string.project_id));
    }


    @Override
    protected void onMessage(Context context, Intent intent) {
        Intent i = intent;
        /*if (MessengerTask_Fragment.messageReceiver !=null){
            ResultReceiver receiver = MessengerTask_Fragment.messageReceiver;
            receiver.send(1,i.getExtras());
        }*/
        Log.i(TAG, "onMessage: data showed up");

       /*String mess = i.getStringExtra("message");
        String user = i.getStringExtra("user");
        String from = i.getStringExtra("userId");*/



    }

    @Override
    protected void onError(Context context, String s) {
       Log.i("gcm","error") ;
    }

    @Override
    protected void onRegistered(Context context, String s) {
        Log.i(TAG, "onRegistered: registrationId=" + s);
        if (s != null){
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("regId", s).commit();
        }

    }

    @Override
    protected void onUnregistered(Context context, String s) {

    }

    /*private void sendNotification(Context context, String msg) {

        Intent notifyIntent = new Intent(context.getApplicationContext(),Test_FragmentActivity.class);
        notifyIntent.p
        PendingIntent	notifyPendIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notifyIntent, 0);
        NotificationManager	notifyMngr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification Message = new Notification();
        Message.icon = R.drawable.icon;
        Message.tickerText = message;
        Message.number = 0;
        Message.flags = Notification.FLAG_AUTO_CANCEL;

        Message.setLatestEventInfo(context.getApplicationContext(), "Maintenance is Due", message, notifyPendIntent);
        notifyMngr.notify(0,Message);
    }*/

}

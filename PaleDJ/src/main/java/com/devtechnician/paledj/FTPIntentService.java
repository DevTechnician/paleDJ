package com.devtechnician.paledj;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 8/23/13
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class FTPIntentService extends IntentService {
    private FTPClient mClient;
    private boolean mStatus;
    private int mTotalBytesTransfered;
    public static final String PARAM_FILE_NAME = "fileName";
    public static final String PARAM_FILE_PATH = "filePath";
    public static final String PARAM_FILE_SIZE = "fileSize";
    public static final String PARAM_ACTION = "action";

    private int mId;
    private int paramFileSize;
    private String paramFileName;
    private String paramFilePath;
    private String paramAction;

    private String lineSep = System.getProperty ("line.separator");

    private enum ACTION{
        UPLOAD,
        DOWNLOAD,
        DELETE
    }
    public FTPIntentService() {
        super("FTPIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        paramAction = intent.getStringExtra(PARAM_ACTION);
        paramFileName = intent.getStringExtra(PARAM_FILE_NAME);
        paramFilePath = intent.getStringExtra(PARAM_FILE_PATH);
        paramFileSize = intent.getIntExtra(PARAM_FILE_SIZE,0);

        mClient = new FTPClient();
        mClient.setBufferSize(1024 * 1024);

        try {
            mClient.connect("ftp.devtechnician.net", 9021);
            mStatus = mClient.login("paledj", "5689hhhu79lv45");
            mClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            mClient.enterLocalPassiveMode();

        switch (ACTION.valueOf(paramAction)){

            case DOWNLOAD:
                downloadFile();
                break;
            case UPLOAD:
                uploadFile();
                break;
            case DELETE:
                deleteFile();
                break;
        }

    } catch (IOException e) {
            e.printStackTrace();
            Log.d("client connected", String.valueOf(mStatus));

        }


    }

    private void downloadFile(){
        try {
            String path = paramFilePath + "/" + paramFileName;
            FileOutputStream fileOutputStream = new FileOutputStream(path);



            CopyStreamAdapter streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

                     mTotalBytesTransfered = (int)totalBytesTransferred;
                    sendNotification(paramAction + "ING " + lineSep + paramFileName + lineSep +
                            String.valueOf(totalBytesTransferred)+"/"+ paramFileSize);
                }

            };
            mClient.setCopyStreamListener(streamListener);

            mClient.retrieveFile(paramFileName, fileOutputStream);
            fileOutputStream.close();
            updateMediaStore(paramFileName);

        } catch (IOException e) {
            e.printStackTrace();
            sendNotification("Download Failed");
        }
        sendNotification("Download Complete");
    }

    private void uploadFile(){

        try {
            FileInputStream fileInputStream = new FileInputStream(paramFilePath);
             long fileSize = (long)fileInputStream.available();
              paramFileSize = (int)fileSize;


                      CopyStreamAdapter streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {



                    sendNotification(paramAction + "ING " + lineSep + paramFileName + lineSep +
                            String.valueOf(totalBytesTransferred)+"/"+ paramFileSize);

                }

            };
            mClient.setCopyStreamListener(streamListener);
            mClient.storeFile(paramFileName, fileInputStream);
            fileInputStream.close();
            updateFTPFilelist(100);//update filelist on ftp list fragment

        } catch (IOException e) {
            e.printStackTrace();
            sendNotification("Upload Failed");
        }
        sendNotification("Upload Complete");

    }
    //delete file on ftp server
    private void deleteFile(){

        try {
            mClient.deleteFile(paramFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendNotification(paramAction+" "+paramFileName);
        updateFTPFilelist(100);    //100 refresh ftp fragment
    }

    private void updateFTPFilelist(int code){
        if(Media_FTP_Fragment.mFTPReceiver != null){
            ResultReceiver receiver = Media_FTP_Fragment.mFTPReceiver;

            Bundle bundle = new Bundle();
            bundle.putString(PARAM_ACTION,paramAction);
            receiver.send(code,bundle);
        }
    }

    private void updateMediaStore(String fileName){
        Intent intent =

        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(Environment.getExternalStoragePublicDirectory("music"),fileName);
        intent.setData(Uri.fromFile(file));

        sendBroadcast(intent);

    }

    private void sendNotification(String message){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.abs__ic_go)
                        .setContentTitle("PaleDJ")
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
        mNotificationManager.notify(mId, mBuilder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onStart(Intent intent, int startId) {
         super.onStart(intent, startId);
         this.mId = startId;
    }



    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

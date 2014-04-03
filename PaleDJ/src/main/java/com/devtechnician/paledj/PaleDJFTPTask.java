package com.devtechnician.paledj;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.devtechnician.paledj.Bus_Objects.Song;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 8/9/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaleDJFTPTask extends AsyncTask<Song,Integer,FTPFile[]> {

    private enum Action{
        FILELIST,
        UPLOAD,
        DOWNLOAD,
        DELETE
    }

    private FTPClient paleDjClient;
    private Song mediaData;
    private static ProgressDialog pdSpinner = null;
    private final Context mContext;

        public PaleDJFTPTask(Context context,Song event){

            this.mContext = context;
            this.mediaData = event;

            if (pdSpinner != null){
                onPreExecute();
            }

        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdSpinner = new ProgressDialog(mContext);
        pdSpinner.setIndeterminate(true);
        pdSpinner.setTitle("PaleDj");
        pdSpinner.setMessage("Loading Media");
        pdSpinner.setCancelable(false);
        pdSpinner.show();




        paleDjClient = new FTPClient();
        paleDjClient.setBufferSize(1024*1024);

    }


    @Override
    protected FTPFile[] doInBackground(Song... params) {
        try {
            paleDjClient.connect("ftp.devtechnician.net",9021);
            boolean status = paleDjClient.login("paledj","5689hhhu79lv45");
            paleDjClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            paleDjClient.enterLocalPassiveMode();
            //mediaData = params[0];
            switch (Action.valueOf(mediaData.ACTION)){
                case FILELIST:
                    FTPFile[]  fileList = paleDjClient.listFiles();
                    return fileList;

                case DELETE:
                    deleteFile(mediaData.FILENAME);
                    return null;

                case DOWNLOAD:
                    downloadFile(mediaData.FILENAME);
                    return null;

                case UPLOAD:
                    uploadFile(mediaData.FILENAME,mediaData.FILEPATH);
                    return  null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    };


    private  void downloadFile(String fileName){
        //check for existing folder if not create one
        //use exsting media folder if one exists
        //need to check out internal storage also
        //setup progress bar for file transfers

        try {
            String path = mediaData.FILEPATH + "/"+fileName;
            FileOutputStream fileOutputStream = new FileOutputStream(path);



            CopyStreamAdapter streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

                    publishProgress((int)streamSize,(int)totalBytesTransferred);
                }

            };
            paleDjClient.setCopyStreamListener(streamListener);

            paleDjClient.retrieveFile(fileName,fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //setup progress bar for file transfers
    private void uploadFile(String fileName, String sourceFilePath){

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFilePath);
            final long mediaFileSize = (long)fileInputStream.available();


            CopyStreamAdapter streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {


                    int percent = ((int)totalBytesTransferred/(int)mediaFileSize)*100;

                    publishProgress((int)mediaFileSize,(int)totalBytesTransferred);
                }

            };
            paleDjClient.setCopyStreamListener(streamListener);
            paleDjClient.storeFile(fileName, fileInputStream);
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void deleteFile(String fileName){

        try {
            paleDjClient.deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("deleted", fileName);
    }



    @Override
    protected void onProgressUpdate(Integer... values) {

        if(pdSpinner.isShowing()!= true){
            pdSpinner.show();
        }
        pdSpinner.setTitle(mediaData.ACTION+"ING");
        pdSpinner.setMessage(mediaData.FILENAME+
                System.getProperty("line.separator")
                +String.valueOf(values[1]+" / "+String.valueOf(values[0])));
        //mediaData.PROGRESSTEXT.setText(String.valueOf(values[1]+" / "+String.valueOf(mediaFileSize)));

    }

    @Override
    protected void onPostExecute(FTPFile[] ftpFiles) {

        if(pdSpinner.isShowing()){
            pdSpinner.dismiss();

        }
        pdSpinner = null;

        if(mediaData.ACTION == "FILELIST"){
           BusProvider.getInstance().post(ftpFiles);
            }


        if(mediaData.ACTION == "DOWNLOAD"){

            pdSpinner.setMessage("Download Complete");
            BusProvider.getInstance().post(new File(Environment.getExternalStoragePublicDirectory("music"),mediaData.FILENAME));

        }
        if (mediaData.ACTION == "DELETE"){
            //mediaListAdapter.remove(mediaListAdapter.getItem(listItemPosition));
            BusProvider.getInstance().post(new Song("FILELIST"));
            pdSpinner.setMessage("File Deleted");

        }
        if (mediaData.ACTION == "UPLOAD"){

            BusProvider.getInstance().post(new Song("FILELIST"));
            pdSpinner.setMessage("Upload Complete");

        }

        try {
            paleDjClient.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }


}
}

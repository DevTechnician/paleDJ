package com.devtechnician.paledj.Bus_Objects;

/**
 * Created by Jason on 6/23/13.
 */
public class Song {
    public String FILEPATH;
    public String FILENAME;
    public String ACTION;
    public int FILESIZE;


    public Song(String action, String filePath, String fileName){
        this.FILENAME = fileName;
        this.FILEPATH = filePath;
        this.ACTION = action;

    }

    public Song(String action, String filePath, String fileName,int fileSize){
        this.FILENAME = fileName;
        this.FILEPATH = filePath;
        this.ACTION = action;
        this.FILESIZE = fileSize;
    }

    public Song(String action){
        this.ACTION = action;
    }
}

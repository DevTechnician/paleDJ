package com.devtechnician.paledj.Bus_Objects;

/**
 * Created by Jason on 9/12/13.
 */
public class Play_Media {
    public String FILEPATH;
    public String FILENAME;
    public int ID;

    public Play_Media(String name, String path){
        FILENAME =name;
        FILEPATH = path;
    }

    public Play_Media(String name, String path, int Id){
        FILENAME =name;
        FILEPATH = path;
        ID = Id;
    }
}

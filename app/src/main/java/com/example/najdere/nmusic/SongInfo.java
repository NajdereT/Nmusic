package com.example.najdere.nmusic;

import android.net.Uri;

public class SongInfo {
    private long id;
    private String title;
    private String artist;
    private long duration;
    private String art;
    private Uri albumart;






    public SongInfo(long songID, String songTitle, String songArtist, long songDuration,String songArt,Uri albumArt) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        duration=songDuration;
        art=songArt;
        albumart=albumArt;
    }
    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public long getDuration(){return duration;}
    public String getArt(){return art;}
    public Uri getAlbumartArt(){return albumart;}






}

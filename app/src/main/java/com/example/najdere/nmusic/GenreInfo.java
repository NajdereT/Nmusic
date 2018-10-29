package com.example.najdere.nmusic;

import android.net.Uri;

public class GenreInfo {

    private String genrename;
    private String genreid;
    private Uri genreart;




    public GenreInfo(String GenreName, String GenreId, Uri GenreArt){

        genrename=GenreName;
        genreid=GenreId;
        genreart=GenreArt;
    }
    public String getGenrename(){return genrename;}
    public String getGenreid(){return genreid;}
    public Uri getGenreart(){return genreart;}
}

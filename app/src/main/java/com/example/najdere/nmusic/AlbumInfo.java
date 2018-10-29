package com.example.najdere.nmusic;

public class AlbumInfo {
    private String albumname;
    private String albumartist;
    private String albumart;
    private String albumcontent;
    private String albumcontentnum;





    public AlbumInfo(String AlbumName, String AlbumArtist, String AlbumArt, String AlbumContent,String AlbumContentnum){

        albumname=AlbumName;
        albumartist=AlbumArtist;
        albumart=AlbumArt;
        albumcontent=AlbumContent;
        albumcontentnum=AlbumContentnum;

    }

    public String getAlbumname(){return albumname;}
    public String getAlbumartist(){return albumartist;}
    public String getAlbumart(){return albumart;}
    public String getAlbumcontent(){return albumcontent;}
    public String getAlbumcontentnum(){return albumcontentnum;}





}

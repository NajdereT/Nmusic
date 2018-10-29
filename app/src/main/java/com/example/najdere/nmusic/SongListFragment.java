package com.example.najdere.nmusic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class SongListFragment extends Fragment{
    private ListView songView;
    private ArrayList<SongInfo> songList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.songlist_libary, container, false);










        return rootView;

    }



}









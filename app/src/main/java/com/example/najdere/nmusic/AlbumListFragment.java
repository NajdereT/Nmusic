package com.example.najdere.nmusic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.wonderkiln.blurkit.BlurLayout;

import java.util.ArrayList;

public class AlbumListFragment extends Fragment {

    private RecyclerView albumrecycler;
    private GridView albumsgrid;
    private ArrayList<AlbumInfo> albumList;
    private BlurLayout blurLayout;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.albumlist_libary, container, false);





        return rootView;



    }

}

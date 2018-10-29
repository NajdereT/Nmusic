package com.example.najdere.nmusic.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.example.najdere.nmusic.R;


public class MusicController extends MediaController {
    private LayoutInflater songInf;

    public MusicController(Context context) {



        super(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        //map to song layout
        @SuppressLint("ViewHolder") View songLay = (View) songInf.inflate
                (R.layout.nnabig, parent, false);
        return songLay;
    }
    public void hide(){}

}

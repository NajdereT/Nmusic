package com.example.najdere.nmusic;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class SongAdapter extends BaseAdapter {
    private float movement = 150;
    private Context mcontext;

    private ArrayList<SongInfo> songs;

    private int lastPosition = -1;

    private LayoutInflater songInf;

    public SongAdapter(Context c, ArrayList<SongInfo> theSongs) {
        songs = theSongs;
        songInf = LayoutInflater.from(c);
        this.mcontext = c;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //map to song layout
        @SuppressLint("ViewHolder") View songLay = (View) songInf.inflate
                (R.layout.song_row, parent, false);
        //get title and artist views


        TextView songView = (TextView) songLay.findViewById(R.id.tvSong);
        TextView artistView = (TextView) songLay.findViewById(R.id.tvArtist);
        TextView durationView = (TextView) songLay.findViewById(R.id.tvSongDurationRow);
        TextView textView = (TextView) songLay.findViewById(R.id.textView5);



        //get song using position

        SongInfo currSong = songs.get(position);


        durationView.setText(currSong.getArt());
        textView.setText(currSong.getArt());






        //get title and artist strings


        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(currSong.getDuration()))),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(String.valueOf(currSong.getDuration()))) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(currSong.getDuration()))))
        );

        songView.setText(currSong.getTitle());
        durationView.setText(time);
        artistView.setText(currSong.getArtist());


        //set position as tag
        songLay.setTag(position);

        Animation animation = AnimationUtils.loadAnimation(songLay.getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        songLay.startAnimation(animation);
        lastPosition = position;

        return songLay;
    }


}

package com.example.najdere.nmusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class GenreAdapter extends BaseAdapter {


    private ArrayList<GenreInfo> genres;

    private int lastPosition = -1;
    private Context mcontext;

    private LayoutInflater genreInf;

    public GenreAdapter(Context c, ArrayList<GenreInfo> theGenres){
        genres=theGenres;
        genreInf=LayoutInflater.from(c);
        this.mcontext=c;


    }

    @Override
    public int getCount() {
        return genres.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        @SuppressLint("ViewHolder") View genreLay = (View)genreInf.inflate
                (R.layout.genre_row, parent, false);
        //get title and artist views

        TextView GenreView = (TextView)genreLay.findViewById(R.id.tvGenreName);
        TextView GenreIdView = (TextView)genreLay.findViewById(R.id.tvGenreId);
        ImageView GenreArtView = (ImageView)genreLay.findViewById(R.id.ivAlbumArt);

        GenreInfo currGenre= genres.get(position);


        GenreView.setText(currGenre.getGenrename());
        GenreIdView.setText(currGenre.getGenreid());






        genreLay.setTag(position);

        Animation animation = AnimationUtils.loadAnimation(genreLay.getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        genreLay.startAnimation(animation);
        lastPosition = position;


        return genreLay;
    }
}

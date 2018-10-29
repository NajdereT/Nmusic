package com.example.najdere.nmusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlbumAdapter  extends BaseAdapter {
    Context context;


    private ArrayList<AlbumInfo> albums;

    private int lastPosition = -1;

    private LayoutInflater albumInf;

    public AlbumAdapter(Context c, ArrayList<AlbumInfo> theAlbums){
        albums=theAlbums;
        albumInf=LayoutInflater.from(c);

    }


    @Override
    public int getCount() {
        return albums.size();
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


        @SuppressLint("ViewHolder") View albumLay = (View)albumInf.inflate
                (R.layout.album_row, parent, false);
        //get title and artist views

        TextView AlbumView = (TextView)albumLay.findViewById(R.id.tvAlbumName);
        TextView AlbumArtistView = (TextView)albumLay.findViewById(R.id.tvAlbumArtist);
        ImageView AlbumArtView = (ImageView)albumLay.findViewById(R.id.ivAlbumArt) ;
        TextView AlbumContentView = (TextView)albumLay.findViewById(R.id.tvAlbumContent);


        final AlbumInfo currAlbum= albums.get(position);



        AlbumArtView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(v.getContext(),
                       "number of songs in album: "+ currAlbum.getAlbumcontentnum(), Toast.LENGTH_SHORT).show();

            }
        });
        if (currAlbum.getAlbumcontent() != null){
            AlbumContentView.setText("year: "+currAlbum.getAlbumcontent());
        }

        AlbumView.setText(currAlbum.getAlbumname());
        AlbumArtistView.setText(currAlbum.getAlbumartist());
        Bitmap bm= BitmapFactory.decodeFile(currAlbum.getAlbumart());



        if (currAlbum.getAlbumart() != null){
            AlbumArtView.setImageBitmap(bm);
        }

        albumLay.setTag(position);

        Animation animation = AnimationUtils.loadAnimation(albumLay.getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        albumLay.startAnimation(animation);
        lastPosition = position;

        return albumLay;
    }
}

package com.example.najdere.nmusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.najdere.nmusic.controller.MusicController;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.nvanbenschoten.motion.ParallaxImageView;

import info.abdolahi.CircularMusicProgressBar;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl{

    private static final int NUM_PAGES = 4;
    Color color;
    private ListView songView;
    private ArrayList<SongInfo> songList;
    private MusicService musicSrv;
    private Intent playIntent;
    private ArrayList<GenreInfo> genreList;
    private GridView genregrid;

    private SeekBar mSeekBar;
    private Runnable runnable;
    private Handler handler;

    private GridView albumsgrid;
    private ArrayList<AlbumInfo> albumList;
    private CircularMusicProgressBar circularMusicProgressBar;

    private int songPosn;
    public String songTitle="";
    public String songArtist="";
    private  TextView songtitletextView;
    private BarVisualizer barVisualizer;
    private Handler mHandler = new Handler();



    private MusicController controller;
    private boolean musicBound = false;
    private boolean paused = false, playbackPaused = false;
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list

            musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };




    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);




                return;
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2);




                return;
            }

        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }





        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle("NMusic");
        ParallaxImageView mBackground = findViewById(R.id.bg);
        mBackground.registerSensorManager();

        View v = (View) findViewById(R.id.inculde_song);





        songtitletextView = findViewById(R.id.textView7);

        songtitletextView.setSelected(true);









        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        int backgroundColor = ContextCompat.getColor(this, R.color.transfull);






        ImageView  nextimg = (ImageView ) findViewById(R.id.imageView_next);

        nextimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });

        ImageView  previmg = (ImageView ) findViewById(R.id.imageView_prev);

        previmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        final ImageView playimg = (ImageView ) findViewById(R.id.imageView_play);

        playimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView playimg = (ImageView ) findViewById(R.id.imageView_play);
                if (musicSrv.player.isPlaying()) {
                    musicSrv.player.pause();
                    playimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_48dp));

                } else {
                    musicSrv.player.start();
                    playimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_48dp));

                }


            }
        });






        songView = findViewById(R.id.listview);
        songList = new ArrayList<SongInfo>();
        SongAdapter songAdt = new SongAdapter(this, songList);
        songView.setAdapter(songAdt);
        getSongList();



        songView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if ( view.hasFocus()){
                    view.clearFocus(); //we can put it inside the second if as well, but it makes sense to do it to all scraped views
                    //Optional: also hide keyboard in that case
                    if ( view instanceof EditText) {
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });


        Collections.sort(songList, new Comparator<SongInfo>() {
            public int compare(SongInfo a, SongInfo b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });


        albumsgrid =findViewById(R.id.gridview);
        albumList = new ArrayList<AlbumInfo>();
        AlbumAdapter albumAdt = new AlbumAdapter(this, albumList);
        albumsgrid.setAdapter(albumAdt);
        getAlbumList();








        final int textcolor = ContextCompat.getColor(this, R.color.colorPrimary);
        final int textcolorchecked = ContextCompat.getColor(this, R.color.cardview_dark_background);

        Collections.sort(albumList, new Comparator<AlbumInfo>() {
            public int compare(AlbumInfo a, AlbumInfo b) {
                return a.getAlbumname().compareTo(b.getAlbumname());
            }
        });

        genregrid = findViewById(R.id.gridview_genre);
        genreList = new ArrayList<GenreInfo>();
        GenreAdapter genreAdt = new GenreAdapter(this, genreList);
        genregrid.setAdapter(genreAdt);
        getGenreList();


        Collections.sort(genreList, new Comparator<GenreInfo>() {
            public int compare(GenreInfo a, GenreInfo b) {
                return a.getGenrename().compareTo(b.getGenrename());
            }
        });

        ImageView shuffleimage = (ImageView) findViewById(R.id.imageView_shuffle);
        shuffleimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.setShufflefalse();
                Toast.makeText(MainActivity.this,
                        "shuffle enabled", Toast.LENGTH_SHORT).show();

            }
        });




    }




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            View includesongView = (View) findViewById(R.id.inculde_song);
            View includealbumView = (View) findViewById(R.id.include_album);
            View includegenreView = (View) findViewById(R.id.include_genre);
            View includeEqView = (View) findViewById(R.id.include_equalizer);


            switch (item.getItemId()) {
                case R.id.action_music:
                    includesongView.setVisibility(View.VISIBLE);
                    includealbumView.setVisibility(View.GONE);
                    includegenreView.setVisibility(View.GONE);
                    includeEqView.setVisibility(View.GONE);



                    return true;
                case R.id.action_equalizer:
                    includesongView.setVisibility(View.GONE);
                    includealbumView.setVisibility(View.VISIBLE);
                    includegenreView.setVisibility(View.GONE);
                    includeEqView.setVisibility(View.GONE);


                    return true;
                case R.id.action_search:
                    includesongView.setVisibility(View.GONE);
                    includealbumView.setVisibility(View.GONE);
                    includegenreView.setVisibility(View.VISIBLE);
                    includeEqView.setVisibility(View.GONE);

                    return true;


                case R.id.action_background:
                    includesongView.setVisibility(View.GONE);
                    includealbumView.setVisibility(View.GONE);
                    includegenreView.setVisibility(View.GONE);
                    includeEqView.setVisibility(View.VISIBLE);


                    return true;


            }
            return false;
        }
    };
    public void getSongList() {

        //retrieve song info


        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = getContentResolver().query(musicUri, null, null, null, null);


        if (musicCursor != null && musicCursor.moveToFirst()) ;
        {

                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int duration = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.DURATION);

                int artistAlbum = musicCursor.getColumnIndex
                        (MediaStore.Audio.Media.ALBUM);

                int album_Column = musicCursor.getColumnIndex
                       (MediaStore.Audio.Media.ALBUM_ID);


            //add songs to list
                do {

                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    long thisDuration = musicCursor.getLong(duration);
                    String thisAlbum = musicCursor.getString(artistAlbum);
                    String songAlbum  = musicCursor.getString(album_Column);

                    long parseLong = Long.parseLong(songAlbum);
                    final Uri sArtWorkUri =  Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtWorkUri, parseLong);






                    if (thisDuration > 10000) {

                        songList.add(new SongInfo(thisId, thisTitle, thisArtist, thisDuration, thisAlbum,uri));
                        }

                } while (musicCursor.moveToNext());


            }

        }

    public void getAlbumList() {

        //retrieve song info

        ContentResolver musicResolver = getContentResolver();

        Uri musicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);


        if (musicCursor != null && musicCursor.moveToFirst());

        {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM);

            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Albums.ARTIST);

            int artistAlbum = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART);


            int contentAlbum = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.FIRST_YEAR);

            int contentAlbumnum = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.NUMBER_OF_SONGS);


            //add songs to list
            do {


                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbumart = musicCursor.getString(artistAlbum);
                String thisAlbumcontent = musicCursor.getString(contentAlbum);
                String thisAlbumnum = musicCursor.getString(contentAlbumnum);



                albumList.add(new AlbumInfo(thisTitle, thisArtist, thisAlbumart,thisAlbumcontent,thisAlbumnum));

            } while (musicCursor.moveToNext());


        }

    }
    public void getGenreList() {

        //retrieve song info
        ContentResolver musicResolver = getContentResolver();

        Uri musicUri = android.provider.MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst());

        {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Genres._ID);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Genres.NAME);
            int artist = musicCursor.getColumnIndex
                    (MediaStore.Audio.Genres._ID);


            //add songs to list
            do {
                String songAlbum  = musicCursor.getString(artist);

                long parseLong = Long.parseLong(songAlbum);
                final Uri sArtWorkUri =  Uri.parse("content://media/external/audio/albumart");
                Uri uri = ContentUris.withAppendedId(sArtWorkUri, parseLong);


                String thisGenreName = musicCursor.getString(idColumn);
                String thisGenreId = musicCursor.getString(titleColumn);

                genreList.add(new GenreInfo(thisGenreName, thisGenreId,uri));

            } while (musicCursor.moveToNext());


        }

    }
    //play next
    private  void setmSeekBar(){
        mSeekBar= (SeekBar)findViewById(R.id.seekBar);

        mSeekBar.setMax(musicSrv.player.getDuration());

        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(musicSrv != null){
                    int mCurrentPosition = musicSrv.player.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(musicSrv.player != null && fromUser){
                    musicSrv.player.seekTo(progress * 1000);
                }
            }
        });

    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    private void playNext() {
        musicSrv.playNext();
        if (playbackPaused) {
            playbackPaused = false;
        }
        String songTitle = musicSrv.songTitle;
        songtitletextView = findViewById(R.id.textView7);
        songtitletextView.setText(songTitle);

        String arm = musicSrv.songArtist;
        TextView songartisttextView = findViewById(R.id.textView8);
        songartisttextView.setText(arm);

        ImageView circleImageView = (ImageView) findViewById(R.id.Circleimageview);
        circleImageView.scrollTo(1,1);

        circleImageView.setVerticalFadingEdgeEnabled(true);

        circleImageView.setHorizontalFadingEdgeEnabled(true);
        circleImageView.setFadingEdgeLength(250);
           Glide.with(this)
                   .load(musicSrv.albumart)
                   .apply(new RequestOptions()
                           .placeholder(R.drawable.nmusic_logo_niew))
                   .into(circleImageView);



        ImageView playimg = (ImageView ) findViewById(R.id.imageView_play);
        playimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_48dp));


    }

    //play previous
    private void playPrev() {


        musicSrv.playPrev();
        if (playbackPaused) {
            playbackPaused = false;
        }
        String songTitle = musicSrv.songTitle;
        songtitletextView = findViewById(R.id.textView7);
        songtitletextView.setText(songTitle);


        String arm = musicSrv.songArtist;
        TextView songartisttextView = findViewById(R.id.textView8);
        songartisttextView.setText(arm);

        ImageView circleImageView = (ImageView)findViewById(R.id.Circleimageview);
        circleImageView.scrollTo(1,1);
        circleImageView.setVerticalFadingEdgeEnabled(true);

        circleImageView.setHorizontalFadingEdgeEnabled(true);
        circleImageView.setFadingEdgeLength(250);

        Glide.with(this)
                .load(musicSrv.albumart)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.nmusic_logo_niew))
                .into(circleImageView);


        ImageView playimg = (ImageView ) findViewById(R.id.imageView_play);
        playimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_48dp));

    }


    public void songPicked(View view) {
        setmSeekBar();
        AppBarLayout collapsingToolbarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbarLayout.setExpanded(true);









        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
        if (playbackPaused) {
            playbackPaused = false;
        }


        String songTitle = musicSrv.songTitle;
        songtitletextView = findViewById(R.id.textView7);
        songtitletextView.setText(songTitle);


        String arm = musicSrv.songArtist;
        TextView songartisttextView = findViewById(R.id.textView8);
        songartisttextView.setText(arm);

        ImageView circleImageView = (ImageView)findViewById(R.id.Circleimageview);
        circleImageView.scrollTo(1,1);
        circleImageView.setVerticalFadingEdgeEnabled(true);

        circleImageView.setHorizontalFadingEdgeEnabled(true);
        circleImageView.setFadingEdgeLength(250);
        Glide.with(this)
                .load(musicSrv.albumart)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.nmusic_logo_niew))
                .into(circleImageView);





        ImageView playimg = (ImageView ) findViewById(R.id.imageView_play);
        playimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_48dp));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tool, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_end:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onStart() {
        super.onStart();



        if (playIntent == null) {


            playIntent = new Intent(this, MusicService.class);

            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    public void start() {
        setmSeekBar();






        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();

    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
        }

    }

    @Override
    protected void onStop() {


        super.onStop();
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();



        ImageView imageViewnextnotif =(ImageView)findViewById(R.id.imageViewNext);
        imageViewnextnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        });
        return false;
    }
    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        if (barVisualizer != null)
            barVisualizer.release();
        super.onDestroy();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}




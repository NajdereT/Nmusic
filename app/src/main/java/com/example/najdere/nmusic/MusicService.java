package com.example.najdere.nmusic;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.app.NotificationCompat.MediaStyle;

import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import android.app.PendingIntent;
import android.widget.RemoteViews;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_NEXT;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE;
import static com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PREVIOUS;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();
    //media player
    public MediaPlayer player;
    private static final String CHANNEL_ID = "media_playback_channel";

    //song list
    private ArrayList<SongInfo> songs;
    //current position
    private int songPosn;
    private int notificationId;
    public String songTitle="";
    public String songArtist="";
    public Uri albumart= Uri.parse("");
    private MusicService musicSrv;

    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;
    public static final int requestCode = 1;
    SimpleExoPlayer simpleExoPlayer;
    private Context mcontext;
    Bitmap mBitmap;






    @Nullable
    @Override

    public void onCreate(){
        //create the service
        //initialize position
        mBitmap= null;

        songPosn=0;
//create player
        player = new MediaPlayer();
        initMusicPlayer();
        rand=new Random();



    }


    public void initMusicPlayer(){




        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    public void setShufflefalse(){
        if(shuffle) shuffle=true;
        else shuffle=false;
    }



    public void playSong(){
        player.reset();

         //get song
        SongInfo playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
        songArtist=playSong.getArtist();
        albumart=playSong.getAlbumartArt();



        //get id
        long currSong = playSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        //play a song
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);

        }
        player.prepareAsync();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        int duration = player.getDuration();
        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );





        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.nna);
        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.nnabig);
        notificationLayout.setImageViewUri(R.id.imageView7, albumart);

        notificationLayout.setTextViewText(R.id.textView, songTitle);
        notificationLayout.setTextViewText(R.id.tvSongduration, time);

        notificationLayout.setTextViewText(R.id.textView2, songArtist);
        notificationLayout.setTextViewText(R.id.Title, "Nmusic player");

        notificationLayout.setImageViewResource(R.id.imageViewPrev, R.drawable.ic_skip_previous_black_48dp);

        notificationLayout.setImageViewResource(R.id.imageViewNext, R.drawable.ic_skip_next_black_48dp);


        notificationLayout.setImageViewResource(R.id.imageViewStart, R.drawable.ic_play_arrow_black_48dp);




        try {
            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), albumart);
        } catch (IOException e) {
            mBitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.mipmap.ic_launcher);
        }

        final MediaSession mediaSession = new MediaSession(this, "debug tag");
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadata.Builder()
                .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, mBitmap)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, songArtist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "")
                .putString(MediaMetadata.METADATA_KEY_TITLE, songTitle)
                .build());
        // Indicate you're ready to receive media commands
        mediaSession.setActive(true);
        // Attach a new Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSession.Callback() {

            // Implement your callbacks

        });
        // Indicate you want to receive transport controls via your Callback
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);


        createNotificationChannel();

        @SuppressLint("WrongConstant") NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)



                .setSmallIcon(R.drawable.musiciconblack)
                .setContentTitle(songTitle)
                .setContentText(songArtist)
                .setLargeIcon(mBitmap)
                .setColorized(true)
                .setStyle(new MediaStyle()
                        .setMediaSession(MediaSessionCompat.Token.fromToken(mediaSession.getSessionToken()))

                        .setShowActionsInCompactView(0, 1, 2))
                .addAction(R.drawable.exo_icon_previous, "prev", retreivePlaybackAction(3))
                .addAction(R.drawable.exo_icon_pause, "pause", retreivePlaybackAction(1))
                .addAction(R.drawable.exo_icon_next, "next", retreivePlaybackAction(2))





                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());

// notificationId is a unique int for each notification that you must define

// notificationId is a unique int for each notification that you must define

    }
    private PendingIntent retreivePlaybackAction(int which) {
        Intent action;
        PendingIntent pendingIntent;
        final ComponentName serviceName = new ComponentName(this, MusicService.class);
        switch (which) {
            case 1:
                // Play and pause
                action = new Intent(ACTION_PAUSE);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 1, action, 0);
                return pendingIntent;
            case 2:
                // Skip tracks

                action = new Intent(ACTION_NEXT);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 2, action, 0);
                return pendingIntent;
            case 3:
                // Previous tracks
                action = new Intent(ACTION_PREVIOUS);
                action.setComponent(serviceName);
                pendingIntent = PendingIntent.getService(this, 3, action, 0);
                return pendingIntent;
            default:
                break;
        }
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }
    public void setList(ArrayList<SongInfo> theSongs){
        songs=theSongs;
    }




    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }



    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){

        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }


    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }
    //skip to next
    public void playNext() {
        if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        } else {
            songPosn++;
            if ( songPosn >=songs.size())songPosn = 0;
        }
        playSong();
    }
}

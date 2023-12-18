package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.gauravk.audiovisualizer.visualizer.BlastVisualizer;
import java.util.ArrayList;
import java.util.Random;
//player class play songs and execute buttons of each song view.
public class Player extends AppCompatActivity {
    Button play_btn, next_btn, previous_btn, forward_btn, repeat_btn, rewind_btn, shuffle_btn;
    TextView name, start, stop;
    SeekBar seek_music;
    BlastVisualizer visualizer;
    String SongName;
    Boolean repeated = true, shuffled = false;//for checking to repeat or shuffle the song
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<song> mySongs = MainActivity.mSongsList;//list of all songs to shuffle randomly
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player2);
        previous_btn = findViewById(R.id.prevbtn);
        next_btn = findViewById(R.id.nextbtn);
        play_btn = findViewById(R.id.playbtn);
        forward_btn = findViewById(R.id.fastforbtn);
        rewind_btn = findViewById(R.id.fastrewbtn);
        repeat_btn = findViewById(R.id.repeat);
        shuffle_btn = findViewById(R.id.shuffle);
        name = findViewById(R.id.songname);
        start = findViewById(R.id.Sstart);//start and stop of seekbar
        stop = findViewById(R.id.Sstop);
        seek_music = findViewById(R.id.seekbar);
        visualizer = findViewById(R.id.blast);//audio visualizer
        Random r = new Random();
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        //getting song position in the list
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        position = bundle.getInt("pos", 0);
        name.setSelected(true);
        //playing the song
        Uri uri = Uri.parse(mySongs.get(position).getData());
        SongName = mySongs.get(position).getName().replace(".mp3","");
        name.setText(SongName);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        seek_music.setMax(mediaPlayer.getDuration());
        //changing seekbar based on screen touches.
        seek_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        //changing seekbar color;
        seek_music.getProgressDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        seek_music.getThumb().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
        //setting start and end time of seekbar and changing it each 500ms;
        final Handler handler = new Handler();
        final int delay = 500;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String endTime = creat_time(mediaPlayer.getDuration());
                stop.setText(endTime);
                String current = creat_time(mediaPlayer.getCurrentPosition());
                start.setText(current);
                int currentPosition = mediaPlayer.getCurrentPosition();
                seek_music.setProgress(currentPosition);
                handler.postDelayed(this, delay);
            }
        }, delay);
        //play and pause button;
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pausing;
                if(mediaPlayer.isPlaying()){
                    play_btn.setBackgroundResource(R.drawable.ic__play);
                    mediaPlayer.pause();
                }
                //playing
                else{
                    play_btn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    //when song complete, repeat or not:
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if(repeated)
                                play_btn.performClick();
                            else
                                next_btn.performClick();
                        }
                    });
                }
            }
        });
        //starting audio visualizer
         int audio_id = mediaPlayer.getAudioSessionId();
         if(audio_id != -1){
             visualizer.setAudioSessionId(audio_id);
         }
         //next button performance;
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                //going to next or random song based on shuffled situation
                if(shuffled)
                    position = ((r.nextInt(mySongs.size()-0))%mySongs.size());
                else
                    position = ((position+1)%mySongs.size());
                //playing next song
                Uri u = Uri.parse(mySongs.get(position).getData());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                SongName = mySongs.get(position).getName();
                name.setText(SongName);
                mediaPlayer.start();
                play_btn.setBackgroundResource(R.drawable.ic_pause);
                seek_music.setMax(mediaPlayer.getDuration());
                //start_animation(imageView);
                int audio_id = mediaPlayer.getAudioSessionId();
                if(audio_id != -1){
                    visualizer.setAudioSessionId(audio_id);
                }
                //repeat or not when song is complete;
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if(repeated)
                            play_btn.performClick();
                        else
                            next_btn.performClick();
                    }
                });
            }
        });
         //previous button performance;
        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                //going to previous song if possible; otherwise going to last song
                // (if it was the first song).
                position = ((position-1)<0)?(mySongs.size()-1):(position-1);
                //playing previous song:
                Uri u = Uri.parse(mySongs.get(position).getData());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                SongName = mySongs.get(position).getName();
                name.setText(SongName);
                mediaPlayer.start();
                play_btn.setBackgroundResource(R.drawable.ic_pause);
                seek_music.setMax(mediaPlayer.getDuration());
                //start_animation(imageView);
                int audio_id = mediaPlayer.getAudioSessionId();
                if(audio_id != -1){
                    visualizer.setAudioSessionId(audio_id);
                }
            }
        });
        //forward button, going 15 seconds forward in song;
        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+15000);
                }
            }
        });
        //rewind button, going 15 seconds before in song;
        rewind_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-15000);
                }
            }
        });
        //repeating button performance;
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeated){
                    repeated = false;
                    repeat_btn.setBackgroundResource(R.drawable.ic_arrow_forward);
                }else{
                    repeated = true;
                    repeat_btn.setBackgroundResource(R.drawable.ic_repeat);
                }
            }
        });
        //shuffle button performance;
        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffled){
                    shuffled = false;
                    shuffle_btn.setBackgroundResource(R.drawable.ic_shuffle_off);
                }else{
                    shuffled = true;
                    shuffle_btn.setBackgroundResource(R.drawable.ic_shuffle);
                }
            }
        });
        //when song is completed;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(repeated)
                    play_btn.performClick();
                else
                    next_btn.performClick();
            }
        });
    }
    //back option;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    //stopping audio visualizer;
    @Override
    protected void onDestroy() {
        if(visualizer != null){
            visualizer.release();
        }
        super.onDestroy();
    }
    //converting time in milli second to needed string for start and end time in seekbar.
    public String creat_time(int duration){
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time += min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}
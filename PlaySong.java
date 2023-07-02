package com.example.melody;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int currentIndex =0;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        ArrayList<Integer> songs = new ArrayList<>();
        songs.add(0, R.raw.aisalagtahai);
        songs.add(1, R.raw.dekhaekhwab);
        songs.add(2, R.raw.mehndilagakrakhna);
        songs.add(3, R.raw.pehlanasha);
        songs.add(4, R.raw.testing);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(mediaPlayer !=null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                }
                else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                seekBar.setMax(mediaPlayer.getDuration());
                updateSeek = new Thread(){
                    @Override
                    public void run() {
                        currentIndex=0;
                        try{
                            while (currentIndex<mediaPlayer.getDuration()){
                                currentIndex= mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(currentIndex);
                                sleep(800);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                };
                updateSeek.start();
            }
        });
         next.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(mediaPlayer!=null){
                     play.setImageResource(R.drawable.pause);
             }

                 if(currentIndex<songs.size()-1){
                     currentIndex++;
                 }
                 else {
                     currentIndex=0;
                 }

                 if(mediaPlayer.isPlaying()){
                     mediaPlayer.stop();
                 }
                 mediaPlayer=MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
                 mediaPlayer.start();
                 play.setImageResource(R.drawable.pause);
                 seekBar.setMax(mediaPlayer.getDuration());
                 textContent= songs.get(currentIndex).toString();
                 textView.setText(textContent);
             }
         });
         previous.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 mediaPlayer.stop();
                 mediaPlayer.release();
                 if (mediaPlayer != null) {
                     play.setImageResource(R.drawable.pause);
                 }

                 if (currentIndex > 0) {
                     currentIndex--;
                 } else {
                     currentIndex = songs.size() - 1;
                 }

                 if (mediaPlayer.isPlaying()) {
                     mediaPlayer.stop();
                 }
                 mediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
                 mediaPlayer.start();
                 play.setImageResource(R.drawable.pause);
                 seekBar.setMax(mediaPlayer.getDuration());
                 textContent= songs.get(currentIndex).toString();
                 textView.setText(textContent);

             }
         });

         new Thread(new Runnable() {
             @Override
             public void run() {
                 while (mediaPlayer!=null) {
                     try {
                         if (mediaPlayer.isPlaying()) {
                             Message message = new Message();
                             message.what = mediaPlayer.getCurrentPosition();
                             handler.sendMessage(message);
                             Thread.sleep(1000);
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             }
         }).start();
    }
    @SuppressLint("Handler Leak") Handler handler=new Handler(){
        @Override
        public void handleMessage (Message msg){
            seekBar.setProgress(msg.what);
        }



        };
    }









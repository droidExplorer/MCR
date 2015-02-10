/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webmyne.myclassroom;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MyPlayerActivity extends Activity implements OnInfoListener, OnBufferingUpdateListener {


    private String path;
    private Uri uri;
    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    long millies = 0;
    private int currentIndex = 0;
    private boolean isSeeked = false;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.videobuffer);
        mVideoView = (VideoView) findViewById(R.id.buffer);
        pb = (ProgressBar) findViewById(R.id.probar);
        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);

        playVideo(currentIndex);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.stopPlayback();

                if (currentIndex > 3) {
                } else {
                    playVideo(currentIndex + 1);
                }

            }
        });
    }

    private void playVideo(final int currentIndex) {

        path = HomeActivity.currentPrograms.get(currentIndex).url;
        millies = 0;


        if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(
                    MyPlayerActivity.this,
                    "Please edit VideoBuffer Activity, and set path"
                            + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
            return;
        } else {
            
      /*
       * Alternatively,for streaming media you can use
       * mVideoView.setVideoURI(Uri.parse(URLstring));
       */

            uri = Uri.parse(path);
            mVideoView.setVideoURI(uri);
            //  mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                    try {
                        Date date = new Date();
                        String temp = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(date);
                        millies = new SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(temp).getTime() - new SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(HomeActivity.currentPrograms.get(currentIndex).start_time).getTime();
                        mVideoView.seekTo(10000);
                        mVideoView.start();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.e("Millis", "  " + millies);

                }
            });


        }

    }


    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {

            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);
                    
                }
                break;

            case MediaPlayer.MEDIA_INFO_BUFFERING_END:

                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);

                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:

                downloadRateView.setText("" + extra + "kb/s" + "  ");

                break;
        }

        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

}
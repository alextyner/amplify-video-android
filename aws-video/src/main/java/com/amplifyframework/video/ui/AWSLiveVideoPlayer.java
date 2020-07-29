/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.video.ui;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;
import androidx.annotation.NonNull;

import com.amplifyframework.extended.video.resources.live.EgressType;
import com.amplifyframework.extended.video.resources.live.LiveResource;
import com.amplifyframework.extended.video.ui.VideoPlayer;

import java.util.Objects;

/**
 * Manages a video player for a live AWS resource.
 */
public class AWSLiveVideoPlayer extends AWSVideoPlayer {

    private LiveResource liveResource;
    private Handler handler;
    private State currentState = State.IDLE;

    /**
     * Create a new {@link AWSVideoPlayer} composed of a {@link VideoView}.
     *
     * @param videoView The primary {@link VideoView} used by the player.
     */
    public AWSLiveVideoPlayer(@NonNull VideoView videoView) {
        super(videoView);
        handler = new Handler();
        configureListeners();
    }

    /**
     * Configure this video player for a live streaming resource.
     *
     * @param liveResource the {@link LiveResource} to source from.
     */
    public void attach(LiveResource liveResource) {
        this.liveResource = Objects.requireNonNull(liveResource);
        handlePreparing(getDuration());
        connect(liveResource);
    }

    private void connect(LiveResource liveResource) {
        // Try all of the egress types until one isn't null.
        for (EgressType type : EgressType.values()) {
            String egressPoint = liveResource.getEgressPoint(type);
            if (egressPoint != null) {
                setSourceURI(Uri.parse(egressPoint));
                break;
            }
        }
        if (getState() == State.PREPARING) {
            handleReady();
        }
        getVideoView().requestFocus();
        getVideoView().start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveResource getVideoResource() {
        return liveResource;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getVideoView().setOnInfoListener(new LiveInfoListener());
        }
        getVideoView().setOnErrorListener(new LiveErrorListener());
        getVideoView().setOnPreparedListener(new LivePreparedListener());
        getVideoView().setOnTouchListener(new LiveTouchListener());
        getVideoView().setOnCompletionListener(new LiveCompletionListener());
    }

    /**
     * Move the player into a new state; notifies listeners of the change.
     * @param newState new {@link State} to enter.
     */
    private void setState(State newState) {
        if (newState != currentState) {
            currentState = newState;
            handleStateChange(newState);
        }
    }

    /**
     * Begin playback.
     */
    public void play() {
        handlePlay(getCurrentPosition());
        getVideoView().requestFocus();
        getVideoView().start();
    }

    /**
     * Pause playback.
     */
    public void pause() {
        handlePause(getCurrentPosition());
        getVideoView().pause();
    }

    /**
     * Check if the player currently playing.
     * @return true if the player is playing
     */
    public boolean isPlaying() {
        return getVideoView().isPlaying();
    }

    /**
     * To be called when a seek event happens.
     * @param newPosition new playback position
     */
    public void onSeek(int oldPosition, int newPosition) {
        handleSeek(oldPosition, newPosition);
    }

    private State getState() {
        return currentState;
    }

    private void handleStateChange(VideoPlayer.State newState) {
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onStateChange(newState);
            }
        });
    }

    private void handlePreparing(long totalDuration) {
        setState(State.PREPARING);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onPreparing(totalDuration);
            }
        });
    }

    private void handleReady() {
        setState(State.READY);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onReady();
            }
        });
    }

    private void handlePlay(long currentPosition) {
        setState(State.PLAYING);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onPlay(currentPosition);
            }
        });
    }

    private void handlePause(long currentPosition) {
        setState(State.IDLE);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onPause(currentPosition);
            }
        });
    }

    private void handleEnd(long totalDuration) {
        setState(State.ENDED);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onEnd(totalDuration);
            }
        });
    }

    private void handleSeek(long oldPosition, int newPosition) {
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onSeek(oldPosition, newPosition);
            }
        });
    }

    private void handleBufferingStart(long currentPosition) {
        setState(State.BUFFERING);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onBufferingStart(currentPosition);
            }
        });
    }

    private void handleBufferingComplete(long currentPosition) {
        setState(State.PLAYING);
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onBufferingComplete(currentPosition);
            }
        });
    }

    private void handleTouch(MotionEvent event) {
        handler.post(() -> {
            for (Listener l : getListeners()) {
                l.onTouch(event);
            }
        });
    }

    private class LivePreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            handleReady();
        }
    }

    private class LiveErrorListener implements MediaPlayer.OnErrorListener {
        private static final int RETRY_DELAY = 5000;
        private Handler handler = new Handler();
        private Runnable doTryReconnect = () -> {
            Log.d("AMPAPP", "Attempting to connect to the video stream.");
            connect(getVideoResource());
            Log.d("AMPAPP", "Waiting and trying again...");
        };

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            switch (extra) {
                case MediaPlayer.MEDIA_ERROR_IO:
                    if (getState() != State.BUFFERING) {
                        handleBufferingStart(getCurrentPosition());
                    }

                    // Trigger 1 connection re-try event. If it fails, an error will propogate right back
                    // to this point, triggering another re-try event.
                    handler.postDelayed(doTryReconnect, RETRY_DELAY);
                    return true;
                default:
                    return false;
            }
        }
    }

    private class LiveInfoListener implements MediaPlayer.OnInfoListener {
        private static final int RETRY_DELAY = 5000;
        private Handler handler = new Handler();
        private Runnable doTryReconnect = () -> {
            Log.d("AMPAPP", "Attempting to reconnect to the video stream.");
            connect(getVideoResource());
            Log.d("AMPAPP", "Waiting and trying again...");
        };

        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d("AMPAPP", "Buffering started...");

                    if (getState() != State.BUFFERING) {
                        handleBufferingStart(getCurrentPosition());
                    }

                    // Buffering triggers 1 connection re-try event after RETRY_DELAY seconds. If the connection
                    // fails, LiveErrorListener.onError will be called, and another re-try event will be triggered.
                    handler.postDelayed(doTryReconnect, RETRY_DELAY);

                    return true;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d("AMPAPP", "Buffering has stopped, won't try to reconnect.");

                    if (getState() == State.BUFFERING) {
                        handleBufferingComplete(getCurrentPosition());
                    } else if (getState() != State.PLAYING) {
                        handlePlay(getCurrentPosition());
                    }

                    // If the player signals that buffering has stopped, don't try to reconnect.
                    handler.removeCallbacks(doTryReconnect);

                    return true;
                default:
                    return false;
            }
        }
    }

    private class LiveTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            handleTouch(event);
            view.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (getVideoView().isPlaying()) {
                    pause();
                } else {
                    play();
                }
                return true;
            }
            return false;
        }
    }

    private class LiveCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            handleEnd(getDuration());
        }
    }

}
